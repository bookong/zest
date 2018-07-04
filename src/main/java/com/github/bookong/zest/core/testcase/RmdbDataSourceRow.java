package com.github.bookong.zest.core.testcase;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import com.github.bookong.zest.util.LoadTestCaseUtil;
import com.github.bookong.zest.util.Messages;

/**
 * 关系型数据库的表
 * 
 * @author jiangxu
 */
public class RmdbDataSourceRow extends AbstractDataSourceRow {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public RmdbDataSourceRow(TestCaseData testCaseData, TestCaseDataSource testCaseDataSource, String tableName, com.github.bookong.zest.core.xml.data.Row xmlRow,
                             boolean isTargetData){
        for (Entry<QName, String> entry : xmlRow.getOtherAttributes().entrySet()) {
            String fieldName = entry.getKey().toString();
            fields.put(fieldName, parseValue(tableName, fieldName, entry.getValue(), testCaseData.getRmdbTableColSqlTypes(testCaseDataSource.getId(), tableName)));
        }

        if (isTargetData) {
            for (com.github.bookong.zest.core.xml.data.Field xmlField : xmlRow.getField()) {
                fields.put(xmlField.getName(), xmlField);
            }
        } else if (!xmlRow.getField().isEmpty()) {
            throw new RuntimeException(Messages.getString("rmdbDataSourceRow.xmlTableMustUnderTarget", tableName));
        }
    }

    private Object parseValue(String tableName, String fieldName, String xmlFieldValue, Map<String, Integer> colSqlTypes) {
        Integer colSqlType = colSqlTypes.get(fieldName.toLowerCase());
        if (colSqlType == null) {
            throw new RuntimeException(Messages.getString("rmdbDataSourceRow.tableNotFoundRmdbColSqlType", tableName, fieldName));
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
                return LoadTestCaseUtil.parseDate(xmlFieldValue);
            default:
                return xmlFieldValue;
        }
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}
