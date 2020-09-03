package com.github.bookong.zest.util;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.rule.CurrentTimeRule;
import com.github.bookong.zest.rule.FromCurrentTimeRule;
import com.github.bookong.zest.rule.RegExpRule;
import com.github.bookong.zest.testcase.ZestData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jiang Xu
 */
public class ZestAssertUtil {

    /** 比较时间（考虑数据偏移情况) */
    public static void dateEquals(ZestData zestData, String msg, String dateFormat, String expected, String actual) {
        try {
            if (StringUtils.isBlank(expected)) {
                Assert.assertTrue(msg, StringUtils.isBlank(actual));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                dateEquals(zestData, msg, sdf.parse(expected), sdf.parse(actual));
            }
        } catch (Exception e) {
            throw new ZestException(e);
        }
    }

    /** 比较时间（考虑数据偏移情况) */
    public static void dateEquals(ZestData zestData, String msg, Date expected, Date actual) {
        Date expectedInZest = ZestDateUtil.getDateInZest(zestData, expected);
        Assert.assertEquals(msg, ZestDateUtil.formatDateNormal(expectedInZest), ZestDateUtil.formatDateNormal(actual));
    }

    public static void verifyRegExpRule(ZestData zestData, String field, String regExp, Object actual) {
        verifyRegExpRule(zestData, field, false, regExp, actual);
    }

    public static void verifyRegExpRule(ZestData zestData, String field, boolean nullable, String regExp,
                                        Object actual) {
        new RegExpRule(field, nullable, regExp).verify(zestData, actual);
    }

    public static void verifyCurrentTimeRule(ZestData zestData, String field, Object actual) {
        verifyCurrentTimeRule(zestData, field, false, 1000, actual);
    }

    public static void verifyCurrentTimeRule(ZestData zestData, String field, boolean nullable, int offset,
                                             Object actual) {
        new CurrentTimeRule(field, nullable, offset).verify(zestData, actual);
    }

    public static void verifyFromCurrentTimeRule(ZestData zestData, String field, int min, int max, int unit,
                                                 Object actual) {
        verifyFromCurrentTimeRule(zestData, field, false, min, max, unit, 1000, actual);
    }

    public static void verifyFromCurrentTimeRule(ZestData zestData, String field, boolean nullable, int min, int max,
                                                 int unit, int offset, Object actual) {
        new FromCurrentTimeRule(field, nullable, min, max, unit, offset).verify(zestData, actual);
    }
}
