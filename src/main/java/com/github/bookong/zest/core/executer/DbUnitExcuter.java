package com.github.bookong.zest.core.executer;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bookong.zest.core.testcase.AbstractDataSourceTable;
import com.github.bookong.zest.core.testcase.RmdbDataSourceRow;
import com.github.bookong.zest.core.testcase.RmdbDataSourceTable;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.thirdparty.dbunit.DbUnitDataSet;
import com.github.bookong.zest.util.ZestDateUtils;
import com.github.bookong.zest.util.ZestSqlHelper;

/**
 * 使用 DBUnit 的执行器
 * 
 * @author jiangxu
 */
public class DbUnitExcuter extends AbstractJdbcExcuter {

    private static Logger       logger = LoggerFactory.getLogger(DbUnitExcuter.class);
    private IDatabaseConnection dbUnitConn;
    private DbUnitDataSet       zestDataSet;

    @Override
    public void initDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) {
        try {
            zestDataSet = new DbUnitDataSet(testCaseData, testCaseDataSource);
            dbUnitConn = new DatabaseConnection(conn);
            dbUnitConn.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            dbUnitConn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

            DatabaseOperation.TRUNCATE_TABLE.execute(dbUnitConn, zestDataSet);
            DatabaseOperation.INSERT.execute(dbUnitConn, zestDataSet);
        } catch (Exception e) {
            throw new RuntimeException("Fail to init Zest dataSource with DBUnit, ID:\"" + testCaseDataSource.getId() + "\"", e);
        }
    }

    @Override
    public void checkTargetDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) {
        try {
            if (testCaseDataSource.isIgnoreTargetData()) {
                logger.info(String.format("DB: %1$s ignore verify.", testCaseDataSource.getId()));
            } else {
                for (AbstractDataSourceTable<?> table : testCaseDataSource.getTargetDatas().values()) {
                    if (table.isIgnoreCheckTarget()) {
                        logger.info(String.format("DB: %1$s table: %2$s ignore verify.", testCaseDataSource.getId(), table.getName()));
                    } else {
                        verifyTable(conn, testCaseData, testCaseDataSource, (RmdbDataSourceTable) table);
                    }
                }
            }
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fail to check target dataSource with DBUnit, ID:\"" + testCaseDataSource.getId() + "\"", e);
        }
    }

    @Override
    public void clearDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) {
        try {
            DatabaseOperation.TRUNCATE_TABLE.execute(dbUnitConn, zestDataSet);
        } catch (Exception e2) {
            logger.error("", e2);
        }
    }

    /** 验证表数据 */
    private void verifyTable(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource, RmdbDataSourceTable table) {
        logger.info(String.format("DB: %1$s table: %2$s verifying...", testCaseDataSource.getId(), table.getName()));

        try {
            List<Map<String, Object>> datasInDb = getDatasInDb(conn, table);
            Assert.assertEquals(String.format("check DB: %1$s table: %2$s row count. ", testCaseDataSource.getId(), table.getName()), table.getRowDatas().size(), datasInDb.size());

            for (int rowIdx = 0; rowIdx < datasInDb.size(); rowIdx++) {
                Map<String, Object> actualRowDatas = datasInDb.get(rowIdx);
                RmdbDataSourceRow rmdbDataSourceRow = table.getRowDatas().get(rowIdx);
                verifyRowData(testCaseData, testCaseDataSource, table, rmdbDataSourceRow, actualRowDatas);
            }
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Fail to check target DB: %1$s table: %2$s", testCaseDataSource.getId(), table.getName()), e);
        }
    }

    /** 从数据库读取指定表数据 */
    private List<Map<String, Object>> getDatasInDb(Connection conn, AbstractDataSourceTable<?> table) {
        String sql = "";
        if (StringUtils.isNotBlank(table.getQuerySql())) {
            sql = table.getQuerySql();
        } else {
            sql = "select * from " + table.getName();
        }

        return ZestSqlHelper.findDataInDatabase(conn, sql);
    }

    /** 验证每行的数据 */
    private void verifyRowData(TestCaseData testCaseData, TestCaseDataSource testCaseDataSource, RmdbDataSourceTable table, RmdbDataSourceRow rmdbDataSourceRow,
                               Map<String, Object> actualRowDatas) {
        for (Entry<String, Object> entry : rmdbDataSourceRow.getFields().entrySet()) {
            String expectedColName = entry.getKey();
            Object expectedColData = entry.getValue();
            // 只验证 expected 中指定的列
            Assert.assertTrue(String.format("DB: %1$s table: %2$s must has column %3$s", testCaseDataSource.getId(), table.getName(), expectedColName),
                              actualRowDatas.containsKey(expectedColName));

            Object actualColData = actualRowDatas.get(expectedColName);
            if (expectedColData instanceof com.github.bookong.zest.core.xml.data.Field) {
                com.github.bookong.zest.core.xml.data.Field expectedField = (com.github.bookong.zest.core.xml.data.Field) expectedColData;
                if (actualColData == null) {
                    if (expectedField.isNullable()) {
                        return;
                    } else {
                        Assert.fail(String.format("DB: %1$s, table: %2$s, column: %3$s must be NOT NULL", testCaseDataSource.getId(), table.getName(), expectedColName));
                    }
                } else {
                    if (expectedField.isMustNull()) {
                        Assert.assertNull(String.format("DB: %1$s, table: %2$s, column: %3$s must be NULL", testCaseDataSource.getId(), table.getName(), expectedColName),
                                          actualColData);

                    } else if (expectedField.getCurrentTimeRule() != null) {
                        verifyCurrentTimeRule(testCaseData, testCaseDataSource, table, expectedColName, expectedColData, expectedField, actualColData);

                    } else if (expectedField.getFromCurrentTimeRule() != null) {
                        verifyFromCurrentTimeRule(testCaseData, testCaseDataSource, table, expectedColName, expectedColData, expectedField, actualColData);

                    } else if (StringUtils.isNotBlank(expectedField.getRegExpRule())) {
                        Assert.assertTrue(String.format("DB: %1$s, table: %2$s, column: %3$s mush match regExp: %4$s", testCaseDataSource.getId(), table.getName(), expectedColName,
                                                        expectedField.getRegExpRule()),
                                          Pattern.matches(expectedField.getRegExpRule().trim(), String.valueOf(actualColData)));

                    } else {
                        Assert.fail("Undefined!");
                    }
                }

            } else if (expectedColData instanceof Date) {
                Assert.assertTrue(String.format("DB: %1$s, table: %2$s, column: %3$s must be Date", testCaseDataSource.getId(), table.getName(), expectedColName),
                                  (actualColData instanceof Date));
                Assert.assertEquals(String.format("DB: %1$s, table: %2$s, column: %3$s check value", testCaseDataSource.getId(), table.getName(), expectedColName),
                                    ZestDateUtils.formatDateNormal((Date) expectedColData), ZestDateUtils.getStringFromDBDate((Date) actualColData, testCaseData));
            } else {
                // 具体值验证 - 非日期的其他类型
                Assert.assertNotNull(String.format("DB: %1$s, table: %2$s, column: %3$s must be NOT NULL", testCaseDataSource.getId(), table.getName(), expectedColName),
                                     actualColData);
                Assert.assertEquals(String.format("DB: %1$s, table: %2$s, column: %3$s check value", testCaseDataSource.getId(), table.getName(), expectedColName),
                                    String.valueOf(expectedColData), String.valueOf(actualColData));
            }
        }
    }

    private void verifyCurrentTimeRule(TestCaseData testCaseData, TestCaseDataSource testCaseDataSource, RmdbDataSourceTable table, String expectedColName, Object expectedColData,
                                       com.github.bookong.zest.core.xml.data.Field expectedField, Object actualColData) {
        long tmp = getActualColDataTime(testCaseDataSource, table, expectedColName, actualColData);
        Assert.assertTrue(String.format("DB: %1$s, table: %2$s, column: %3$s must be current time", testCaseDataSource.getId(), table.getName(), expectedColName),
                          (tmp >= testCaseData.getStartTime() && tmp <= testCaseData.getEndTime() + expectedField.getCurrentTimeRule().getOffset()));
    }

    private void verifyFromCurrentTimeRule(TestCaseData testCaseData, TestCaseDataSource testCaseDataSource, RmdbDataSourceTable table, String expectedColName,
                                           Object expectedColData, com.github.bookong.zest.core.xml.data.Field expectedField, Object actualColData) {
        int unit = Calendar.SECOND;
        switch (expectedField.getFromCurrentTimeRule().getUnit()) {
            case "day":
                unit = Calendar.DAY_OF_YEAR;
                break;
            case "hour":
                unit = Calendar.HOUR_OF_DAY;
                break;
            case "minute":
                unit = Calendar.MINUTE;
                break;
            case "second":
                unit = Calendar.SECOND;
                break;
            default:
                Assert.fail(String.format("DB: %1$s, table: %2$s, column: %3$s unknown Unit value %4$s", testCaseDataSource.getId(), table.getName(), expectedColName,
                                          expectedField.getFromCurrentTimeRule().getUnit()));
        }

        int min = Math.min(expectedField.getFromCurrentTimeRule().getMin(), expectedField.getFromCurrentTimeRule().getMax());
        int max = Math.max(expectedField.getFromCurrentTimeRule().getMin(), expectedField.getFromCurrentTimeRule().getMax());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(testCaseData.getStartTime());
        cal.add(unit, min);
        long expectedMin = cal.getTimeInMillis();

        cal.setTimeInMillis(testCaseData.getEndTime());
        cal.add(unit, max);
        long expectedMax = cal.getTimeInMillis();

        long tmp = getActualColDataTime(testCaseDataSource, table, expectedColName, actualColData);
        Assert.assertTrue(String.format("DB: %1$s, table: %2$s, column: %3$s must be from current time value", testCaseDataSource.getId(), table.getName(), expectedColName),
                          (tmp >= expectedMin && tmp <= expectedMax));
    }

    private long getActualColDataTime(TestCaseDataSource testCaseDataSource, RmdbDataSourceTable table, String expectedColName, Object actualColData) {
        long tmp = 0;
        if (actualColData instanceof Date) {
            tmp = ((Date) actualColData).getTime();
        } else if (actualColData instanceof Long) {
            tmp = ((Long) actualColData).longValue();
        } else {
            Assert.fail(String.format("DB: %1$s, table: %2$s, column: %3$s must be Date or Long", testCaseDataSource.getId(), table.getName(), expectedColName));
        }
        return tmp;
    }
}
