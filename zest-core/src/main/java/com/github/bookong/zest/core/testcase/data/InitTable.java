package com.github.bookong.zest.core.testcase.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.github.bookong.zest.exceptions.ParseTestCaseException;
import com.github.bookong.zest.util.LoadTestCaseUtils;

/**
 * @author jiangxu
 *
 */
public class InitTable {
	/** 所有出现过的数据的类型 */
	private Map<String, Class<?>> colDataTypes = new HashMap<String, Class<?>>();
	private List<LinkedHashMap<String, Object>> datas = new ArrayList<LinkedHashMap<String, Object>>();

	public void loadInitData(String tabName, JSONArray json) {
		try {
			for (int i = 0; i < json.size(); i++) {
				JSONObject obj = json.getJSONObject(i);
				loadRow(obj, colDataTypes);
			}

		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to load data. Table name:" + tabName, e);
		}
	}
	
	protected void loadRow(JSONObject json, Map<String, Class<?>> colDataTypes) {
		LinkedHashMap<String, Object> rowDatas = new LinkedHashMap<String, Object>();
		datas.add(rowDatas);
		
		for (Object colName : json.keySet()) {
			Object jsonData = json.get(colName);
			Object colData = LoadTestCaseUtils.loadColData(colName.toString(), jsonData, colDataTypes);
			rowDatas.put(colName.toString(), colData);
		}
	}
	
	public List<LinkedHashMap<String, Object>> getDatas() {
		return datas;
	}
	
	public Map<String, Class<?>> getColDataTypes() {
		return colDataTypes;
	}
}
