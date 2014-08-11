package com.github.bookong.zest.core;

import java.sql.Connection;

import org.junit.runners.model.TestClass;

import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.core.testcase.data.TestParam;


/**
 * @author jiangxu
 *
 */
public interface Launcher {
	
	/** 得到被测试类 */
	TestClass getTestObject();
	
	/** 读取当前指定的 test case 文件，并返回读取后的对象 */
	void loadCurrTestCaseFile(TestParam testParam);
	
	TestCaseData getCurrTestCaseData();
	
	/** 初始化DB中数据 */
	void initDb();
	
	/** 检查目标DB中数据是否符合预期 */
	void checkTargetDb();
	
	/** 获得 JDBC 的连接对象 */
	Connection getJdbcConn(String databaseName);
	
}
