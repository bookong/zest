package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.rule.AbstractRule;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractRow;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestJsonUtil;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Types;
import java.util.*;

/**
 * @author Jiang Xu
 */
public class Row extends AbstractRow<Map<String, Object>> {

    private Map<String, Object> dataMap = new LinkedHashMap<>();

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

    @Override
    public void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable<?> table, int rowIdx,
                       Map<String, Object> actualRow) {
        try {
            SqlExecutor executor = worker.getExecutor(source.getId(), SqlExecutor.class);
            Connection conn = worker.getOperator(source.getId(), Connection.class);
            try {
                executor.verifyRow(conn, zestData, source, (Table) table, rowIdx, actualRow);
            } catch (UnsupportedOperationException e) {
                verify(zestData, table, rowIdx, actualRow);
            }
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

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    @Override
    protected Set<String> getExpectedFields() {
        return getDataMap().keySet();
    }
}
