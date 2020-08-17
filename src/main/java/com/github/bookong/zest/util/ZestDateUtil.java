package com.github.bookong.zest.util;

import com.github.bookong.zest.testcase.ZestData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理
 * 
 * @author Jiang Xu
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
    public static Date getDateInZest(Date date, ZestData zestData) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);

        if (zestData.isTransferTime()) {
            cal.setTimeInMillis(cal.getTimeInMillis() + zestData.getCurrentTimeDiff());
        }

        return cal.getTime();
    }

}
