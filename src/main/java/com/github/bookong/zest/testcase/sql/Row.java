package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.rule.RuleFactory;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.testcase.AbstractRowData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import org.apache.commons.lang3.StringUtils;

import javax.xml.namespace.QName;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Jiang Xu
 */
public class Row extends AbstractRowData {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public Row(ZestWorker worker, String sourceId, String tableName, int rowIdx,
               com.github.bookong.zest.support.xml.data.Row xmlRow, Map<String, Integer> sqlTypes,
               boolean isTargetData){
        SqlExecutor sqlExecutor = worker.getExecutor(sourceId, SqlExecutor.class);
        for (Entry<QName, String> entry : xmlRow.getOtherAttributes().entrySet()) {
            String fieldName = entry.getKey().toString();
            Object value = parseValue(sqlExecutor, tableName, fieldName, entry.getValue(), sqlTypes);
            fields.put(fieldName, value);
        }

        for (Field xmlField : xmlRow.getField()) {
            String fieldName = xmlField.getName();
            if (fields.containsKey(xmlField.getName())) {
                throw new ZestException(Messages.parseDataFieldDuplicate(tableName, fieldName));
            }

            if (xmlField.getNull() != null) {
                fields.put(fieldName, null);

            } else if (xmlField.getValue() != null) {
                fields.put(fieldName, StringUtils.trimToEmpty(xmlField.getValue()));

            } else {
                if (!isTargetData) {
                    throw new ZestException(Messages.parseDataFieldUnder(tableName, fieldName));
                }

                // fields.put(fieldName, RuleFactory.createRule(sourceId, tableName, rowIdx, fieldName, xmlField));
            }
        }
    }

    private Object parseValue(SqlExecutor sqlExecutor, String tableName, String fieldName, String xmlFieldValue,
                              Map<String, Integer> colSqlTypes) {
        Integer colSqlType = colSqlTypes.get(fieldName.toLowerCase());
        if (colSqlType == null) {
            throw new ZestException(Messages.parseDataSqlType(tableName, fieldName));
        }

        try {
            return sqlExecutor.parseRowValue(tableName, fieldName, colSqlType, xmlFieldValue);
        } catch (UnsupportedOperationException e) {
            // Ignore
        }

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
                return ZestDateUtil.parseDate(xmlFieldValue);

            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return xmlFieldValue;

            default:
                throw new ZestException(Messages.parseDataSqlTypeUnsupported(tableName, fieldName, colSqlType));
        }
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}
