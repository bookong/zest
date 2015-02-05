package com.github.bookong.zest.core.testcase.data;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 数据表
 * 
 * @author jiangxu
 *
 */
public class InitTable extends AbstractTable{
	
	public InitTable(String tableName) {
		super(tableName);
	}

	/** 加载初始数据 */
	public void loadInitData(Connection conn, JSONArray json) {
		try {
			if (conn != null) {
				loadColumnMetaDatas(conn);
			}
			
			for (int i = 0; i < json.size(); i++) {
				loadRow(json.getJSONObject(i));
			}

		} catch (Exception e) {
			throw new RuntimeException("Fail to load table \"" + getTableName() + "\"", e);
		}
	}
	
	/** 加载每行数据 */
	private void loadRow(JSONObject json) {
		try {
			LinkedHashMap<String, Object> rowDatas = new LinkedHashMap<String, Object>();
			getDatas().add(rowDatas);
			
			for (Object key : json.keySet()) {
				String colName = (String)key;
				Object jsonData = json.get(colName);
				Object colData = loadColData(colName, jsonData);
				rowDatas.put(colName, colData);
			}
			
			for (Entry<String, ColumnMetaData> entry : getColumnMetaDatas().entrySet()) {
				Object colData = rowDatas.get(entry.getKey());
				if (!entry.getValue().isNullable() && colData == null) {
					throw new RuntimeException("Column \"" + entry.getKey() + "\" must not be null");
				}
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Fail to load table row \"" + json + "\"", e);
		}
	}
}
