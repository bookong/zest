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
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Row;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.junit.Assert;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * Operator for <em>SQL</em>.
 *
 * @author Jiang Xu
 */
public class SqlExecutor extends AbstractExecutor {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> supportedOperatorClass() {
        return DataSource.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractTable createTable() {
        return new Table();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(ZestWorker worker, ZestData zestData, Source source) {
        DataSource dataSource = worker.getOperator(source.getId(), DataSource.class);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            for (AbstractTable<?> table : findAllTables(source)) {
                ZestSqlHelper.execute(conn, String.format("truncate table `%s`", table.getName()));
            }
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable sourceTable) {
        Table table = (Table) sourceTable;

        if (!table.getDataList().isEmpty()) {
            DataSource dataSource = worker.getOperator(source.getId(), DataSource.class);
            Connection conn = DataSourceUtils.getConnection(dataSource);
            try {
                ZestSqlHelper.insert(conn, table);
            } finally {
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable sourceTable) {
        Table table = (Table) sourceTable;
        DataSource dataSource = worker.getOperator(source.getId(), DataSource.class);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        List<Map<String, Object>> actualList;
        try {
            actualList = ZestSqlHelper.find(conn, table);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        Assert.assertEquals(Messages.verifyTableSize(source.getId(), table.getName()), //
                            table.getDataList().size(), actualList.size());

        for (int i = 0; i < table.getDataList().size(); i++) {
            Row expected = table.getDataList().get(i);
            Map<String, Object> actual = actualList.get(i);
            expected.verify(worker, zestData, source, table, i + 1, actual);
        }
    }

    /**
     * Custom data parser
     *
     * @param tableName
     *          Database table name.
     * @param fieldName
     *          Field name of the database table.
     * @param fieldSqlType
     *          {@link Types} of the field of the database table.
     * @param value
     *          Actual data value.
     * @return value after data conversion.
     * @throws UnsupportedOperationException
     *          This method is not implemented by default.
     */
    public Object parseRowValue(String tableName, String fieldName, Integer fieldSqlType,
                                Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Custom load {@link Types} value.
     *
     * @param conn
     *          Database connection object.
     * @param sqlTypes
     *          {@link Types} map.
     * @throws UnsupportedOperationException
     *          This method is not implemented by default.
     */
    public void loadSqlTypes(Connection conn, Map<String, Integer> sqlTypes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Custom validation data row.
     *
     * @param conn
     *          Database connection object.
     * @param zestData
     *          An object containing unit test case data.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     * @param table
     *          Database table.
     * @param rowIdx
     *          The number of the data.
     * @param expectedRow
     *          Expected row data.
     * @param actualRow
     *          Actual row data.
     * @throws UnsupportedOperationException
     *          This method is not implemented by default.
     */
    public void verifyRow(Connection conn, ZestData zestData, Source source, Table table, int rowIdx,
                          Map<String, Object> expectedRow,
                          Map<String, Object> actualRow) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
