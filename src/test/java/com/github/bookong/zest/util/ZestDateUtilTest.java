package com.github.bookong.zest.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Jiang Xu
 */
public class ZestDateUtilTest {

    /**
     * @see ZestDateUtil#parseDate(String)
     */
    @Test
    public void testParseDate1() {
        testParseDate("2020-08-09T07:08:09.000+0800", //
                      2020, 8, 9, //
                      7, 8, 9, 0, "Asia/Shanghai");
    }

    /**
     * @see ZestDateUtil#parseDate(String)
     */
    @Test
    public void testParseDate2() {
        testParseDate("2020-12-25T13:14:15.016+0800", //
                      2020, 12, 25, //
                      13, 14, 15, 16, "Asia/Shanghai");
    }

    /**
     * @see ZestDateUtil#parseDate(String)
     */
    @Test
    public void testParseDate3() {
        testParseDate("0001-01-01T00:00:00.000+0800", //
                      1, 1, 1, //
                      0, 0, 0, 0, "Asia/Shanghai");
    }

    /**
     * @see ZestDateUtil#formatDateNormal(Date)
     */
    @Test
    public void testFormatDateNormal1() {
        testFormatDateNormal(2020, 8, 9, //
                             7, 8, 9, 0, "Asia/Shanghai", //
                             "2020-08-09T07:08:09.000+0800");
    }

    /**
     * @see ZestDateUtil#formatDateNormal(Date)
     */
    @Test
    public void testFormatDateNormal2() {
        testFormatDateNormal(2020, 12, 25, //
                             13, 14, 15, 16, "Asia/Shanghai", //
                             "2020-12-25T13:14:15.016+0800");
    }

    /**
     * @see ZestDateUtil#formatDateNormal(Date)
     */
    @Test
    public void testFormatDateNormal3() {
        testFormatDateNormal(1, 1, 1, //
                             0, 0, 0, 0, "Asia/Shanghai", //
                             "0001-01-01T00:00:00.000+0800");
    }

    private void testParseDate(String time, int year, int month, int day, int hour, int minute, int second,
                               int millisecond, String timeZoneID) {
        Date date = ZestDateUtil.parseDate(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Assert.assertEquals(year, cal.get(Calendar.YEAR));
        Assert.assertEquals(month - 1, cal.get(Calendar.MONTH));
        Assert.assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(hour, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(minute, cal.get(Calendar.MINUTE));
        Assert.assertEquals(second, cal.get(Calendar.SECOND));
        Assert.assertEquals(millisecond, cal.get(Calendar.MILLISECOND));
        Assert.assertEquals(timeZoneID, cal.getTimeZone().getID());
    }

    private void testFormatDateNormal(int year, int month, int day, int hour, int minute, int second, int millisecond,
                                      String timeZoneID, String display) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        cal.setTimeZone(TimeZone.getTimeZone(timeZoneID));

        Assert.assertEquals(display, ZestDateUtil.formatDateNormal(cal.getTime()));
    }
}
