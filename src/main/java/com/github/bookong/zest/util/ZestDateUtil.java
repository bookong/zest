package com.github.bookong.zest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.bookong.zest.core.testcase.TestCaseData;

/**
 * 日期处理
 * 
 * @author jiangxu
 */
public class ZestDateUtil {

    private static final ThreadLocal<SimpleDateFormat> TYPE_NORMAL = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> TYPE_MINUTE = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> TYPE_HOUR   = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> TYPE_DAY    = new ThreadLocal<>();

    public static Date parseDate(String time) {
        try {
            return getDateFormat(time).parse(time);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatDateNormal(Date time) {
        return getDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }

    private static SimpleDateFormat getDateFormat(String time) {
        switch (time.length()) {
            case 19:
                return getDateFormat(TYPE_NORMAL, "yyyy-MM-dd HH:mm:ss");
            case 16:
                return getDateFormat(TYPE_MINUTE, "yyyy-MM-dd HH:mm");
            case 13:
                return getDateFormat(TYPE_HOUR, "yyyy-MM-dd HH");
            case 10:
                return getDateFormat(TYPE_DAY, "yyyy-MM-dd");
            default:
                throw new RuntimeException(Messages.parseDate(time));
        }
    }

    private static SimpleDateFormat getDateFormat(ThreadLocal<SimpleDateFormat> threadLocal, String pattern) {
        SimpleDateFormat obj = threadLocal.get();
        if (obj == null) {
            obj = new SimpleDateFormat(pattern);
            threadLocal.set(obj);
        }
        return obj;
    }

    /**
     * 计算要录入到数据库中的日期值（经过 currDbTimeDiff 修正过）
     * 
     * @param date 待转换的日期对象
     * @param testCaseData 测试用例数据
     * @return 转换后的日期对象
     */
    public static Date getDateInDB(Date date, TestCaseData testCaseData) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);

        if (testCaseData.isTransferTime()) {
            cal.setTimeInMillis(cal.getTimeInMillis() + testCaseData.getCurrDbTimeDiff());
        }

        return cal.getTime();
    }

    /**
     * 得到数据库中 Date 类型字段在通过 currDbTimeDiff 修正以后的字符串表示
     * 
     * @param date 待转换的日期对象
     * @param testCaseData 测试用例数据
     * @return 转换后字符串表达的日期
     * @see #getDateInDB(Date, TestCaseData)
     */
    public static String getStringFromDBDate(Date date, TestCaseData testCaseData) {
        return ZestDateUtil.formatDateNormal(getDateInDB(date, testCaseData));
    }

}
