package com.github.bookong.zest.runner;

import com.github.bookong.zest.annotation.ZestConnection;
import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.AbstractExecutor;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.support.xml.data.Param;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.ZestParam;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.redis.core.RedisOperations;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangxu
 */
public abstract class ZestWorker {

    protected static Logger                 logger           = LoggerFactory.getLogger(ZestWorker.class);

    protected Map<String, AbstractExecutor> executerMap      = new HashMap<>();
    /**
     * value 放三种东西: <br>
     * javax.sql.DataSource <br>
     * org.springframework.data.mongodb.core.MongoOperations<br>
     * org.springframework.data.redis.core.RedisOperations
     */
    private Map<String, Object>             sourceOperations = Collections.synchronizedMap(new HashMap<>());

    protected abstract Connection getConnection(DataSource dataSource);

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
                    value = getConnection((DataSource) obj);
                } else if (obj instanceof MongoOperations || obj instanceof RedisOperations) {
                    value = obj;
                } else {
                    throw new RuntimeException(Messages.parseOperation());
                }

                if (sourceOperations.containsKey(zestSource.value())) {
                    throw new RuntimeException(Messages.duplicateOperation(zestSource.value()));
                }

                sourceOperations.put(zestSource.value(), value);
                try {
                    executerMap.put(zestSource.value(), zestSource.executorClass().newInstance());
                } catch (Exception e) {
                    throw new ZestException(Messages.initExecuter(zestSource.executorClass().getName()), e);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    protected void prepare(ZestData zestData) {
        for (Field f : zestData.getParam().getClass().getDeclaredFields()) {
            ZestConnection ann = f.getAnnotation(ZestConnection.class);
            if (ann != null) {
                if (!Connection.class.getName().equals(f.getType().getName())) {
                    throw new ZestException(Messages.annotationConnection());
                }

                Connection conn = getSourceOperationConnection(ann.value(), Connection.class);
                ZestReflectHelper.setValue(zestData.getParam(), f.getName(), conn);
            }
        }
    }

    public void initDataSource(ZestData zestData) {
        for (Source source : zestData.getSourceList()) {
            AbstractExecutor executer = executerMap.get(source.getId());

            if (executer instanceof SqlExecutor) {
                SqlExecutor jdbcExcuter = (SqlExecutor) executer;
                Connection conn = getSourceOperationConnection(source.getId(), Connection.class);

                jdbcExcuter.clearDatabase(conn, zestData, source);
                jdbcExcuter.initDatabase(conn, zestData, source);
            }
        }
    }

    public void checkTargetDataSource(ZestData zestData) {
        for (Source source : zestData.getSourceList()) {
            AbstractExecutor executer = executerMap.get(source.getId());

            if (executer instanceof SqlExecutor) {
                SqlExecutor jdbcExcuter = (SqlExecutor) executer;
                Connection conn = getSourceOperationConnection(source.getId(), Connection.class);

                if (source.getTargetData().isIgnoreCheck()) {
                    logger.info(Messages.ignoreTargetData(source.getId()));
                } else {
                    jdbcExcuter.checkTargetDatabase(conn, zestData, source);
                }
            }
        }
    }

    public Object getSourceOperation(String sourceId) {
        return sourceOperations.get(sourceId);
    }

    public <T> T getSourceOperationConnection(String sourceId, Class<T> operationClass) {
        Object operation = sourceOperations.get(sourceId);
        if (operationClass.isAssignableFrom(operation.getClass())) {
            return operationClass.cast(operation);
        }

        throw new ZestException(Messages.operationNull(sourceId));
    }

}
