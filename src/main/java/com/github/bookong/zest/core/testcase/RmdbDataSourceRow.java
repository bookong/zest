package com.github.bookong.zest.core.testcase;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;

import com.github.bookong.zest.util.LoadTestCaseUtils;

/**
 * 关系型数据库的表
 * 
 * @author jiangxu
 */
public class RmdbDataSourceRow extends AbstractDataSourceRow {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public RmdbDataSourceRow(String tableName, Map<String, Integer> colSqlTypes, com.github.bookong.zest.core.xml.data.Row xmlRow, boolean isTargetData){
        for (Entry<QName, String> entry : xmlRow.getOtherAttributes().entrySet()) {
            String fieldName = entry.getKey().toString().toLowerCase();
            fields.put(fieldName, parseValue(tableName, fieldName, entry.getValue(), colSqlTypes));
        }

        if (isTargetData) {
            for (com.github.bookong.zest.core.xml.data.Field xmlField : xmlRow.getField()) {
                fields.put(xmlField.getName().toLowerCase(), xmlField);
            }
        } else if (!xmlRow.getField().isEmpty()) {
            throw new RuntimeException(String.format("Xml element Table (%1$s) with xml element Field must under xml element Target.", tableName));
        }
    }

    private Object parseValue(String tableName, String fieldName, String xmlFieldValue, Map<String, Integer> colSqlTypes) {
        Integer colSqlType = colSqlTypes.get(fieldName);
        if (colSqlType == null) {
            throw new RuntimeException(String.format("Table (%1$s) not found RMDB column (%2$s) SqlType.", tableName, fieldName));
        }

        // TODO 暂时先不考虑 BLOB 和 CLOB 等
        switch (colSqlType) {
            case Types.BIT:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return Integer.valueOf(xmlFieldValue);
            case Types.BIGINT:
                return Long.valueOf(xmlFieldValue);
            case Types.FLOAT:
                return Float.valueOf(xmlFieldValue);
            case Types.REAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.DECIMAL:
                return Double.valueOf(xmlFieldValue);
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return LoadTestCaseUtils.parseDate(xmlFieldValue);
            default:
                return xmlFieldValue;
        }
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}
