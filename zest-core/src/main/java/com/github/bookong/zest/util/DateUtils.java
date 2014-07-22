package com.github.bookong.zest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangxu
 *
 */
public class DateUtils {
	private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_CACHE = new ThreadLocal<Map<String, SimpleDateFormat>>();

	/** 以格式(yyyy-MM-dd HH:mm:ss)解析时间 */
	public static Date parseNormalDate(String time) {
		try {
			return getDateFormat(DataFormatType.NORMAL_DATE).parse(time);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/** 以格式(yyyy-MM-dd HH:mm:ss)格式化日期 */
	public static String formatDateNormal(Date date) {
		return getDateFormat(DataFormatType.NORMAL_DATE).format(date);
	}
	
	/** 计算要录入到数据库中的日期值（经过 currDbTimeDiff 修正过） */
	public static Date getDateInDB(Date date, long currDbTimeDiff) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.setTimeInMillis(cal.getTimeInMillis() + currDbTimeDiff);
		return cal.getTime();
	}
	
	/** 得到数据库中 Date 类型字段在通过 currDbTimeDiff 修正以后的字符串表示 */
	public static String getStringFromDBDate(Date date, long currDbTimeDiff) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.setTimeInMillis(cal.getTimeInMillis() - currDbTimeDiff);
		return DateUtils.formatDateNormal(cal.getTime());
	}

	public static SimpleDateFormat getDateFormat(DataFormatType formatType) {
		Map<String, SimpleDateFormat> map = DATE_FORMAT_CACHE.get();
		if (map == null) {
			map = new HashMap<String, SimpleDateFormat>();
		}
		
		SimpleDateFormat dateFormat = map.get(formatType.getFormatType());
		if (dateFormat == null) {
			dateFormat = formatType.genDateFormat();
			map.put(formatType.getFormatType(), dateFormat);
		}
		
		return dateFormat;
	}

	public static enum DataFormatType {
		/** yyyy-MM-dd HH:mm:ss */
		NORMAL_DATE("yyyy-MM-dd HH:mm:ss"),
		/** yyyy-MM-dd HH:mm:ss.SSS */
		FULL_DATE("yyyy-MM-dd HH:mm:ss.SSS"),
		/** yyyy-MM-dd */
		DAY_DATE("yyyy-MM-dd");

		private String formatType;

		private DataFormatType(String dataFormatType) {
			this.formatType = dataFormatType;
		}
		
		public String getFormatType() {
			return formatType;
		}

		public SimpleDateFormat genDateFormat() {
			return new SimpleDateFormat(formatType);
		}
	}
}
