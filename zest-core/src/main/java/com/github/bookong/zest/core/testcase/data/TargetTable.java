package com.github.bookong.zest.core.testcase.data;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.github.bookong.zest.exceptions.ParseTestCaseException;

/**
 * @author jiangxu
 *
 */
public class TargetTable extends InitTable {
	/** 验证目标表时为了和测试用例中的顺序一直，这里记录查询用的sql */
	private String targetTableQuerySql;
	/** 忽略对目标表的验证 */
	private boolean ignoreTargetTableVerify = false;
	
	public TargetTable(InitTable initTab) {
		getColDataTypes().putAll(initTab.getColDataTypes());
	}

	public void loadTargetDataRule(JSONObject jsonObject) {
		if (jsonObject.containsKey("ignore")) {
			ignoreTargetTableVerify = jsonObject.getBoolean("ignore");
		} else if (jsonObject.containsKey("querySql")) {
			targetTableQuerySql = jsonObject.getString("querySql");
		}
	}

	public void loadTargetData(String tabName, InitTable initTab, JSONObject jsonObject) {
		try {
			if (ignoreTargetTableVerify) {
				return;
			}
			
			if (jsonObject.containsKey(tabName)) {
				// 指定了要验证什么
				JSONArray tabJson = jsonObject.getJSONArray(tabName);
				for (int i = 0; i < tabJson.size(); i++) {
					JSONObject obj = tabJson.getJSONObject(i);
					loadRow(obj, getColDataTypes());
				}
			} else {
				// 未指定，且没设置忽略这个表的验证，则目标与初始内容一致
				for (LinkedHashMap<String, Object> rowData : initTab.getDatas()) {
					getDatas().add(rowData);
				}
			}
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to load target data. table name:" + tabName, e);
		}
	}

	public String getTargetTableQuerySql() {
		return targetTableQuerySql;
	}

	public boolean isIgnoreTargetTableVerify() {
		return ignoreTargetTableVerify;
	}

}
