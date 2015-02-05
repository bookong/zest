package com.github.bookong.zest.runner;

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * @author jiangxu
 *
 */
public interface ZestClassRunner {

	Connection getConnection(DataSource dataSource);
	
	Object createTest() throws Exception;
}
