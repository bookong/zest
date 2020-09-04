package com.github.bookong.zest.util;

import com.github.bookong.zest.exception.ZestException;
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

    private static final ThreadLocal<SimpleDateFormat> TYPE_FULL = new ThreadLocal<>();

    public static Date parseDate(String time) {
        try {
            return getDateFormatFull().parse(time);
        } catch (Exception e) {
            throw new ZestException(Messages.parseDataDate(time), e);
        }
    }

    public static String formatDateNormal(Date time) {
        return getDateFormatFull().format(time);
    }

    private static SimpleDateFormat getDateFormatFull() {
        SimpleDateFormat obj = TYPE_FULL.get();
        if (obj == null) {
            obj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            TYPE_FULL.set(obj);
        }
        return obj;
    }

    /**
     * 计算要录入到数据库中的日期值（经过 currDbTimeDiff 修正过）
     *
     * @param zestData 测试用例数据
     * @param date 待转换的日期对象
     * @return 转换后的日期对象
     */
    public static Date getDateInZest(ZestData zestData, Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);

        if (zestData.isTransferTime()) {
            cal.setTimeInMillis(cal.getTimeInMillis() + zestData.getCurrentTimeDiff());
        }

        return cal.getTime();
    }

}
