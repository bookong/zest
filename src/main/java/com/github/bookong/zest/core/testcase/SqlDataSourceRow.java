package com.github.bookong.zest.core.testcase;

import com.github.bookong.zest.support.rule.RuleFactory;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.support.xml.data.Row;
import com.github.bookong.zest.util.LoadTestCaseUtil;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import org.apache.commons.lang.StringUtils;

import javax.xml.namespace.QName;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author jiangxu
 */
public class SqlDataSourceRow extends AbstractDataSourceRow {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public SqlDataSourceRow(TestCaseData testCaseData, String dataSourceId, String tableName, int rowIdx, Row xmlRow,
                            List<AbstractDataConverter> dataConverterList, boolean isTargetData){
        Map<String, Integer> colSqlTypes = testCaseData.getRmdbTableColSqlTypes(dataSourceId, tableName);

        for (Entry<QName, String> entry : xmlRow.getOtherAttributes().entrySet()) {
            String fieldName = entry.getKey().toString();
            Object value = parseValue(tableName, fieldName, entry.getValue(), colSqlTypes, dataConverterList);
            fields.put(fieldName, value);
        }

        for (Field xmlField : xmlRow.getField()) {
            String fieldName = xmlField.getName();
            if (fields.containsKey(xmlField.getName())) {
                throw new RuntimeException(Messages.parseDataFieldDuplicate(tableName, fieldName));
            }

            if (xmlField.getNull() != null) {
                fields.put(fieldName, null);

            } else if (xmlField.getValue() != null) {
                fields.put(fieldName, StringUtils.trimToEmpty(xmlField.getValue()));

            } else {
                if (!isTargetData) {
                    throw new RuntimeException(Messages.parseDataFieldUnder(tableName, fieldName));
                }

                fields.put(fieldName, RuleFactory.createRule(dataSourceId, tableName, rowIdx, fieldName, xmlField));
            }
        }
    }

    private Object parseValue(String tableName, String fieldName, String xmlFieldValue,
                              Map<String, Integer> colSqlTypes, List<AbstractDataConverter> dataConverterList) {
        Integer colSqlType = colSqlTypes.get(fieldName.toLowerCase());
        if (colSqlType == null) {
            throw new RuntimeException(Messages.parseDataSqlType(tableName, fieldName));
        }

        for (AbstractDataConverter dataConverter : dataConverterList) {
            if (dataConverter.applySqlType(colSqlType)) {
                return dataConverter.sqlDataConvert(colSqlType, xmlFieldValue);
            }
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
                throw new RuntimeException(Messages.parseDataSqlTypeUnsupport(tableName, fieldName, colSqlType));
        }
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}
