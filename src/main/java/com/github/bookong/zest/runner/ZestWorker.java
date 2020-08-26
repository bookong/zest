package com.github.bookong.zest.runner;

import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.AbstractExecutor;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public abstract class ZestWorker {

    protected static Logger                 logger      = LoggerFactory.getLogger(ZestWorker.class);

    protected Map<String, AbstractExecutor> executorMap = new HashMap<>();
    /**
     * value 放两种东西: <br>
     * javax.sql.DataSource <br>
     * org.springframework.data.mongodb.core.MongoOperations
     */
    private Map<String, Object>             operatorMap = new HashMap<>();

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

                if (operatorMap.containsKey(zestSource.value())) {
                    throw new ZestException(Messages.duplicateOperation(zestSource.value()));
                }

                operatorMap.put(zestSource.value(), value);
                try {
                    executorMap.put(zestSource.value(), zestSource.executorClass().newInstance());
                } catch (Exception e) {
                    throw new ZestException(Messages.initExecutor(zestSource.executorClass().getName()), e);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public void initDataSource(ZestData zestData) {
        for (Source source : zestData.getSourceList()) {
            AbstractExecutor executor = executorMap.get(source.getId());

            executor.clear(this, zestData, source);
            executor.init(this, zestData, source);
        }
    }

    public void verifyDataSource(ZestData zestData) {
        for (Source source : zestData.getSourceList()) {
            executorMap.get(source.getId()).verify(this, zestData, source);
        }
    }

    public Object getOperator(String sourceId) {
        return operatorMap.get(sourceId);
    }

    public <T> T getOperator(String sourceId, Class<T> operatorClass) {
        Object operation = operatorMap.get(sourceId);
        if (operatorClass.isAssignableFrom(operation.getClass())) {
            return operatorClass.cast(operation);
        }

        return null;
    }

    public <E extends AbstractExecutor> E getExecutor(String sourceId, Class<E> executorClass) {
        return executorClass.cast(executorMap.get(sourceId));
    }
}
