package com.github.bookong.zest.core.executer;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;

import com.github.bookong.zest.core.testcase.data.DataBase;
import com.github.bookong.zest.core.testcase.data.TargetTable;
import com.github.bookong.zest.thirdparty.dbunit.ZestDataSet;
import com.github.bookong.zest.util.SqlHelper;

/**
 * @author jiangxu
 *
 */
public class DbUnitExcuter extends AbstractJdbcExcuter {

	private IDatabaseConnection dbUnitConn;
	private ZestDataSet zestDataSet;

	@Override
	public void initDatabase(Connection connection, DataBase database, long currDbTimeDiff) {
		try {
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
			for (Entry<String, TargetTable> entry : database.getTargetTables().entrySet()) {
				if (entry.getValue().isIgnoreTargetTableVerify()) {
					System.out.println("Ignore target table verify. Table name:" + entry.getKey());
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
		System.out.println("verify table:\"" + tabName + "\"");

		String sql = "";
		if (StringUtils.isNotBlank(targetTab.getTargetTableQuerySql())) {
			sql = targetTab.getTargetTableQuerySql();
		} else {
			sql = "select * from " + tabName;
		}

		List<Map<String, Object>> datasInDb = SqlHelper.findDataInDatabase(connection, sql);
		for (int rowIdx = 0; rowIdx < datasInDb.size(); rowIdx++) {
			if (rowIdx >= targetTab.getDatas().size()) {
				throw new RuntimeException("Fail to verify target table \"" + tabName + "\", row count wrong.");
			} else {
				Map<String, Object> rowDataInDb = datasInDb.get(rowIdx);
				Map<String, Object> rowDataInTargetTab = targetTab.getDatas().get(rowIdx);
				for (Entry<String, Object> colDataInDb : rowDataInDb.entrySet()) {
					String colDataInDbName = colDataInDb.getKey();
					Object colDataInDbValue = colDataInDb.getValue();
					String assertMsg = "verify table \"" + tabName + "\" col \"" + colDataInDbName + "\"";
					if (rowDataInTargetTab.containsKey(colDataInDbName)) {
						Object colDataInTargetTab = rowDataInTargetTab.get(colDataInDbName);
						if (colDataInTargetTab == null) {
							Assert.assertNull(assertMsg, colDataInDbValue);
						} else {
							if (colDataInTargetTab instanceof Date) {
								Assert.assertTrue(assertMsg, (colDataInDbValue instanceof Date));
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date newDate = new Date();
								newDate.setTime(((Date)colDataInDbValue).getTime() - currDbTimeDiff);
								Assert.assertEquals(assertMsg, sdf.format((Date)colDataInTargetTab), sdf.format(newDate));
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
