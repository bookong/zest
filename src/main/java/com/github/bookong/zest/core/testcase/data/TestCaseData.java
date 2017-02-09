package com.github.bookong.zest.core.testcase.data;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.github.bookong.zest.core.Launcher;

/**
 * 测试用的所有数据
 * 
 * @author jiangxu
 *
 */
public class TestCaseData {
	private static final String JSON_KEY_INIT = "init";
	private static final String JSON_KEY_TARGET = "target";
	private static final String JSON_KEY_TARGET_RULE = "targetRule";
	
	private ZestMetaData zestMetaData;
	
	/** JSON 文件名 */
	private String jsonFileName;
	/** 测试数据的描述 */
	@Deprecated
	private String desc;
	/** 对于日期类型，在插入数据库时是否需要做偏移处理 */
	@Deprecated
	private boolean transferTime = false;
	/** 如果日期需要偏移处理，当前时间与测试用例上描述的时间相差多少毫秒 */
	@Deprecated
	private long currDbTimeDiff;
	/** 各个数据库的具体内容 */
	@Deprecated
	private Map<String, Database> dataBases = new HashMap<String, Database>();
	/** 测试参数 */
	private TestParam param;
	/** 开始初始化数据库的时间 */
	@Deprecated
	private long initDBTime;
	/** 开始进行目标库检查的时间 */
	@Deprecated
	private long checkTargetDBTime;
	
	/** 加载 json 文件中 init, target, targetRule 部分内容 */
	public void load(Launcher zestLauncher, JSONObject root) {
		loadInitData(zestLauncher, root);
		loadTargetDataRule(root);
		loadTargetData(root);
	}

	/** 加载初始化数据库用的数据 */
	private void loadInitData(Launcher zestLauncher, JSONObject root) {
		try {
			JSONObject initJson = root.getJSONObject(JSON_KEY_INIT);
			for (Object key : initJson.keySet()) {
				String dbName = (String) key;
				
				Connection conn = zestLauncher.getJdbcConn(dbName);
				Database db = new Database();
				db.loadInitData(conn, dbName, initJson.getJSONObject(dbName));
				dataBases.put(dbName, db);
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to parse part \"" + JSON_KEY_INIT + "\".", e);
		}
	}
	
	/** 加载测试结果的验证规则 */
	private void loadTargetDataRule(JSONObject root) {
		try {
			JSONObject targetRuleJson = root.getJSONObject(JSON_KEY_TARGET_RULE);
			if (targetRuleJson != null) {
				for (Object key : targetRuleJson.keySet()) {
					String dbName = (String) key;
					Database db = dataBases.get(dbName);
					if (db == null) {
						throw new RuntimeException("Unknown database name:" + dbName);
					}
					db.loadTargetDataRule(dbName, targetRuleJson.getJSONObject(dbName));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to parse part \"" + JSON_KEY_TARGET_RULE + "\".", e);
		}
	}
	
	/** 加载测试后目标库 */
	private void loadTargetData(JSONObject root) {
		try {
			JSONObject targetJson = root.getJSONObject(JSON_KEY_TARGET);
			if (targetJson != null) {
				for (Object key : targetJson.keySet()) {
					String dbName = (String) key;
					Database db = dataBases.get(dbName);
					if (db == null) {
						throw new RuntimeException("Unknown database name:" + dbName);
					}
					db.loadTarget(dbName, targetJson.getJSONObject(dbName));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to parse part \"" + JSON_KEY_TARGET + "\".", e);
		}
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

	public Map<String, Database> getDataBases() {
		return dataBases;
	}

	public boolean isTransferTime() {
		return transferTime;
	}

	public void setTransferTime(boolean transferTime) {
		this.transferTime = transferTime;
	}

	public long getInitDBTime() {
		return initDBTime;
	}

	public void setInitDBTime(long initDBTime) {
		this.initDBTime = initDBTime;
	}

	public long getCheckTargetDBTime() {
		return checkTargetDBTime;
	}

	public void setCheckTargetDBTime(long checkTargetDBTime) {
		this.checkTargetDBTime = checkTargetDBTime;
	}

	public ZestMetaData getZestMetaData() {
		return zestMetaData;
	}

	public void setZestMetaData(ZestMetaData zestMetaData) {
		this.zestMetaData = zestMetaData;
	}

    
    public String getJsonFileName() {
        return jsonFileName;
    }

    
    public void setJsonFileName(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

}
