package com.github.bookong.zest.core.testcase.data.rule;

import java.util.Date;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import com.github.bookong.zest.core.testcase.data.TestCaseData;

/**
 * 记录列验证规则的数据
 * 
 * @author jiangxu
 *
 */
public class ColumnRule {
	/** 是否满足指定的正则表达式 */
	private static final String JSON_KEY_REG_EXP = "regExp";
	/** 是否可为空 */
	private static final String JSON_KEY_NULLABLE = "nullable";
	/** 是数据库的当前时间, 与 fromCurrentTime 互斥 */
	private static final String JSON_KEY_CURRENT_TIME = "currentTime";
	/** 是距离当前一定范围内的时间值,与 currentTime 互斥 */
	private static final String JSON_KEY_FROM_CURRENT_TIME = "fromCurrentTime";
	
	private static final String[] allJsonKeys = {
		JSON_KEY_REG_EXP, JSON_KEY_NULLABLE, JSON_KEY_CURRENT_TIME, JSON_KEY_FROM_CURRENT_TIME
	};
	
	/** 能否为空 */
	private boolean nullable = false;
	/** 正则表达式 */
	private String regExp;
	/** 是否是当前时间 */
	private boolean currentTime;
	/** 是否是从当前时间开始计算的时间 */
	private FromCurrentTime fromCurrentTime;
	
	/** 分析是否是指定了列的验证规则 */
	public static ColumnRule parseColumnRule(JSONObject json) {
		if (!isColumnRuleJson(json)) {
			return null;
		}

		ColumnRule colData = new ColumnRule();

		if (json.containsKey(JSON_KEY_NULLABLE)) {
			colData.setNullable(json.getBoolean(JSON_KEY_NULLABLE));
		}

		if (json.containsKey(JSON_KEY_REG_EXP)) {
			colData.setRegExp(json.getString(JSON_KEY_REG_EXP).trim());
		}

		if (json.containsKey(JSON_KEY_CURRENT_TIME)) {
			colData.setCurrentTime(json.getBoolean(JSON_KEY_CURRENT_TIME));
		} else if (json.containsKey(JSON_KEY_FROM_CURRENT_TIME)) {
			colData.setFromCurrentTime(new FromCurrentTime(json.getJSONObject(JSON_KEY_FROM_CURRENT_TIME)));
		}

		return colData;
	}
	
	/** 验证数据 */
	public void verify(String baseMessage, TestCaseData testCaseData, Object actualColData) {
		if (!isNullable()) {
			Assert.assertNotNull(baseMessage + "must be NOT NULL", actualColData);
		}
		
		if (StringUtils.isNotBlank(getRegExp())) {
			Assert.assertTrue(baseMessage + "mush match regExp:\"" + getRegExp() + "\"",
					Pattern.matches(getRegExp().trim(), String.valueOf(actualColData)));
		}
		
		if (isCurrentTime()) {
			Assert.assertTrue(baseMessage + "must be Date", (actualColData instanceof Date));
			long tmp = ((Date) actualColData).getTime();
			Assert.assertTrue(baseMessage + "must be current time",
					(tmp >= testCaseData.getInitDBTime() && tmp <= testCaseData.getCheckTargetDBTime()));
			
		} else if (getFromCurrentTime() != null) {
			getFromCurrentTime().verify(baseMessage, testCaseData, actualColData);
			
		}
	}
	
	/** 这个 json 是否描述的是验证规则信息 */
	private static boolean isColumnRuleJson(JSONObject json) {
		if (json == null || JSONUtils.isNull(json)) {
			return false;
		}
		
		for (String jsonKey : allJsonKeys) {
			if (json.containsKey(jsonKey)) {
				return true;
			}
		}
		
		return false;
	}

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

	public boolean isCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(boolean currentTime) {
		this.currentTime = currentTime;
	}

	public FromCurrentTime getFromCurrentTime() {
		return fromCurrentTime;
	}

	public void setFromCurrentTime(FromCurrentTime fromCurrentTime) {
		this.fromCurrentTime = fromCurrentTime;
	}

	public static String getJsonKeyRegExp() {
		return JSON_KEY_REG_EXP;
	}

	public static String getJsonKeyNullable() {
		return JSON_KEY_NULLABLE;
	}

	public static String getJsonKeyCurrentTime() {
		return JSON_KEY_CURRENT_TIME;
	}
}
