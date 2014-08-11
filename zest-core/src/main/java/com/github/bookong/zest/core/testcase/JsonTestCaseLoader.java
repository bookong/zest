package com.github.bookong.zest.core.testcase;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bookong.zest.core.Launcher;
import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.exceptions.ParseTestCaseException;
import com.github.bookong.zest.util.LoadTestCaseUtils;
import com.github.bookong.zest.util.ReflectHelper;

/**
 * JSON 格式的测试数据加载器
 * 
 * @author jiangxu
 *
 */
public class JsonTestCaseLoader  {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String JSON_ERROR_STR_AT_CHARACTER = "at character";
	private static final String JSON_ERROR_STR_OF = "of";
	
	private static final String JSON_KEY_DESC = "desc";
	private static final String JSON_KEY_CURR_DB_TIME = "currDbTime";
	private static final String JSON_KEY_PARAM = "param";
	
	/** 从绝对路径加载 json 文件 */
	public void loadFromAbsolutePath(String filepath, TestCaseData testCaseData, Launcher zestLauncher) {
		File file = new File(filepath);
		String jsonFileContent = "";
		try {
			jsonFileContent = FileUtils.readFileToString(file, "UTF-8");
			JSONObject root = JSONObject.fromObject(jsonFileContent);
			
			loadDesc(testCaseData, root);
			loadCurrDbTime(testCaseData, root);
			testCaseData.load(zestLauncher, root);
			loadParam(root, testCaseData);
		} catch (JSONException e) {
			parseJSONException(e, jsonFileContent);
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to parse json " + filepath, e);
		}
	}
	
	/** 分析 json-lib 解析时报出的异常信息，用更可读的方式展示 */
	private void parseJSONException(JSONException e, String jsonFileContent) {
		String errMsg = e.getMessage();
		int pos1 = errMsg.indexOf(JSON_ERROR_STR_AT_CHARACTER);
		if (pos1 <= 0) {
			throw new ParseTestCaseException("Fail to parse json.");
		}
		
		int pos2 = 0;
		while(pos2 <= pos1) {
			pos2 = errMsg.indexOf(JSON_ERROR_STR_OF, pos2);
		}
		
		String desc = errMsg.substring(0, pos1);
		int charIdx = 0;
		try {
			charIdx = Integer.parseInt(errMsg.substring(pos1 + JSON_ERROR_STR_AT_CHARACTER.length(), pos2).trim());
		} catch (Exception e2) {
			throw new ParseTestCaseException("Fail to parse json.", e);
		}
		
		int lineNum = 0;
		StringBuilder actual = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		for (int i = 0; i < jsonFileContent.length(); i++) {
			char ch = jsonFileContent.charAt(i);
			if (i == charIdx - 1) {
				expected.append(desc);
			}
			
			actual.append(ch);
			expected.append(ch);
			
			if (ch == '\n') {
				lineNum++;
				
				if (i > charIdx) {
					break;
				} else {
					actual.setLength(0);
					expected.setLength(0);
				}
			}
		}
		
		Assert.assertEquals("Fail to parse json at line " + lineNum + ", at character " + charIdx, expected.toString().trim(), actual.toString().trim());
	}

