package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.testcase.AbstractRowData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestJsonUtil;

import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class Row extends AbstractRowData {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public Row(SqlExecutor sqlExecutor, Map<String, Integer> sqlTypes, String tableName, String xmlContent){
        Map dataMap = ZestJsonUtil.fromJson(xmlContent, HashMap.class);
        for (Object key : dataMap.keySet()) {
            String fieldName = String.valueOf(key);
            if (!sqlTypes.containsKey(fieldName)) {
                throw new ZestException(Messages.parseTableRowExist(tableName, fieldName));
            }

            Object value = parseValue(sqlExecutor, tableName, fieldName, (String) dataMap.get(key), sqlTypes);
            fields.put(fieldName, value);
        }
    }

    private Object parseValue(SqlExecutor sqlExecutor, String tableName, String fieldName, String xmlValue,
                              Map<String, Integer> colSqlTypes) {
        if (xmlValue == null) {
            return null;
        }

        Integer colSqlType = colSqlTypes.get(fieldName.toLowerCase());
        if (colSqlType == null) {
            throw new ZestException(Messages.parseDataSqlType(tableName, fieldName));
        }

        try {
            return sqlExecutor.parseRowValue(tableName, fieldName, colSqlType, xmlValue);

        } catch (UnsupportedOperationException e) {
            switch (colSqlType) {
                case Types.BIT:
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                    return Integer.valueOf(xmlValue);

                case Types.BIGINT:
                    return Long.valueOf(xmlValue);

                case Types.FLOAT:
                    return Float.valueOf(xmlValue);

                case Types.REAL:
                case Types.DOUBLE:
                case Types.NUMERIC:
                case Types.DECIMAL:
                    return Double.valueOf(xmlValue);

                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    return ZestDateUtil.parseDate(xmlValue);

                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    return xmlValue;

                default:
                    throw new ZestException(Messages.parseDataSqlTypeUnsupported(tableName, fieldName, colSqlType));
            }
        }
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}
