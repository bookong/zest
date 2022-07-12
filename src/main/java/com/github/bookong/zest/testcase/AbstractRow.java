/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.testcase;

import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.rule.AbstractRule;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

/**
 * Abstract table data.
 *
 * @author Jiang Xu
 */
public abstract class AbstractRow<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract Set<String> getExpectedFields();

    /**
     * Verify the result data.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     * @param sourceTable
     *          A data table object containing the expected data.
     * @param rowIdx
     *          The number of the data.
     * @param actualData
     *          Actual Data.
     */
    public abstract void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable<?> sourceTable,
                                int rowIdx, T actualData);

    /**
     * Verify the result data.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param sourceTable
     *          A data table object containing the expected data.
     * @param rowIdx
     *          The number of the data.
     * @param fieldName
     *          Field name.
     * @param expected
     *          Expected data.
     * @param actual
     *          Actual Data.
     */
    protected void verify(ZestData zestData, AbstractTable<?> sourceTable, int rowIdx, String fieldName,
                          Object expected, Object actual) {
        AbstractRule rule = sourceTable.getRuleMap().get(fieldName);

        if (getExpectedFields().contains(fieldName)) {
            if (expected != null) {
                if (rule != null) {
                    logger.info(Messages.verifyRuleIgnore(rule.getField(), rowIdx));
                }

                Assert.assertNotNull(Messages.verifyRowDataNotNull(fieldName), actual);

                if (expected instanceof Date) {
                    Assert.assertTrue(Messages.verifyRowDataDate(fieldName), actual instanceof Date);
                    String expectedValue = ZestDateUtil.formatDateNormal((Date) expected);
                    String actualValue = ZestDateUtil.formatDateNormal((Date) actual);
                    Assert.assertEquals(Messages.verifyRowData(fieldName, expectedValue), expectedValue, actualValue);

                } else if (expected instanceof Double) {
                    Assert.assertTrue(Messages.verifyRowDataNumber(fieldName), actual instanceof Double);
                    Double expectedValue = (Double) expected;
                    Double actualValue = (Double) actual;
                    Assert.assertEquals(Messages.verifyRowData(fieldName, String.valueOf(expectedValue)), expectedValue, actualValue,
                                        0.000001D);

                } else {
                    String expectedValue = String.valueOf(expected);
                    String actualValue = String.valueOf(actual);
                    Assert.assertEquals(Messages.verifyRowData(fieldName, expectedValue), expectedValue, actualValue);
                }

            } else {
                // expected == null
                if (rule != null) {
                    rule.verify(zestData, actual);
                } else {
                    Assert.assertNull(Messages.verifyRowDataNull(fieldName), actual);
                }
            }

        } else if (rule != null) {
            rule.verify(zestData, actual);
        }
    }
}
