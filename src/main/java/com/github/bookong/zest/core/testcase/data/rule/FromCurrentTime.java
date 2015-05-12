package com.github.bookong.zest.core.testcase.data.rule;

import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONObject;

import org.junit.Assert;

import com.github.bookong.zest.core.testcase.data.TestCaseData;

/**
 * 距离当前时间多久范围，例如 valid_end_time 字段是距离当前时间 30 天到 31 天之间:
 * 
 * <pre>
 * valid_end_time : {fromCurrentTime:{min:30, max:31, unit:"day"}}
 * </pre>
 * 
 * @author jiangxu
 */
public class FromCurrentTime {

    /** 时间单位：年 */
    public static final String YEAR        = "year";
    /** 时间单位：月 */
    public static final String MONTH       = "month";
    /** 时间单位：日 */
    public static final String DAY_OF_YEAR = "day";
    /** 时间单位：时 */
    public static final String HOUR_OF_DAY = "hour";
    /** 时间单位：分 */
    public static final String MINUTE      = "minute";
    /** 时间单位：秒 */
    public static final String SECOND      = "second";

    /** 时间单位 */
    private int                unit        = Calendar.DAY_OF_YEAR;
    /** 最小值 */
    private int                min;
    /** 最大值 */
    private int                max;

    public FromCurrentTime(JSONObject json){
        String str = json.getString("unit");
        if (YEAR.equalsIgnoreCase(str)) {
            unit = Calendar.YEAR;

        } else if (MONTH.equalsIgnoreCase(str)) {
            unit = Calendar.MONTH;

        } else if (DAY_OF_YEAR.equalsIgnoreCase(str)) {
            unit = Calendar.DAY_OF_YEAR;

        } else if (HOUR_OF_DAY.equalsIgnoreCase(str)) {
            unit = Calendar.HOUR_OF_DAY;

        } else if (MINUTE.equalsIgnoreCase(str)) {
            unit = Calendar.MINUTE;

        } else if (SECOND.equalsIgnoreCase(str)) {
            unit = Calendar.SECOND;

        }

        min = json.getInt("min");
        max = json.getInt("max");
    }

    /** 验证 */
    public void verify(String baseMessage, TestCaseData testCaseData, Object actualColData) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(testCaseData.getInitDBTime());
        cal.add(unit, min);
        long expectedMin = cal.getTimeInMillis();

        cal.setTimeInMillis(testCaseData.getCheckTargetDBTime());
        cal.add(unit, max);
        long expectedMax = cal.getTimeInMillis();

        long tmp = 0;
        if (actualColData instanceof Date) {
            tmp = ((Date) actualColData).getTime();
        } else if (actualColData instanceof Long) {
            tmp = ((Long) actualColData).longValue();
        } else {
            Assert.assertTrue(baseMessage + "must be Date or Long", false);
        }

        Assert.assertTrue(baseMessage + "the distance between the current time " + min + "-" + max + " "
                                  + getUnitDesc() + ", expect [" + expectedMin + "," + expectedMax + "] actual " + tmp,
                          (tmp >= expectedMin && tmp <= expectedMax));
    }

    /** 时间单位的描述 */
    private String getUnitDesc() {
        switch (unit) {
            case Calendar.YEAR:
                return "years";
            case Calendar.MONTH:
                return "months";
            case Calendar.DAY_OF_YEAR:
                return "days";
            case Calendar.HOUR_OF_DAY:
                return "hours";
            case Calendar.MINUTE:
                return "minutes";
            case Calendar.SECOND:
                return "seconds";
            default:
                return "";
        }
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

}
