package com.github.bookong.zest.core.testcase.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.bookong.zest.exceptions.ParseTestCaseException;

import net.sf.json.JSONObject;

/**
 * 数据库
 * 
 * @author jiangxu
 *
 */
public class DataBase {
	private Map<String, InitTable> initTables = new HashMap<String, InitTable>();
	private Map<String, TargetTable> targetTables = new HashMap<String, TargetTable>();

	/** 是否忽略对目标数据库的验证 */
	private boolean ignoreTargetDbVerify = false;

	/** 加载初始化数据库的数据 */
	public void loadInitData(String dbName, JSONObject json) {
		try {
			for (Object key : json.keySet()) {
				String tabName = (String) key;
				InitTable tab = new InitTable();
				tab.loadInitData(tabName, json.getJSONArray(tabName));
				initTables.put(tabName, tab);
			}
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to load init data. Database name:" + dbName, e);
		}
	}

	/** 加载目标库验证规则 */
	public void loadTargetDataRule(String dbName, JSONObject jsonObject) {
		try {
			if (jsonObject.containsKey("ignore") && jsonObject.getBoolean("ignore")) {
				ignoreTargetDbVerify = true;
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
			throw new ParseTestCaseException("Fail to load target data rule. database name:" + dbName, e);
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
			throw new ParseTestCaseException("Fail to load target data. database name:" + dbName, e);
		}
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (Entry<String, InitTable> entry : initTables.entrySet()) {
			buff.append("\t\tinit table:" + entry.getKey()).append("\n");
			InitTable tab = entry.getValue();
			for (LinkedHashMap<String, Object> datas : tab.getDatas()) {
				buff.append("\t\t\trow\n");
				for (Entry<String, Object> obj : datas.entrySet()) {
					Class<?> dataType = tab.getColDataTypes().get(obj.getKey());
					Object colData = obj.getValue();

					if (colData != null && (colData instanceof Date)) {
						buff.append(
								"\t\t\t\tcol:" + obj.getKey() + ", type:" + dataType.getName() + ", value:"
										+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date) colData))
								.append("\n");
					} else {
						buff.append(
								"\t\t\t\tcol:" + obj.getKey() + ", type:" + dataType.getName() + ", value:" + colData)
								.append("\n");
					}

				}
			}
		}
		
		for (Entry<String, TargetTable> entry : targetTables.entrySet()) {
			buff.append("\t\ttarget table:" + entry.getKey()).append("\n");
			TargetTable tab = entry.getValue();
			if (tab.isIgnoreTargetTableVerify()) {
				buff.append("\t\t\tignore table data\n");
				continue;
			}
			
			buff.append("\t\t\tquery sql:").append(tab.getTargetTableQuerySql()).append("\n");
			
			for (LinkedHashMap<String, Object> datas : tab.getDatas()) {
				buff.append("\t\t\trow\n");
				for (Entry<String, Object> obj : datas.entrySet()) {
					Class<?> dataType = tab.getColDataTypes().get(obj.getKey());
					Object colData = obj.getValue();

					if (colData != null && (colData instanceof Date)) {
						buff.append(
								"\t\t\t\tcol:" + obj.getKey() + ", type:" + dataType.getName() + ", value:"
										+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date) colData))
								.append("\n");
					} else {
						buff.append(
								"\t\t\t\tcol:" + obj.getKey() + ", type:" + dataType.getName() + ", value:" + colData)
								.append("\n");
					}

				}
			}
		}

		buff.append("\t\tignoreTargetDbVerify:").append(ignoreTargetDbVerify);
		return buff.toString();
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
	
}
