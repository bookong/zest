package com.github.bookong.zest.core.testcase.data;

import java.util.HashMap;
import java.util.Map;

public class ZestMetaData {
	/** 测试数据的描述 */
	private String desc;
	/** 对于日期类型，在插入数据库时是否需要做偏移处理 */
	private boolean transferTime = false;
	/** 如果日期需要偏移处理，当前时间与测试用例上描述的时间相差多少毫秒 */
	private long currDbTimeDiff;
	/** 各个数据库的具体内容 */
	private Map<String, Database> dataBases = new HashMap<String, Database>();
	/** 开始初始化数据库的时间 */
	private long initDBTime;
	/** 开始进行目标库检查的时间 */
	private long checkTargetDBTime;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isTransferTime() {
		return transferTime;
	}
	public void setTransferTime(boolean transferTime) {
		this.transferTime = transferTime;
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
	public void setDataBases(Map<String, Database> dataBases) {
		this.dataBases = dataBases;
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
}
