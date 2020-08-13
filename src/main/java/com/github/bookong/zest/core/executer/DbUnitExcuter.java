package com.github.bookong.zest.core.executer;

import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.thirdparty.dbunit.DbUnitDataSet;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import java.sql.Connection;

/**
 * 使用 DBUnit 的执行器
 * 
 * @author jiangxu
 */
public class DbUnitExcuter extends SqlExcuter {

    private IDatabaseConnection dbUnitConn;
    private DbUnitDataSet       zestDataSet;

    @Override
    public void initDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource dataSource) {
        try {
            zestDataSet = new DbUnitDataSet(testCaseData, dataSource);
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
