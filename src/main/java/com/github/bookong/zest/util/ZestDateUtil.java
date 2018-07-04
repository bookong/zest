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

    private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_CACHE = new ThreadLocal<Map<String, SimpleDateFormat>>();

    /**
     * 以格式 (yyyy-MM-dd HH:mm:ss) 解析时间
     * 
     * @param time 待处理的时间字符串
     * @return 解析后的日期对象
     */
    public static Date parseNormalDate(String time) {
        try {
            return getDateFormat(DateFormatType.NORMAL_DATE).parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 以格式 (yyyy-MM-dd HH:mm:ss) 格式化日期
     * 
     * @param time 待处理的时间对象
     * @return 格式化后的字符串
     */
    public static String formatDateNormal(Date time) {
        return getDateFormat(DateFormatType.NORMAL_DATE).format(time);
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

    /**
     * 得到指定的格式化对象
     * 
     * @param formatType 格式化类型
     * @return 返回格式化对象
     */
    public static SimpleDateFormat getDateFormat(DateFormatType formatType) {
        Map<String, SimpleDateFormat> map = DATE_FORMAT_CACHE.get();
        if (map == null) {
            map = new HashMap<String, SimpleDateFormat>();
        }

        SimpleDateFormat dateFormat = map.get(formatType.getFormatType());
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(formatType.getFormatType());
            map.put(formatType.getFormatType(), dateFormat);
        }

        return dateFormat;
    }

    /**
     * 格式化对象枚举值
     * 
     * @author jiangxu
     */
    public static enum DateFormatType {
                                       /** yyyy-MM-dd HH:mm:ss */
                                       NORMAL_DATE("yyyy-MM-dd HH:mm:ss"),
                                       /** yyyy-MM-dd HH:mm:ss.SSS */
                                       FULL_DATE("yyyy-MM-dd HH:mm:ss.SSS"),
                                       /** yyyy-MM-dd */
                                       DAY_DATE("yyyy-MM-dd");

        private String formatType;

        private DateFormatType(String dateFormatType){
            this.formatType = dateFormatType;
        }

        /**
         * 获得指定枚举值对应的格式化字符串
         * 
         * @return 返回格式化字符串
         */
        public String getFormatType() {
            return formatType;
        }

    }
}
