package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;

/**
 * @author Jiang Xu
 */
public class RuleFactory {

    public static AbstractRule createRule(String sourceId, String tableName, int rowIdx, String fieldName,
                                          Field xmlField) {
        if (xmlField.getRegExp() != null) {
            return new RegExpRule(xmlField);

        } else if (xmlField.getCurrentTime() != null) {
            return new CurrentTimeRule(xmlField);

        } else if (xmlField.getFromCurrentTime() != null) {
            return new FromCurrentTimeRule(sourceId, tableName, rowIdx, fieldName, xmlField);

        } else {
            throw new RuntimeException(Messages.parseDataFieldNone(tableName, fieldName));
        }
    }
}
