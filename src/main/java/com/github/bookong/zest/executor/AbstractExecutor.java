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
package com.github.bookong.zest.executor;

import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Base class for executor. Specific realization of the function of initializing and automatically verifying the database.
 * 
 * @author Jiang Xu
 */
public abstract class AbstractExecutor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The operator supported by the executor. Operators are objects used to specifically manipulate the database, which
     * may be {@link DataSource} or {@link MongoOperations}.
     * 
     * @return operator classes supported by the executor.
     */
    public abstract Class<?> supportedOperatorClass();

    /**
     * Create a new {@code AbstractTable} instance.
     * 
     * @return an {@code AbstractTable} instance.
     */
    public abstract AbstractTable createTable();

    /**
     * Clear the old data of related tables in the database.
     * 
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     */
    public abstract void clear(ZestWorker worker, ZestData zestData, Source source);

    /**
     * Initialize the data in the database during the <em>setup phase</em>.
     * 
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     * @param sourceTable
     *          A data table object containing initial data.
     */
    protected abstract void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable sourceTable);

    /**
     * Automatically verify data in the database during the the <em>verify phase</em>.
     * 
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     * @param sourceTable
     *          A data table object containing the expected data.
     */
    protected abstract void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable sourceTable);

    /**
     * Initialize the data in the database during the <em>setup phase</em>
     * 
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     */
    public void init(ZestWorker worker, ZestData zestData, Source source) {
        for (AbstractTable table : source.getInitData().getTableList()) {
            init(worker, zestData, source, table);
        }
    }

    /**
     * Check if this operator is supported
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param sourceId
     *          Current {@link ZestSource#value()}
     */
    public void checkSupportedOperatorClass(ZestWorker worker, String sourceId) {
        Class<?> operatorClass = worker.getOperator(sourceId).getClass();
        if (!supportedOperatorClass().isAssignableFrom(operatorClass)) {
            throw new ZestException(Messages.parseSourceOperationUnknown(operatorClass.getName()));
        }
    }

    /**
     * Automatically verify data in the database during the the <em>verify phase</em>.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     */
    public void verify(ZestWorker worker, ZestData zestData, Source source) {
        if (source.getVerifyData().isIgnoreVerify()) {
            logger.info(Messages.verifyIgnore(source.getId()));
            return;
        }

        for (AbstractTable sourceTable : source.getVerifyData().getTableMap().values()) {
            if (sourceTable.isIgnoreVerify()) {
                logger.info(Messages.verifyTableIgnore(source.getId(), sourceTable.getName()));
                continue;
            }

            logger.info(Messages.verifyTableStart(source.getId(), sourceTable.getName()));
            verify(worker, zestData, source, sourceTable);
        }
    }

    /**
     * Get all the tables involved in this unit test
     * 
     * @param source
     *      An object containing information about the current {@link ZestSource}.
     * @return tables involved in this unit test
     */
    protected Set<AbstractTable<?>> findAllTables(Source source) {
        Set<AbstractTable<?>> tables = new LinkedHashSet<>();
        Set<String> set = new HashSet<>();
        source.getInitData().getTableList().forEach(table -> {
            if (!set.contains(table.getName())) {
                tables.add(table);
                set.add(table.getName());
            }
        });
        source.getVerifyData().getTableMap().values().forEach(table -> {
            if (!set.contains(table.getName())) {
                tables.add(table);
                set.add(table.getName());
            }
        });
        return tables;
    }
}
