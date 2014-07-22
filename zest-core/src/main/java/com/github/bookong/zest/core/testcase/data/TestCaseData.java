package com.github.bookong.zest.core.testcase.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import com.github.bookong.zest.exceptions.ParseTestCaseException;

/**
 * 测试数据
 * 
 * @author jiangxu
 *
 */
public class TestCaseData {
	private String desc;
	private Date testCaseRunningTime;
	private long currDbTimeDiff;
	private Map<String, DataBase> dataBases = new HashMap<String, DataBase>();
	private TestParam param;

	/** 加载初始化数据库用的数据 */
	public void loadInitData(JSONObject root) {
		try {
			JSONObject initJson = root.getJSONObject("init");
			for (Object key : initJson.keySet()) {
				String dbName = (String) key;
				DataBase db = new DataBase();
				db.loadInitData(dbName, initJson.getJSONObject(dbName));
				dataBases.put(dbName, db);
			}
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to parse part \"init\".", e);
		}
	}
	
	/** 加载测试结果的验证规则 */
	public void loadTargetDataRule(JSONObject root) {
		try {
			JSONObject targetRuleJson = root.getJSONObject("targetRule");
			if (targetRuleJson != null) {
				for (Object key : targetRuleJson.keySet()) {
					String dbName = (String) key;
					DataBase db = dataBases.get(dbName);
					if (db == null) {
						throw new ParseTestCaseException("Unknown database name:" + dbName);
					}
					db.loadTargetDataRule(dbName, targetRuleJson.getJSONObject(dbName));
				}
			}
		} catch (Exception e) {
			throw new ParseTestCaseException("Fail to parse part \"targetRule\".", e);
		}
	}
	
	/** 加载测试后目标库 */
	public void loadTargetData(JSONObject root) {
		try {
			JSONObject targetJson = root.getJSONObject("target");
			if (targetJson != null) {
				for (Object key : targetJson.keySet()) {
					String dbName = (String) key;
					DataBase db = dataBases.get(dbName);
					if (db == null) {
						throw new ParseTestCaseException("Unknown database name:" + dbName);
					}
					db.loadTarget(dbName, targetJson.getJSONObject(dbName));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to parse part \"target\".", e);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("desc:").append(desc).append("\n");
		buff.append("currDbTimeDiff:").append(currDbTimeDiff).append("\n");
		buff.append("dataBases:\n");
		for (Entry<String, DataBase> entry : dataBases.entrySet()) {
			buff.append("\tdata base:").append(entry.getKey()).append("\n");
			buff.append(entry.getValue().toString()).append("\n");
		}
		buff.append("param:").append(param);
		return buff.toString();
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public TestParam getParam() {
		return param;
	}

	public void setParam(TestParam param) {
		this.param = param;
	}

	public long getCurrDbTimeDiff() {
		return currDbTimeDiff;
	}

	public void setCurrDbTimeDiff(long currDbTimeDiff) {
		this.currDbTimeDiff = currDbTimeDiff;
	}

	public Map<String, DataBase> getDataBases() {
		return dataBases;
	}

	public Date getTestCaseRunningTime() {
		return testCaseRunningTime;
	}

	public void setTestCaseRunningTime(Date testCaseRunningTime) {
		this.testCaseRunningTime = testCaseRunningTime;
	}
}