	/** 加载参数部分 */
	private void loadParam(JSONObject root, TestCaseData testCaseData) throws SecurityException, NoSuchFieldException {
		try {
			JSONObject paramJson = root.getJSONObject(JSON_KEY_PARAM);
			for (Object key : paramJson.keySet()) {
				loadTestParam(testCaseData.getParam(), (String) key, paramJson);
			}

		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to parse part \"" + JSON_KEY_PARAM + "\".", e);
		}
	}

	/** 一个递归调用的方法，加载 param 部分的所有属性 */
	private void loadTestParam(Object parent, String fieldName, JSONObject paramValue) {
		try {
			Field field = ReflectHelper.getFieldByFieldName(parent, fieldName);
			if (field == null) {
				throw new RuntimeException("Can not find the field \"" + fieldName + 
						"\" of object \"" + parent.getClass().getName() + "\"");
			}
			
			Object fieldValue = paramValue.get(fieldName);
			if (fieldValue == null || JSONUtils.isNull(fieldValue)) {
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
				ReflectHelper.setValueByFieldName(parent, fieldName, 
						Double.valueOf(paramValue.getDouble(fieldName)).floatValue());

			} else if (type.isAssignableFrom(String.class)) {
				ReflectHelper.setValueByFieldName(parent, fieldName, paramValue.getString(fieldName));

			} else if (type.isAssignableFrom(Date.class)) {
				ReflectHelper.setValueByFieldName(parent, fieldName,
						LoadTestCaseUtils.parseDate(paramValue.getString(fieldName)));
				
			} else if (type.isAssignableFrom(List.class)) {
				loadTestParamList(parent, fieldName, paramValue, field);
			
			} else if (type.isAssignableFrom(Map.class)) {
				loadTestParamMap(parent, fieldName, paramValue, field);
				
			} else if (!type.isPrimitive() && !type.isArray() && !type.isEnum()) {
				Object memberClass = ReflectHelper.getValueByFieldName(parent, fieldName);
				if (memberClass == null) {
					memberClass = type.newInstance();
					ReflectHelper.setValueByFieldName(parent, fieldName, memberClass);
				}
				JSONObject memberClassJson = paramValue.getJSONObject(fieldName);
				for (Object key : memberClassJson.keySet()) {
					String memberClassFieldName = (String) key;
					loadTestParam(memberClass, memberClassFieldName, memberClassJson);
				}

			} else {
				throw new RuntimeException("Unsupported type : " + type.getName());
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Fail to load field \"" + fieldName + "\", value:\"" + paramValue + "\"", e);
		}
	}
	
	/** 加载参数部分中的 List 结构，List 内容不能是基本类型对应的类型，例如 Long, String 等 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadTestParamList(Object parent, String fieldName, JSONObject paramValue, Field field) {
		try {
			List list = (List)ReflectHelper.getValueByFieldName(parent, fieldName);
			if (list == null) {
				list = new ArrayList();
				ReflectHelper.setValueByFieldName(parent, fieldName, list);
			}
			
			Class paramType = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
			
			JSONArray listValue = paramValue.getJSONArray(fieldName);
			for(int i=0; i<listValue.size(); i++) {
				Object item = (paramType).newInstance();
				list.add(item);
				
				JSONObject itemValue = listValue.getJSONObject(i);
				for (Object key : itemValue.keySet()) {
					String memberClassFieldName = (String) key;
					loadTestParam(item, memberClassFieldName, itemValue);
				}
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Fail to load class List, field name:\"" + fieldName + "\", value:\"" + paramValue + "\"", e);
		}
	}
	
	/** 加载参数部分中的 Map 结构, Map 定义必须是 Map<String, SomeClass> */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadTestParamMap(Object parent, String fieldName, JSONObject paramValue, Field field) {
		try {
			Map map = (Map)ReflectHelper.getValueByFieldName(parent, fieldName);
			if (map == null) {
				map = new HashMap();
				ReflectHelper.setValueByFieldName(parent, fieldName, map);
			}
			
			Class paramKeyType = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
			
			if (!paramKeyType.isAssignableFrom(String.class)
					&& !paramKeyType.isAssignableFrom(Long.class)
					&& !paramKeyType.isAssignableFrom(Integer.class)
					&& !paramKeyType.isAssignableFrom(Double.class)
					&& !paramKeyType.isAssignableFrom(Float.class)) {
				
				throw new RuntimeException(
						"Map must be Map<String, SomeClass>, Map<Long, SomeClass>, Map<Integer, SomeClass>,"
								+ "Map<Double, SomeClass> or Map<Float, SomeClass> ");
			}
			
			Class paramValueType = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[1];
			JSONObject mapValue = paramValue.getJSONObject(fieldName);
			for (Object mapKey : mapValue.keySet()) {
				Object item = (paramValueType).newInstance();
				
				if (paramKeyType.isAssignableFrom(String.class)) {
					map.put(mapKey.toString(), item);
				} else if (paramKeyType.isAssignableFrom(Long.class)) {
					map.put(Long.valueOf(mapKey.toString()), item);
				} else if (paramKeyType.isAssignableFrom(Integer.class)) {
					map.put(Integer.valueOf(mapKey.toString()), item);
				} else if (paramKeyType.isAssignableFrom(Double.class)) {
					map.put(Double.valueOf(mapKey.toString()), item);
				} else if (paramKeyType.isAssignableFrom(Float.class)) {
					map.put(Float.valueOf(mapKey.toString()), item);
				} 
				
				JSONObject itemValue = mapValue.getJSONObject(mapKey.toString());
				for (Object key : itemValue.keySet()) {
					String memberClassFieldName = (String) key;
					loadTestParam(item, memberClassFieldName, itemValue);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to load class Map, field name:\"" + fieldName + "\", value:\"" + paramValue + "\"", e);
		}
		
	}

	/** 加载 currDbTime */
	private void loadCurrDbTime(TestCaseData testCaseData, JSONObject json) {
		if (!json.containsKey(JSON_KEY_CURR_DB_TIME)) {
			return;
		}
		
		try {
			String currDbTimeStr = json.getString(JSON_KEY_CURR_DB_TIME).trim();
			Date currDbTime = LoadTestCaseUtils.parseDate(currDbTimeStr);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			
			testCaseData.setTransferTime(true);
			testCaseData.setCurrDbTimeDiff(cal.getTimeInMillis() - currDbTime.getTime());
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to parse \"" + JSON_KEY_CURR_DB_TIME + "\"", e);
		}
	}
	
	/** 加载 desc */
	private void loadDesc(TestCaseData testCaseData, JSONObject json) {
		try {
			testCaseData.setDesc(json.getString(JSON_KEY_DESC));
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to parse \"" + JSON_KEY_DESC + "\"", e);
		}
	}
}