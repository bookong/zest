package com.github.bookong.zest.runner;

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * @author Jiang Xu
 */
public interface ZestClassRunner {
	
    Object createTest() throws Exception;
}
