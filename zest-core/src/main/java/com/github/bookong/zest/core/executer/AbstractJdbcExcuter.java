package com.github.bookong.zest.core.executer;

import java.sql.Connection;

import com.github.bookong.zest.core.testcase.data.DataBase;

/**
 * JDBC 的操作器
 * 
 * @author jiangxu
 *
 */
public abstract class AbstractJdbcExcuter extends AbstractExcuter {

	/**
	 * 初始化数据库中数据
	 * 
	 * @param connection
	 * @param database
	 * @param currDbTimeDiff
	 */
	public abstract void initDatabase(Connection connection, DataBase database, long currDbTimeDiff);

	/**
	 * 验证数据库中的数据
	 * 
	 * @param connection
	 * @param database
	 * @param currDbTimeDiff
	 */
	public abstract void checkTargetDatabase(Connection connection, DataBase database, long currDbTimeDiff);

}
