package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.support.rule.AbstractRule;
import com.github.bookong.zest.support.rule.CurrentTimeRule;
import com.github.bookong.zest.support.rule.FromCurrentTimeRule;
import com.github.bookong.zest.testcase.AbstractRowData;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestJsonUtil;
import org.junit.Assert;

import java.sql.Types;
import java.util.*;

/**
 * @author Jiang Xu
 */
public class Row extends AbstractRowData {

    private Map<String, Object> dataMap = new LinkedHashMap<>();

    public Row(SqlExecutor sqlExecutor, Map<String, Integer> sqlTypes, String tableName, String xmlContent){
        Map xmlDataMap = ZestJsonUtil.fromJson(xmlContent, HashMap.class);
        for (Object key : xmlDataMap.keySet()) {
            String columnName = String.valueOf(key);
            Integer sqlType = sqlTypes.get(columnName);
            if (sqlType == null) {
                throw new ZestException(Messages.parseDataTableRowExist(columnName, tableName));
            }

            getDataMap().put(columnName, parseValue(sqlExecutor, sqlType, tableName, columnName, xmlDataMap.get(key)));
        }
    }

    public void verify(ZestData zestData, Source source, Table table, int rowIdx, Map<String, Object> actualRow) {
        try {
            Set<String> verified = new HashSet<>(getDataMap().size() + 1);
            for (Map.Entry<String, Object> entry : getDataMap().entrySet()) {
                String columnName = entry.getKey();
                Object expected = entry.getValue();
                Object actual = actualRow.get(columnName);
                verified.add(columnName);

                if (expected == null) {
                    Assert.assertNull(Messages.verifyRowDataNull(columnName), actual);

                } else if (expected instanceof Date) {
                    Assert.assertTrue(Messages.verifyRowDataDate(columnName), actual instanceof Date);
                    Date expectedDateInZest = ZestDateUtil.getDateInZest(zestData, (Date) expected);
                    String expectedValue = ZestDateUtil.formatDateNormal(expectedDateInZest);
                    String actualValue = ZestDateUtil.formatDateNormal((Date) actual);
                    Assert.assertEquals(Messages.verifyRowData(columnName, expectedValue), expectedValue, actualValue);

                } else {
                    String expectedValue = String.valueOf(expected);
                    String actualValue = String.valueOf(actual);
                    Assert.assertEquals(Messages.verifyRowData(columnName, expectedValue), expectedValue, actualValue);
                }
            }

            for (AbstractRule rule : table.getRuleMap().values()) {
                if (verified.contains(rule.getPath())) {
                    logger.info(Messages.verifyRowRuleNonuse(rule.getPath(), rowIdx));
                    continue;
                }

                rule.verify(zestData, source, table, rowIdx, rule.getPath(), actualRow.get(rule.getPath()));
            }

        } catch (Exception e) {
            throw new ZestException(Messages.verifyRowError(source.getId(), table.getName(), rowIdx), e);
        }
    }

    private Object parseValue(SqlExecutor sqlExecutor, Integer fieldSqlType, String tableName, String columnName,
                              Object value) {
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
                    return ZestDateUtil.parseDate(String.valueOf(value));
                default:
                    return value;
            }
        }
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }
}
