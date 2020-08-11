package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;

/**
 * @author jiangxu
 */
public class RuleFactory {

    public static AbstractRule createRule(String dataSourceId, String tableName, int rowIdx, String fieldName,
                                          Field xmlField) {
        if (xmlField.getRegExp() != null) {
            return new RegExpRule(xmlField);

        } else if (xmlField.getCurrentTime() != null) {
            return new CurrentTimeRule(xmlField);

        } else if (xmlField.getFromCurrentTime() != null) {
            return new FromCurrentTimeRule(dataSourceId, tableName, rowIdx, fieldName, xmlField);

        } else {
            throw new RuntimeException(Messages.parseDataFieldNone(tableName, fieldName));
        }
    }
}
