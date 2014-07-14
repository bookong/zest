package com.github.bookong.zest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.github.bookong.zest.exceptions.ParseTestCaseException;

/**
 * @author jiangxu
 *
 */
public class LoadTestCaseUtils {

	/** 加载列数据 */
	public static Object loadColData(String colName, Object jsonData, Map<String, Class<?>> colDataTypes) {
		try {
			Class<?> colClazz = colDataTypes.get(colName);
			String colTypeDesc = "";
			if (colClazz == null) {
				if (!(jsonData instanceof JSONObject)) {
					throw new RuntimeException("You must specify the type of table fields.");
				}

				JSONObject colData = (JSONObject) jsonData;
				if (colData.keySet().size() <= 0) {
					throw new RuntimeException("You must specify the type of table fields.");
				}

				colTypeDesc = String.valueOf(colData.keys().next());
				colClazz = parseColClazz(colTypeDesc, colData);
				colDataTypes.put(colName, colClazz);
			}

			if (jsonData instanceof JSONObject) {
				JSONObject colData = (JSONObject) jsonData;
				if (StringUtils.isBlank(colTypeDesc)) {
					colTypeDesc = String.valueOf(colData.keys().next());
				}
				return parseColData(colTypeDesc, colData, colClazz);
			} else {
				return parseColData(jsonData, colClazz);
			}
		} catch (ParseTestCaseException e) {
			throw new ParseTestCaseException("Fail to load data. Column name:" + colName, e);
		}
	}

	public static Date parseDate(String value) {
		try {
			switch (value.length()) {
			case 23:
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(value);
			case 19:
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
			case 16:
				return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value);
			case 13:
				return new SimpleDateFormat("yyyy-MM-dd HH").parse(value);
			case 10:
				return new SimpleDateFormat("yyyy-MM-dd").parse(value);
			default:
				throw new ParseTestCaseException("The date format \"" + value + "\" is not supported.");
			}
		} catch (ParseException e) {
			throw new ParseTestCaseException("Fail to parse date filed.", e);
		}
	}

	public static String loadNotNullString(JSONObject json, String key) {
		if (json.containsKey(key)) {
			return json.getString(key);
		} else {
			throw new ParseTestCaseException("\"" + key + "\" must be set.");
		}
	}

	private static Class<?> parseColClazz(String colTypeDesc, JSONObject colData) {
		Class<?> clazz = String.class;
		if (String.class.getSimpleName().equalsIgnoreCase(colTypeDesc)) {
			clazz = String.class;
		} else if (Long.class.getSimpleName().equalsIgnoreCase(colTypeDesc)
				|| Integer.class.getSimpleName().equalsIgnoreCase(colTypeDesc)) {
			clazz = Long.class;
		} else if (Double.class.getSimpleName().equalsIgnoreCase(colTypeDesc)
				|| Float.class.getSimpleName().equalsIgnoreCase(colTypeDesc)
				|| Number.class.getSimpleName().equalsIgnoreCase(colTypeDesc)) {
			clazz = Double.class;
		} else if (Date.class.getSimpleName().equalsIgnoreCase(colTypeDesc)) {
			clazz = Date.class;
		} else {
			throw new RuntimeException("Unknown field type " + colTypeDesc);
		}
		return clazz;
	}

	private static Object parseColData(String colTypeDesc, JSONObject colData, Class<?> colClazz) {
		Object value = colData.get(colTypeDesc);
		if (value instanceof JSONObject && ((JSONObject) value).isNullObject()) {
			return null;
		}

		if (colClazz.isAssignableFrom(String.class)) {
			return colData.getString(colTypeDesc);
		} else if (colClazz.isAssignableFrom(Long.class)) {
			return colData.getLong(colTypeDesc);
		} else if (colClazz.isAssignableFrom(Double.class)) {
			return colData.getDouble(colTypeDesc);
		} else if (colClazz.isAssignableFrom(Date.class)) {
			return parseDate(colData.getString(colTypeDesc).trim());
		} else {
			throw new RuntimeException("Unknown field type " + colTypeDesc);
		}
	}

	private static Object parseColData(Object jsonData, Class<?> colClazz) {
		if (jsonData == null) {
			return null;
		}

		if (colClazz.isAssignableFrom(String.class)) {
			if (jsonData instanceof String) {
				return (String) jsonData;
			}
			throw new RuntimeException("Field type must be String, but now is " + jsonData.getClass().getName());
		} else if (colClazz.isAssignableFrom(Long.class)) {
			if ((jsonData instanceof Long) || (jsonData instanceof Integer)) {
				return ((Number) jsonData).longValue();
			}
			throw new RuntimeException("Field type must be Long or Integer, but now is "
					+ jsonData.getClass().getName());
		} else if (colClazz.isAssignableFrom(Double.class)) {
			if ((jsonData instanceof Double) || (jsonData instanceof Float) || (jsonData instanceof Number)) {
				return ((Number) jsonData).doubleValue();
			}
			throw new RuntimeException("Field type must be Double Float or Number, but now is "
					+ jsonData.getClass().getName());
		} else if (colClazz.isAssignableFrom(Date.class)) {
			if (jsonData instanceof String) {
				return parseDate(((String) jsonData).trim());
			}
			throw new RuntimeException("Field type must be String, but now is " + jsonData.getClass().getName());
		} else {
			throw new RuntimeException("Unknown field type " + colClazz.getClass().getName());
		}
	}
}
