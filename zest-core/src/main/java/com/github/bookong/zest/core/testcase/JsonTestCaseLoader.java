package com.github.bookong.zest.core.testcase;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;

import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.exceptions.ParseTestCaseException;
import com.github.bookong.zest.util.LoadTestCaseUtils;
import com.github.bookong.zest.util.ReflectHelper;

/**
 * @author jiangxu
 *
 */
public class JsonTestCaseLoader extends AbstractTestCaseLoader {

	@Override
	public TestCaseData load(File file, TestCaseData testCaseData) {
		try {
			JSONObject root = JSONObject.fromObject(FileUtils.readFileToString(file, "UTF-8"));
			testCaseData.setDesc(LoadTestCaseUtils.loadNotNullString(root, "desc"));
			loadCurrDbTimeDiff(root, testCaseData);
			testCaseData.loadInitData(root);
			loadParam(root, testCaseData);
			testCaseData.loadTargetDataRule(root);
			testCaseData.loadTargetData(root);
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to parse json : " + e.getMessage(), e);
		}

		return testCaseData;
	}

	private void loadParam(JSONObject root, TestCaseData testCaseData) throws SecurityException, NoSuchFieldException {
		try {
			JSONObject paramJson = root.getJSONObject("param");
			for (Object key : paramJson.keySet()) {
				loadTestParam(testCaseData.getParam(), (String) key, paramJson);
			}
			
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to parse part \"param\".", e);
		}
	}

	private void loadTestParam(Object parent, String fieldName, JSONObject paramValue) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = ReflectHelper.getFieldByFieldName(parent, fieldName);
		
		if ((paramValue.get(fieldName) instanceof JSONObject) && paramValue.getJSONObject(fieldName).isNullObject()) {
			ReflectHelper.setValueByFieldName(parent, fieldName, null);
			return;
		}
		
		Class<?> type = field.getType();
		if (type.isAssignableFrom(Long.class) || "long".equals(type.getName())) {
			ReflectHelper.setValueByFieldName(parent, fieldName, paramValue.getLong(fieldName));
			
		} else if (type.isAssignableFrom(Integer.class) || "int".equals(type.getName())) {
			ReflectHelper.setValueByFieldName(parent, fieldName, paramValue.getInt(fieldName));
		
		} else if (type.isAssignableFrom(Boolean.class) || "boolean".equals(type.getName())) {
			ReflectHelper.setValueByFieldName(parent, fieldName, paramValue.getBoolean(fieldName));
			
		} else if (type.isAssignableFrom(Double.class) || "double".equals(type.getName())) {
			ReflectHelper.setValueByFieldName(parent, fieldName, paramValue.getDouble(fieldName));
			
		} else if (type.isAssignableFrom(Float.class) || "float".equals(type.getName())) {
			ReflectHelper.setValueByFieldName(parent, fieldName, Double.valueOf(paramValue.getDouble(fieldName)).floatValue());
			
		} else if (type.isAssignableFrom(String.class)) {
			ReflectHelper.setValueByFieldName(parent, fieldName, paramValue.getString(fieldName));
			
		} else if (type.isAssignableFrom(Date.class)) {
			ReflectHelper.setValueByFieldName(parent, fieldName, LoadTestCaseUtils.parseDate(paramValue.getString(fieldName)));
			
		} else if (!type.isPrimitive() && !type.isArray() && !type.isEnum()) {
			Object memberClass = ReflectHelper.getValueByFieldName(parent, fieldName);
			JSONObject memberClassJson = paramValue.getJSONObject(fieldName);
			for (Object key : memberClassJson.keySet()) {
				String memberClassFieldName = (String) key;
				loadTestParam(memberClass, memberClassFieldName, memberClassJson);
			}
			
		} else {
			throw new ParseTestCaseException("Unsupported type : " + type.getName());
		}
	}

	private void loadCurrDbTimeDiff(JSONObject json, TestCaseData testCaseData) throws ParseException {
		String currDbTimeStr = LoadTestCaseUtils.loadNotNullString(json, "currDbTime").trim();
		Date currDbTime = LoadTestCaseUtils.parseDate(currDbTimeStr);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		testCaseData.setTestCaseRunningTime(cal.getTime());
		testCaseData.setCurrDbTimeDiff(cal.getTimeInMillis() - currDbTime.getTime());
	}
}