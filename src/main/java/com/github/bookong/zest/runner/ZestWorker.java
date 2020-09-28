/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.runner;

import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.AbstractExecutor;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract <em>Zest</em> worker.
 *
 * @author Jiang Xu
 */
public abstract class ZestWorker {

    protected static Logger               logger      = LoggerFactory.getLogger(ZestWorker.class);

    private Map<String, AbstractExecutor> executorMap = new HashMap<>();

    private Map<String, Object>           operatorMap = new HashMap<>();

    /**
     * Load the annotation class from the test object.
     *
     * @param test
     *          A test object.
     */
    public void loadAnnotation(Object test) {
        Class<?> clazz = test.getClass();
        while (!Object.class.getName().equals(clazz.getName())) {
            for (Field f : clazz.getDeclaredFields()) {
                ZestSource zestSource = f.getAnnotation(ZestSource.class);
                if (zestSource == null) {
                    continue;
                }

                AbstractExecutor executor;
                try {
                    executor = zestSource.executorClass().newInstance();
                    executorMap.put(zestSource.value(), executor);
                } catch (Exception e) {
                    throw new ZestException(Messages.parseExecutor(zestSource.value(),
                                                                   zestSource.executorClass().getName()),
                                            e);
                }

                Object obj = ZestReflectHelper.getValue(test, f.getName());
                if (!executor.supportedOperatorClass().isAssignableFrom(obj.getClass())) {
                    throw new ZestException(Messages.parseOperator(zestSource.value(),
                                                                   executor.supportedOperatorClass().getName()));
                }

                if (!operatorMap.containsKey(zestSource.value())) {
                    operatorMap.put(zestSource.value(), obj);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Use the data in the test case to initialize the database to be tested.
     *
     * @param zestData
     *          An object containing unit test case data.
     */
    public void initSource(ZestData zestData) {
        for (Source source : zestData.getSourceList()) {
            AbstractExecutor executor = executorMap.get(source.getId());

            executor.clear(this, zestData, source);
            executor.init(this, zestData, source);
        }
    }

    /**
     * Use the data in the test case to automatically verify the database.
     *
     * @param zestData
     *          An object containing unit test case data.
     */
    public void verifySource(ZestData zestData) {
        for (Source source : zestData.getSourceList()) {
            executorMap.get(source.getId()).verify(this, zestData, source);
        }
    }

    /**
     * Get the operator bound to the specified source.
     *
     * @param sourceId
     *          An object containing information about the current {@link ZestSource}.
     * @return an operator.
     */
    public Object getOperator(String sourceId) {
        Object operation = operatorMap.get(sourceId);
        if (operation == null) {
            throw new ZestException(Messages.operatorUnbound(sourceId));
        }
        return operation;
    }

    /**
     * Get the operator bound to the specified source.
     *
     * @param sourceId
     *          An object containing information about the current {@link ZestSource}.
     * @param operatorClass
     *          Class object corresponding to the operator.
     * @param <T>
     *          Operator class.
     * @return an operator.
     */
    public <T> T getOperator(String sourceId, Class<T> operatorClass) {
        Object operation = getOperator(sourceId);

        if (operatorClass.isAssignableFrom(operation.getClass())) {
            return operatorClass.cast(operation);
        } else {
            throw new ZestException(Messages.operatorCast(sourceId, operation.getClass().getName(),
                                                          operatorClass.getName()));
        }
    }

    /**
     * Get the executor bound to the specified source.
     *
     * @param sourceId
     *          An object containing information about the current {@link ZestSource}.
     * @return an executor.
     */
    public AbstractExecutor getExecutor(String sourceId) {
        AbstractExecutor executor = executorMap.get(sourceId);
        if (executor == null) {
            throw new ZestException(Messages.operatorUnbound(sourceId));
        }
        return executor;
    }

    /**
     * Get the executor bound to the specified source.
     *
     * @param sourceId
     *          An object containing information about the current {@link ZestSource}.
     * @param executorClass
     *          Class object corresponding to the executor.
     * @param <E>
     *          Executor class.
     * @return an executor.
     */
    public <E extends AbstractExecutor> E getExecutor(String sourceId, Class<E> executorClass) {
        return executorClass.cast(executorMap.get(sourceId));
    }
}
