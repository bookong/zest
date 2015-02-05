package com.github.bookong.zest.core.executer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;

import com.github.bookong.zest.core.testcase.data.Database;
import com.github.bookong.zest.core.testcase.data.TargetTable;
import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.core.testcase.data.rule.ColumnRule;
import com.github.bookong.zest.thirdparty.dbunit.ZestDataSet;
import com.github.bookong.zest.util.ZestDateUtils;
import com.github.bookong.zest.util.ZestSqlHelper;

/**
 * 使用 DBUnit 的执行器
 * 
 * @author jiangxu
 *
 */
public class DbUnitExcuter extends AbstractJdbcExcuter {
	
	private IDatabaseConnection dbUnitConn;
	private ZestDataSet zestDataSet;

	@Override
	public void initDatabase(Connection connection, TestCaseData testCaseData, Database db) {
		try {
			testCaseData.setInitDBTime(System.currentTimeMillis());
			dbUnitConn = new DatabaseConnection(connection);
			zestDataSet = new ZestDataSet(testCaseData, db);

			DatabaseOperation.TRUNCATE_TABLE.execute(dbUnitConn, zestDataSet);
			DatabaseOperation.INSERT.execute(dbUnitConn, zestDataSet);
		} catch (Exception e) {
			throw new RuntimeException("Fail to init database with DBUnit, DB:\"" + db.getDatabaseName() + "\"", e);
		}
	}

	@Override
	public void checkTargetDatabase(Connection connection, TestCaseData testCaseData, Database db) {
		try {
			testCaseData.setCheckTargetDBTime(System.currentTimeMillis());
			for (Entry<String, TargetTable> entry : db.getTargetTables().entrySet()) {
				if (entry.getValue().isIgnoreTargetTableVerify()) {
					System.out.println("DB: \"" + db.getDatabaseName() + "\", table \"" + entry.getKey()
							+ "\" ignore verify.");
				} else {
					verifyTable(connection, db, testCaseData, entry.getValue());
				}
			}
			
		} catch (AssertionError e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Fail to check target database: \"" + db.getDatabaseName() + "\"", e);
		} finally {
			try {
				DatabaseOperation.TRUNCATE_TABLE.execute(dbUnitConn, zestDataSet);
			} catch (Exception e) {
			}
		}
	}

	/** 验证表数据 */
	private void verifyTable(Connection conn, Database db, TestCaseData testCaseData, TargetTable targetTab) {
		String baseMessage = "DB: \"" + db.getDatabaseName() + "\", Table:\"" + targetTab.getTableName() + "\"";
		System.out.println(baseMessage + " verifying...");
		
		try {
			List<Map<String, Object>> datasInDb = getDatasInDb(conn, targetTab);
			Assert.assertEquals(baseMessage + " row count.", targetTab.getDatas().size(), datasInDb.size());

			for (int rowIdx = 0; rowIdx < datasInDb.size(); rowIdx++) {
				Map<String, Object> actualRowDatas = datasInDb.get(rowIdx);
				Map<String, Object> expectedRowDatas = targetTab.getDatas().get(rowIdx);
				verifyRowData((baseMessage + ", Row:" + rowIdx), testCaseData, expectedRowDatas, actualRowDatas);
			}
		} catch (AssertionError e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Fail to check target table: \"" + targetTab.getTableName() + "\"", e);
		}
	}
	
	/** 验证每行的数据 */
	private void verifyRowData(String baseMessage, TestCaseData testCaseData, Map<String, Object> expectedRowDatas,
			Map<String, Object> actualRowDatas) {
		
		for (Entry<String, Object> item : expectedRowDatas.entrySet()) {
			String expectedColName =  item.getKey();
			Object expectedColData = item.getValue();
			baseMessage += ", Column:\"" + expectedColName + "\" - ";
			
			// 只验证 expected 中指定的列
			Assert.assertTrue(baseMessage + "must has column \"" + expectedColName + "\"",
					actualRowDatas.containsKey(expectedColName));
			
			Object actualColData = actualRowDatas.get(expectedColName);
			
			if (expectedColData == null) {
				Assert.assertTrue(baseMessage + "must be NULL", (actualColData == null));
				
			} else if (expectedColData instanceof ColumnRule) {
				// 通过规则来验证
				((ColumnRule)expectedColData).verify(baseMessage, testCaseData, actualColData);
				
			} else if (expectedColData instanceof Date){
				// 具体值验证 - 日期
				Assert.assertTrue(baseMessage + "must be Date", (actualColData instanceof Date));
				Assert.assertEquals(baseMessage, 
						ZestDateUtils.formatDateNormal((Date) expectedColData),
						ZestDateUtils.getStringFromDBDate((Date)actualColData, testCaseData));
			} else {
				// 具体值验证 - 非日期的其他类型
				Assert.assertTrue(baseMessage + "must be NOT NULL", (actualColData != null));
				Assert.assertEquals(baseMessage, String.valueOf(expectedColData), String.valueOf(actualColData));
			}
		}
	}
	
	/** 从数据库读取指定表数据 */
	private List<Map<String, Object>> getDatasInDb(Connection conn, TargetTable targetTab) {
		String sql = "";
		if (StringUtils.isNotBlank(targetTab.getTargetTableQuerySql())) {
			sql = targetTab.getTargetTableQuerySql();
		} else {
			sql = "select * from " + targetTab.getTableName();
		}

		return ZestSqlHelper.findDataInDatabase(conn, sql);
	}

}
