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
package com.github.bookong.zest.util;

import com.github.bookong.zest.rule.CurrentTimeRule;
import com.github.bookong.zest.rule.FromCurrentTimeRule;
import com.github.bookong.zest.rule.RangeRule;
import com.github.bookong.zest.rule.RegExpRule;
import com.github.bookong.zest.testcase.ZestData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jiang Xu
 */
public class ZestAssertUtil {

    /**
     * Verification time (considering data offset).
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param msg
     *          The identifying message for the {@link AssertionError}.
     * @param dateFormat
     *          The pattern describing the date and time format.
     * @param expected
     *          Expected data.
     * @param actual
     *          Actual data.
     * @throws ParseException
     */
    public static void dateEquals(ZestData zestData, String msg, String dateFormat, String expected, String actual) throws ParseException {
        if (StringUtils.isBlank(expected)) {
            Assert.assertTrue(msg, StringUtils.isBlank(actual));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            dateEquals(zestData, msg, sdf.parse(expected), sdf.parse(actual));
        }
    }

    /**
     * Verification time (considering data offset).
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param msg
     *          The identifying message for the {@link AssertionError}.
     * @param expected
     *          Expected data.
     * @param actual
     *          Actual data.
     */
    public static void dateEquals(ZestData zestData, String msg, Date expected, Date actual) {
        Date expectedInZest = ZestDateUtil.getDateInZest(zestData, expected);
        Assert.assertEquals(msg, ZestDateUtil.formatDateNormal(expectedInZest), ZestDateUtil.formatDateNormal(actual));
    }

    /**
     * Manual validation {@link RegExpRule} rule.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param field
     *          Field name.
     * @param regExp
     *          Regular expression.
     * @param actual
     *          Actual data.
     */
    public static void verifyRegExpRule(ZestData zestData, String field, String regExp, Object actual) {
        verifyRegExpRule(zestData, field, false, regExp, actual);
    }

    /**
     * Manual validation {@link RegExpRule} rule.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param field
     *          Field name.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param regExp
     *          Regular expression.
     * @param actual
     *          Actual data.
     */
    public static void verifyRegExpRule(ZestData zestData, String field, boolean nullable, String regExp,
                                        Object actual) {
        new RegExpRule(field, nullable, regExp).verify(zestData, actual);
    }

    /**
     * Manual validation {@link CurrentTimeRule} rule.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param field
     *          Field name.
     * @param actual
     *          Actual data.
     */
    public static void verifyCurrentTimeRule(ZestData zestData, String field, Object actual) {
        verifyCurrentTimeRule(zestData, field, false, 1000, actual);
    }

    /**
     * Manual validation {@link CurrentTimeRule} rule.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param field
     *          Field name.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param offset
     *          The precision deviation of time, in milliseconds.
     * @param actual
     *          Actual data.
     */
    public static void verifyCurrentTimeRule(ZestData zestData, String field, boolean nullable, int offset,
                                             Object actual) {
        new CurrentTimeRule(field, nullable, offset).verify(zestData, actual);
    }

    /**
     * Manual validation {@link FromCurrentTimeRule} rule.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param field
     *          Field name.
     * @param min
     *          The minimum value from the current time, which can be a negative number.
     * @param max
     *          The maximum value from the current time, which can be a negative number.
     * @param unit
     *          Time unit, the value can be:
     *          <ul>
     *              <li>{@code Calendar.DAY_OF_YEAR}</li>
     *              <li>{@code Calendar.DAY_OF_MONTH}</li>
     *              <li>{@code Calendar.DAY_OF_WEEK}</li>
     *              <li>{@code Calendar.DAY_OF_WEEK_IN_MONTH}</li>
     *              <li>{@code Calendar.HOUR_OF_DAY}</li>
     *              <li>{@code Calendar.HOUR}</li>
     *              <li>{@code Calendar.MINUTE}</li>
     *              <li>{@code Calendar.SECOND}</li>
     *          </ul>
     * @param actual
     *          Actual data.
     */
    public static void verifyFromCurrentTimeRule(ZestData zestData, String field, int min, int max, int unit,
                                                 Object actual) {
        verifyFromCurrentTimeRule(zestData, field, false, min, max, unit, 1000, actual);
    }

    /**
     * Manual validation {@link FromCurrentTimeRule} rule.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param field
     *          Field name.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param min
     *          The minimum value from the current time, which can be a negative number.
     * @param max
     *          The maximum value from the current time, which can be a negative number.
     * @param unit
     *          Time unit, the value can be:
     *          <ul>
     *              <li>{@code Calendar.DAY_OF_YEAR}</li>
     *              <li>{@code Calendar.DAY_OF_MONTH}</li>
     *              <li>{@code Calendar.DAY_OF_WEEK}</li>
     *              <li>{@code Calendar.DAY_OF_WEEK_IN_MONTH}</li>
     *              <li>{@code Calendar.HOUR_OF_DAY}</li>
     *              <li>{@code Calendar.HOUR}</li>
     *              <li>{@code Calendar.MINUTE}</li>
     *              <li>{@code Calendar.SECOND}</li>
     *          </ul>
     * @param offset
     *          The precision deviation of time, in milliseconds.
     * @param actual
     *          Actual data.
     */
    public static void verifyFromCurrentTimeRule(ZestData zestData, String field, boolean nullable, int min, int max,
                                                 int unit, int offset, Object actual) {
        new FromCurrentTimeRule(field, nullable, min, max, unit, offset).verify(zestData, actual);
    }

    /**
     * Manual validation {@link RangeRule} rule.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param field
     *          Field name.
     * @param from
     *          The start value of the numerical range.
     * @param to
     *          The end value of the numerical range.
     * @param actual
     *          Actual data.
     */
    public static void verifyRangeRule(ZestData zestData, String field, Double from, Double to, Object actual) {
        verifyRangeRule(zestData, field, false, from, true, to, true, actual);
    }

    /**
     * Manual validation {@link RangeRule} rule.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param field
     *          Field name.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param from
     *          The start value of the numerical range.
     * @param includeFrom
     *          Whether to include the start value.
     * @param to
     *          The end value of the numerical range.
     * @param includeTo
     *          Whether to include the end value.
     * @param actual
     *          Actual data.
     */
    public static void verifyRangeRule(ZestData zestData, String field, boolean nullable, Double from,
                                                boolean includeFrom, Double to, boolean includeTo, Object actual) {
        new RangeRule(field, nullable, from, includeFrom, to, includeTo).verify(zestData, actual);
    }
}
