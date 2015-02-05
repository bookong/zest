package com.github.bookong.zest.core.testcase.data;

import java.util.LinkedHashMap;

import com.github.bookong.zest.core.testcase.data.rule.ColumnRule;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 目标库相关数据
 * 
 * @author jiangxu
 *
 */
public class TargetTable extends AbstractTable {
	/** 验证目标表时为了和测试用例中的顺序一直，这里记录查询用的sql */
	private String targetTableQuerySql;
	/** 忽略对目标表的验证 */
	private boolean ignoreTargetTableVerify = false;

	public TargetTable(InitTable initTab) {
		super(initTab.getTableName());
		this.getColumnMetaDatas().putAll(initTab.getColumnMetaDatas());
	}

	/** 加载目标库规则 */
	public void loadTargetDataRule(JSONObject jsonObject) {
		if (jsonObject.containsKey("ignore")) {
			ignoreTargetTableVerify = jsonObject.getBoolean("ignore");
		} else if (jsonObject.containsKey("querySql")) {
			targetTableQuerySql = jsonObject.getString("querySql");
		}
	}

	/** 加载目标表数据 */
	public void loadTargetData(String tabName, InitTable initTab, JSONObject jsonObject) {
		try {
			if (ignoreTargetTableVerify) {
				return;
			}

			if (jsonObject.containsKey(tabName)) {
				// 指定了要验证什么
				JSONArray tabJson = jsonObject.getJSONArray(tabName);
				for (int i = 0; i < tabJson.size(); i++) {
					loadRow(tabJson.getJSONObject(i));
				}
			} else {
				// 未指定，且没设置忽略这个表的验证，则目标与初始内容一致
				for (LinkedHashMap<String, Object> rowData : initTab.getDatas()) {
					getDatas().add(rowData);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to load target table \"" + tabName + "\"", e);
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
				Object colData = loadColData(colName.toString(), jsonData);
				rowDatas.put(colName.toString(), colData);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Fail to load table row \"" + json + "\"", e);
		}
	}

	@Override
	protected Object loadColData(String colName, Object data) {
		try {
			if (data != null && (data instanceof JSONObject)) {
				ColumnRule cr = ColumnRule.parseColumnRule((JSONObject) data);
				if (cr != null) {
					return cr;
				}
			}

			return super.loadColData(colName, data);
			
		} catch (Exception e) {
			throw new RuntimeException("Fail to load column \"" + colName + "\", data:\"" + data + "\"", e);
		}
	}
	
	public String getTargetTableQuerySql() {
		return targetTableQuerySql;
	}

	public boolean isIgnoreTargetTableVerify() {
		return ignoreTargetTableVerify;
	}

}
