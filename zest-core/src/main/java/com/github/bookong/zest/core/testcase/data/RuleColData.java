package com.github.bookong.zest.core.testcase.data;

/**
 * @author jiangxu
 *
 */
public class RuleColData {
	/** 能否为空 */
	private boolean nullable = false;
	/** 正则表达式 */
	private String regExp;
	/** 是否是当前时间 */
	private Boolean currentTime;

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public Boolean getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Boolean currentTime) {
		this.currentTime = currentTime;
	}

	@Override
	public String toString() {
		return "RuleColData [nullable=" + nullable + ", regExp=" + regExp + ", currentTime=" + currentTime + "]";
	}
}
