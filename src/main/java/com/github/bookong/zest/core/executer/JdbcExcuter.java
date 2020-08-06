package com.github.bookong.zest.core.executer;

import java.sql.Connection;

import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;

/**
 * JDBC 的执行器
 * 
 * @author jiangxu
 */
public class JdbcExcuter extends AbstractExcuter {

    /**
     * 初始化数据库中数据
     * 
     * @param conn
     * @param testCaseData
     * @param testCaseDataSource
     */
    public void initDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) {

    }

    /**
     * 验证数据库中的数据
     * 
     * @param conn
     * @param testCaseData
     * @param testCaseDataSource
     */
    public void checkTargetDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) {

    }

    /**
     * 清空初始化的数据
     * 
     * @param conn
     * @param testCaseData
     * @param testCaseDataSource
     */
    public void clearDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) {

    }

}
