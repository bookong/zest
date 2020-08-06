package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.exception.LoadTestCaseFileException;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;

/**
 * @author jiangxu
 */
public class RuleFactory {

    public static AbstractRule createRule(String tableName, String fieldName,
                                          Field xmlField) throws LoadTestCaseFileException {
        if (xmlField.getRegExp() != null) {
            return new RegExpRule(xmlField);

        } else if (xmlField.getCurrentTime() != null) {
            return new CurrentTimeRule(xmlField);

        } else if (xmlField.getFromCurrentTime() != null) {
            return new FromCurrentTimeRule(xmlField);

        } else {
            throw new LoadTestCaseFileException(Messages.parseDataFieldNone(tableName, fieldName));
        }
    }
}
