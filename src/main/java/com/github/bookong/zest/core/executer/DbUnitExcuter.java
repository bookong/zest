package com.github.bookong.zest.core.executer;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;

import com.github.bookong.zest.core.testcase.AbstractDataSourceTable;
import com.github.bookong.zest.core.testcase.SqlDataSourceRow;
import com.github.bookong.zest.core.testcase.SqlDataSourceTable;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.thirdparty.dbunit.DbUnitDataSet;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestSqlHelper;

/**
 * 使用 DBUnit 的执行器
 * 
 * @author jiangxu
 */
public class DbUnitExcuter extends SqlExcuter {

    private IDatabaseConnection dbUnitConn;
    private DbUnitDataSet       zestDataSet;

    @Override
    public void initDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) {
        try {
            zestDataSet = new DbUnitDataSet(testCaseData, testCaseDataSource);
            dbUnitConn = new DatabaseConnection(conn);
            dbUnitConn.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            dbUnitConn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

            DatabaseOperation.INSERT.execute(dbUnitConn, zestDataSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource dataSource) {
        try {
            DatabaseOperation.TRUNCATE_TABLE.execute(dbUnitConn, zestDataSet);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
