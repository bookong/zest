package com.github.bookong.zest.runner;

import com.github.bookong.zest.annotation.ZestConnection;
import com.github.bookong.zest.annotation.ZestMongo;
import com.github.bookong.zest.annotation.ZestRedis;
import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.AbstractExecutor;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public abstract class ZestWorker {

    protected static Logger logger = LoggerFactory.getLogger(ZestWorker.class);

    protected Map<String, AbstractExecutor> executorMap = new HashMap<>();
    /**
     * value 放三种东西: <br>
     * javax.sql.DataSource <br>
     * org.springframework.data.mongodb.core.MongoOperations<br>
     * org.springframework.data.redis.core.RedisOperations
     */
    private Map<String, Object> sourceOperations = Collections.synchronizedMap(new HashMap<>());

    protected void loadAnnotation(Object test) {
        Class<?> clazz = test.getClass();
        while (!StringUtils.equals(Object.class.getName(), clazz.getName())) {
            for (Field f : clazz.getDeclaredFields()) {
                ZestSource zestSource = f.getAnnotation(ZestSource.class);
                if (zestSource == null) {
                    continue;
                }

                Object value;
                Object obj = ZestReflectHelper.getValue(test, f.getName());
                if (obj instanceof DataSource) {
                    value = DataSourceUtils.getConnection((DataSource) obj);
                } else if (obj instanceof MongoOperations || obj instanceof RedisOperations) {
                    value = obj;
                } else {
                    throw new ZestException(Messages.parseOperation());
                }

                if (sourceOperations.containsKey(zestSource.value())) {
                    throw new ZestException(Messages.duplicateOperation(zestSource.value()));
                }

                sourceOperations.put(zestSource.value(), value);
                try {
                    executorMap.put(zestSource.value(), zestSource.executorClass().newInstance());
                } catch (Exception e) {
                    throw new ZestException(Messages.initExecutor(zestSource.executorClass().getName()), e);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    protected void prepare(ZestData zestData) {
        for (Field f : zestData.getParam().getClass().getDeclaredFields()) {
            prepare(zestData, f, ZestConnection.class, Connection.class);
            prepare(zestData, f, ZestMongo.class, MongoOperations.class);
            prepare(zestData, f, ZestRedis.class, RedisOperations.class);
        }
    }

    private <T extends Annotation> void prepare(ZestData zestData, Field f, Class<T> annotationClass, Class<?> operationClass) {
        T ann = f.getAnnotation(annotationClass);
        if (ann != null) {
            if (!operationClass.getName().equals(f.getType().getName())) {
                throw new ZestException(Messages.annotationMatch(annotationClass.getSimpleName(), operationClass.getName()));
            }

            String sourceId = String.valueOf(ZestReflectHelper.getValue(ann, "value"));
            ZestReflectHelper.setValue(zestData.getParam(), f.getName(), getSourceOperation(sourceId));
        }
    }

    public void initDataSource(ZestData zestData) {
        for (Source source : zestData.getSourceList()) {
            AbstractExecutor executor = executorMap.get(source.getId());

            executor.clear(this, zestData, source);
            executor.init(this, zestData, source);
        }
    }

    public void checkTargetDataSource(ZestData zestData) {
        for (Source source : zestData.getSourceList()) {
            executorMap.get(source.getId()).verify(this, zestData, source);
        }
    }

    public Object getSourceOperation(String sourceId) {
        return sourceOperations.get(sourceId);
    }

    public <T> T getSourceOperation(String sourceId, Class<T> operationClass) {
        Object operation = sourceOperations.get(sourceId);
        if (operationClass.isAssignableFrom(operation.getClass())) {
            return operationClass.cast(operation);
        }

        throw new ZestException(Messages.operationNull(sourceId));
    }

    public <E extends AbstractExecutor> E getExecutor(String sourceId, Class<E> executorClass) {
        return executorClass.cast(executorMap.get(sourceId));
    }
}
