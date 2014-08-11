package com.github.bookong.zest.core.executer;

import java.sql.Connection;

import com.github.bookong.zest.core.testcase.data.Database;
import com.github.bookong.zest.core.testcase.data.TestCaseData;

/**
 * JDBC 的执行器
 * 
 * @author jiangxu
 *
 */
public abstract class AbstractJdbcExcuter extends AbstractExcuter {

	/** 初始化数据库中数据 */
	public abstract void initDatabase(Connection connection, TestCaseData testCaseData, Database database);

	/** 验证数据库中的数据 */
	public abstract void checkTargetDatabase(Connection connection, TestCaseData testCaseData, Database database);

}
