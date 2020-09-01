package com.github.bookong.zest.testcase;

import com.github.bookong.zest.rule.AbstractRule;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

/**
 * @author Jiang Xu
 */
public abstract class AbstractRow {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract Set<String> getExpectedFields();

    protected void verify(ZestData zestData, AbstractTable<?> sourceTable, int rowIdx, String fieldName,
                          Object expected, Object actual) {
        AbstractRule rule = sourceTable.getRuleMap().get(fieldName);

        if (getExpectedFields().contains(fieldName)) {
            if (expected != null) {
                if (rule != null) {
                    logger.info(Messages.verifyRuleIgnore(rule.getField(), rowIdx));
                }

                if (expected instanceof Date) {
                    Assert.assertTrue(Messages.verifyRowDataDate(fieldName), actual instanceof Date);
                    Date expectedDateInZest = ZestDateUtil.getDateInZest(zestData, (Date) expected);
                    String expectedValue = ZestDateUtil.formatDateNormal(expectedDateInZest);
                    String actualValue = ZestDateUtil.formatDateNormal((Date) actual);
                    Assert.assertEquals(Messages.verifyRowData(fieldName, expectedValue), expectedValue, actualValue);

                } else {
                    String expectedValue = String.valueOf(expected);
                    String actualValue = String.valueOf(actual);
                    Assert.assertEquals(Messages.verifyRowData(fieldName, expectedValue), expectedValue, actualValue);
                }

            } else {
                // expected == null
                if (rule != null) {
                    rule.verify(zestData, rule.getField(), actual);
                } else {
                    Assert.assertNull(Messages.verifyRowDataNull(fieldName), actual);
                }
            }

        } else if (rule != null) {
            rule.verify(zestData, rule.getField(), actual);
        }
    }
}
