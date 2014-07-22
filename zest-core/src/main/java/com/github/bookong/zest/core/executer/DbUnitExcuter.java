package com.github.bookong.zest.core.executer;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;

import com.github.bookong.zest.core.testcase.data.DataBase;
import com.github.bookong.zest.core.testcase.data.RuleColData;
import com.github.bookong.zest.core.testcase.data.TargetTable;
import com.github.bookong.zest.thirdparty.dbunit.ZestDataSet;
import com.github.bookong.zest.util.DateUtils;
import com.github.bookong.zest.util.SqlHelper;

/**
 * @author jiangxu
 *
 */
public class DbUnitExcuter extends AbstractJdbcExcuter {

	private IDatabaseConnection dbUnitConn;
	private ZestDataSet zestDataSet;
	private long initDBTime;
	private long checkTargetDBTime;

	@Override
	public void initDatabase(Connection connection, DataBase database, long currDbTimeDiff) {
		try {
			initDBTime = System.currentTimeMillis();
			dbUnitConn = new DatabaseConnection(connection);
			zestDataSet = new ZestDataSet(database, currDbTimeDiff);

			DatabaseOperation.TRUNCATE_TABLE.execute(dbUnitConn, zestDataSet);
			DatabaseOperation.INSERT.execute(dbUnitConn, zestDataSet);
		} catch (Exception e) {
			throw new RuntimeException("Fail to init database", e);
		}
	}

	@Override
	public void checkTargetDatabase(Connection connection, DataBase database, long currDbTimeDiff) {
		try {
			checkTargetDBTime = System.currentTimeMillis();
			for (Entry<String, TargetTable> entry : database.getTargetTables().entrySet()) {
				if (entry.getValue().isIgnoreTargetTableVerify()) {
					System.out.println("Ignore target table \"" + entry.getKey() + "\" verify.");
				} else {
					verifyTable(connection, entry.getKey(), entry.getValue(), currDbTimeDiff);
				}
			}

			DatabaseOperation.TRUNCATE_TABLE.execute(dbUnitConn, zestDataSet);
		} catch (Exception e) {
			throw new RuntimeException("Fail to check target database", e);
		}
	}

	private void verifyTable(Connection connection, String tabName, TargetTable targetTab, long currDbTimeDiff)
			throws Exception {
		System.out.println("Verify target table:\"" + tabName + "\"");

		String sql = "";
		if (StringUtils.isNotBlank(targetTab.getTargetTableQuerySql())) {
			sql = targetTab.getTargetTableQuerySql();
		} else {
			sql = "select * from " + tabName;
		}

		List<Map<String, Object>> datasInDb = SqlHelper.findDataInDatabase(connection, sql);
		for (int rowIdx = 0; rowIdx < datasInDb.size(); rowIdx++) {
			if (datasInDb.size() != targetTab.getDatas().size()) {
				throw new RuntimeException("Fail to verify target table \"" + tabName + "\", row count wrong.");

			} else {
				Map<String, Object> rowDataInDb = datasInDb.get(rowIdx);
				Map<String, Object> rowDataInTargetTab = targetTab.getDatas().get(rowIdx);
				for (Entry<String, Object> colDataInDb : rowDataInDb.entrySet()) {
					String colDataInDbName = colDataInDb.getKey();
					Object colDataInDbValue = colDataInDb.getValue();
					String assertMsg = "Verify target table \"" + tabName + "\" col \"" + colDataInDbName + "\"";
					if (rowDataInTargetTab.containsKey(colDataInDbName)) {
						Object colDataInTargetTab = rowDataInTargetTab.get(colDataInDbName);
						if (colDataInTargetTab == null) {
							Assert.assertNull(assertMsg, colDataInDbValue);
						} else if (colDataInTargetTab instanceof RuleColData) {
							RuleColData ruleColData = (RuleColData) colDataInTargetTab;
							if (!ruleColData.isNullable()) {
								Assert.assertNotNull(assertMsg + ", must not null", colDataInDbValue);
							}

							if (colDataInDbValue != null) {
								if (ruleColData.getCurrentTime() != null && ruleColData.getCurrentTime().booleanValue()) {
									Assert.assertTrue(assertMsg, (colDataInDbValue instanceof Date));
									long tmp = ((Date) colDataInDbValue).getTime();
									Assert.assertTrue(assertMsg + ", must be current time",
											(tmp >= initDBTime && tmp <= checkTargetDBTime));
								}

								if (StringUtils.isNotBlank(ruleColData.getRegExp())) {
									Assert.assertTrue(
											assertMsg + ", mush match regExp:" + ruleColData.getRegExp(),
											Pattern.matches(ruleColData.getRegExp().trim(),
													String.valueOf(colDataInDbValue)));
								}
							}

						} else {
							if (colDataInTargetTab instanceof Date) {
								Assert.assertTrue(assertMsg, (colDataInDbValue instanceof Date));
								String colDataInDbValueFixed = DateUtils.getStringFromDBDate((Date) colDataInDbValue,
										currDbTimeDiff);
								Assert.assertEquals(assertMsg, DateUtils.formatDateNormal((Date) colDataInTargetTab),
										colDataInDbValueFixed);
							} else {
								Assert.assertEquals(assertMsg, colDataInTargetTab, colDataInDbValue);
							}
						}
					} else {
						// FIXME 目前暂时先忽略对未定义列的验证，以后增加目标库正则等验证规则
					}
				}
			}
		}
	}

	public IDatabaseConnection getDbUnitConn() {
		return dbUnitConn;
	}
}
