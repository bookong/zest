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
package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractRow;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestJsonUtil;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <em>Row</em> data corresponding to <em>Table</em>.
 *
 * @author Jiang Xu
 */
public class Row extends AbstractRow<Map<String, Object>> {

    private Map<String, Object> dataMap = new LinkedHashMap<>();

    /**
     * Create a new instance.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param sqlExecutor
     *          SQL executor.
     * @param sqlTypes
     *          {@link Types} map.
     * @param tableName
     *          Table Name.
     * @param xmlContent
     *          XML content.
     */
    public Row(ZestData zestData, SqlExecutor sqlExecutor, Map<String, Integer> sqlTypes, String tableName,
               String xmlContent){
        Map xmlDataMap = ZestJsonUtil.fromJson(xmlContent, HashMap.class);
        for (Object key : xmlDataMap.keySet()) {
            String columnName = String.valueOf(key);
            Integer sqlType = sqlTypes.get(columnName);
            if (sqlType == null) {
                throw new ZestException(Messages.parseDataTableRowExist(columnName, tableName));
            }

            getDataMap().put(columnName,
                             parseValue(zestData, sqlExecutor, sqlType, tableName, columnName, xmlDataMap.get(key)));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable<?> table, int rowIdx,
                       Map<String, Object> actualRow) {
        try {
            SqlExecutor executor = worker.getExecutor(source.getId(), SqlExecutor.class);
            DataSource dataSource = worker.getOperator(source.getId(), DataSource.class);
            Connection conn = DataSourceUtils.getConnection(dataSource);
            try {
                executor.verifyRow(conn, zestData, source, (Table) table, rowIdx, getDataMap(), actualRow);
            } catch (UnsupportedOperationException e) {
                verify(zestData, table, rowIdx, actualRow);
            } finally {
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
        } catch (AssertionError e) {
            logger.error(Messages.verifyRowError(source.getId(), table.getName(), rowIdx));
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.verifyRowError(source.getId(), table.getName(), rowIdx), e);
        }
    }

    private void verify(ZestData zestData, AbstractTable<?> table, int rowIdx, Map<String, Object> actualRow) {
        for (Map.Entry<String, Object> entry : actualRow.entrySet()) {
            String columnName = entry.getKey();
            Object actual = entry.getValue();
            Object expected = getDataMap().get(columnName);

            verify(zestData, table, rowIdx, columnName, expected, actual);
        }
    }

    private Object parseValue(ZestData zestData, SqlExecutor sqlExecutor, Integer fieldSqlType, String tableName,
                              String columnName, Object value) {
        if (value == null) {
            return null;
        }

        try {
            return sqlExecutor.parseRowValue(tableName, columnName, fieldSqlType, value);
        } catch (UnsupportedOperationException e) {
            switch (fieldSqlType) {
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    return ZestDateUtil.getDateInZest(zestData, ZestDateUtil.parseDate(String.valueOf(value)));
                default:
                    return value;
            }
        }
    }

    /**
     * @return data actually inserted into <em>DB</em>
     */
    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getExpectedFields() {
        return getDataMap().keySet();
    }
}
