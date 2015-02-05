package com.github.bookong.zest.core.testcase.data;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

/**
 * 数据库
 * 
 * @author jiangxu
 *
 */
public class Database {
	private Map<String, InitTable> initTables = new HashMap<String, InitTable>();
	private Map<String, TargetTable> targetTables = new HashMap<String, TargetTable>();

	/** 数据库名称 */
	private String databaseName;
	/** 是否忽略对目标数据库的验证 */
	private boolean ignoreTargetDbVerify = false;

	/** 加载初始化数据库的数据 */
	public void loadInitData(Connection conn, String dbName, JSONObject json) {
		try {
			this.databaseName = dbName;
			
			for (Object key : json.keySet()) {
				String tabName = (String) key;
				InitTable tab = new InitTable(tabName);
				tab.loadInitData(conn, json.getJSONArray(tabName));
				initTables.put(tabName, tab);
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to load database \"" + dbName + "\"", e);
		}
	}

	/** 加载目标库验证规则 */
	public void loadTargetDataRule(String dbName, JSONObject jsonObject) {
		try {
			if (jsonObject.containsKey("ignore")) {
				ignoreTargetDbVerify = jsonObject.getBoolean("ignore");
				return;
			}

			for (Entry<String, InitTable> entry : initTables.entrySet()) {
				TargetTable targetTab = new TargetTable(entry.getValue());
				targetTables.put(entry.getKey(), targetTab);
				if (jsonObject.containsKey(entry.getKey())) {
					targetTab.loadTargetDataRule(jsonObject.getJSONObject(entry.getKey()));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to load target data rule \"" + dbName + "\"", e);
		}
	}
	
	/** 加载目标库数据 */
	public void loadTarget(String dbName, JSONObject jsonObject) {
		try {
			if (ignoreTargetDbVerify) {
				return;
			}
			
			for (Entry<String, InitTable> entry : initTables.entrySet()) {
				TargetTable targetTab = targetTables.get(entry.getKey());
				if (targetTab == null) {
					targetTab = new TargetTable(entry.getValue());
					targetTables.put(entry.getKey(), targetTab);
				}
				targetTab.loadTargetData(entry.getKey(), entry.getValue(), jsonObject);
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to load target database \"" + dbName + "\"", e);
		}
	}
	
	public Map<String, TargetTable> getTargetTables() {
		return targetTables;
	}

	public Map<String, InitTable> getInitTables() {
		return initTables;
	}

	public boolean isIgnoreTargetDbVerify() {
		return ignoreTargetDbVerify;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
}
