package com.github.bookong.zest.core.executer;

import com.github.bookong.zest.core.testcase.*;
import com.github.bookong.zest.core.testcase.sql.SqlDataSourceRow;
import com.github.bookong.zest.core.testcase.sql.SqlDataSourceTable;
import com.github.bookong.zest.support.rule.CurrentTimeRule;
import com.github.bookong.zest.support.rule.FromCurrentTimeRule;
import com.github.bookong.zest.support.rule.RegExpRule;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * 简单的 Sql 的执行器
 * 
 * @author jiangxu
 */
public class SqlExcuter extends AbstractExcuter {

    /**
     * 初始化数据库中数据
     * 
     * @param conn
     * @param testCaseData
     * @param dataSource
     */
    public void initDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource dataSource) {
        for (AbstractDataSourceTable<?> item : dataSource.getInitData().getInitDataList()) {
            if (!(item instanceof SqlDataSourceTable)) {
                throw new RuntimeException(Messages.executerMatchSql());
            }

            SqlDataSourceTable table = (SqlDataSourceTable) item;
            List<SqlDataSourceRow> rowList = table.getRowDataList();
            if (rowList.isEmpty()) {
                return;
            }

            Set<String> columnNames = getColumnNames(table);
            if (columnNames.isEmpty()) {
                return;
            }

            for (int i = 0; i < table.getRowDataList().size(); i++) {
                SqlDataSourceRow row = table.getRowDataList().get(i);
                String sql = genInsertSql(columnNames, table);
                Object[] params = new Object[columnNames.size()];
                int idx = 0;
                for (String columnName : columnNames) {
                    params[idx++] = row.getFields().get(columnName);
                }
                insert(conn, sql, params);
            }
        }
    }

    /**
     * 验证数据库中的数据
     * 
     * @param conn
     * @param testCaseData
     * @param dataSource
     */
    public void checkTargetDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource dataSource) {
        try {
            if (dataSource.getTargetData().isIgnoreCheck()) {
                logger.info(Messages.ignoreTargetData(dataSource.getId()));
                return;
            }

            for (AbstractDataSourceTable<?> table : dataSource.getTargetData().getTargetDataMap().values()) {
                if (table.isIgnoreCheckTarget()) {
                    logger.info(Messages.ignoreTargetTable(dataSource.getId(), table.getName()));
                    continue;
                }

                logger.info(Messages.startCheckTable(dataSource.getId(), table.getName()));
                verifyTable(conn, testCaseData, dataSource, (SqlDataSourceTable) table);
            }

        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError(Messages.checkDs(dataSource.getId()), e);
        }
    }

    /**
     * 清空数据
     * 
     * @param conn
     * @param testCaseData
     * @param dataSource
     */
    public void clearDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource dataSource) {
        Set<String> tableNames = new LinkedHashSet<>();
        dataSource.getInitData().getInitDataList().forEach(table -> tableNames.add(table.getName()));
        dataSource.getTargetData().getTargetDataMap().values().forEach(table -> tableNames.add(table.getName()));

        for (String tableName : tableNames) {
            truncateTable(conn, tableName);
        }
    }

    protected Set<String> getColumnNames(SqlDataSourceTable table) {
        Set<String> columnNames = new LinkedHashSet<>(table.getRowDataList().get(0).getFields().size() + 1);
        for (SqlDataSourceRow row : table.getRowDataList()) {
            columnNames.addAll(row.getFields().keySet());
        }
        return columnNames;
    }

    protected String genInsertSql(Set<String> columnNames, SqlDataSourceTable table) {
        try {
            StringBuilder sb = new StringBuilder(128);
            sb.append("insert into ").append("`").append(table.getName()).append("`(");
            Iterator<String> it = columnNames.iterator();
            while (it.hasNext()) {
                String columnName = it.next();
                sb.append("`").append(columnName).append("`");
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(") values(");

            for (int i = 0; i < columnNames.size(); i++) {
                sb.append("?");
                if (i < columnNames.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void truncateTable(Connection conn, String tableName) {
        ZestSqlHelper.execute(conn, String.format("truncate table `%s`", tableName));
    }

    protected void insert(Connection conn, String sql, Object[] params) {
        ZestSqlHelper.execute(conn, sql, params);
    }

    protected void verifyTable(Connection conn, TestCaseData testCaseData, TestCaseDataSource dataSource,
                               SqlDataSourceTable table) {
        List<Map<String, Object>> dataInDb = findDatas(conn, table);
        Assert.assertEquals(Messages.checkTableSize(dataSource.getId(), table.getName()), table.getRowDataList().size(),
                            dataInDb.size());
        for (int i = 0; i < table.getRowDataList().size(); i++) {
            SqlDataSourceRow expected = table.getRowDataList().get(i);
            Map<String, Object> actual = dataInDb.get(i);
            verifyRow(testCaseData, dataSource, table, i + 1, expected, actual);
        }
    }

    protected void verifyRow(TestCaseData testCaseData, TestCaseDataSource dataSource, SqlDataSourceTable table,
                             int rowIdx, SqlDataSourceRow expectedRow, Map<String, Object> actualRow) {
        List<String> columnNames = new ArrayList<>(actualRow.size() + 1);
        if (dataSource.getTargetData().isOnlyCheckCoreData()) {
            logger.info(Messages.ignoreTargetColUnspecified(dataSource.getId(), table.getName()));
            columnNames.addAll(expectedRow.getFields().keySet());
        } else {
            columnNames.addAll(actualRow.keySet());
        }

        for (String columnName : columnNames) {
            Object expected = expectedRow.getFields().get(columnName);
            Object actual = actualRow.get(columnName);

            if (expected == null) {
                Assert.assertNull(Messages.checkTableColNull(dataSource.getId(), table.getName(), rowIdx, columnName),
                                  actual);

            } else if (expected instanceof CurrentTimeRule) {
                ((CurrentTimeRule) expected).assertIt(testCaseData, dataSource, table, rowIdx, columnName, actual);

            } else if (expected instanceof FromCurrentTimeRule) {
                ((FromCurrentTimeRule) expected).assertIt(testCaseData, dataSource, table, rowIdx, columnName, actual);

            } else if (expected instanceof RegExpRule) {
                ((RegExpRule) expected).assertIt(testCaseData, dataSource, table, rowIdx, columnName, actual);

            } else if (expected instanceof Date) {
                Assert.assertTrue(Messages.checkTableColDateType(dataSource.getId(), table.getName(), rowIdx,
                                                                 columnName),
                                  (actual instanceof Date));
                String expectedDate = ZestDateUtil.formatDateNormal(ZestDateUtil.getDateInDB((Date) expected,
                                                                                             testCaseData));
                String actualDate = ZestDateUtil.formatDateNormal((Date) actual);
                Assert.assertEquals(Messages.checkTableCol(dataSource.getId(), table.getName(), rowIdx, columnName),
                                    expectedDate, actualDate);

            } else {
                Assert.assertEquals(Messages.checkTableCol(dataSource.getId(), table.getName(), rowIdx, columnName),
                                    String.valueOf(expected), String.valueOf(actual));
            }
        }
    }

    protected List<Map<String, Object>> findDatas(Connection conn, SqlDataSourceTable table) {
        String sql = String.format("select * from `%s`", table.getName());
        if (StringUtils.isNotBlank(table.getQuery())) {
            sql = table.getQuery();
        }

        Statement stat = null;
        ResultSet rs = null;
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> obj = new HashMap<>();
                list.add(obj);
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String name = rs.getMetaData().getColumnName(i).toLowerCase();
                    Object value = rs.getObject(i);
                    obj.put(name, parseValue(value));
                }
            }

            return list;
        } catch (Exception e) {
            ZestSqlHelper.close(rs);
            ZestSqlHelper.close(stat);
            throw new RuntimeException(e);
        }
    }

    protected Object parseValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer || value instanceof Long || value instanceof Byte) {
            return ((Number) value).longValue();
        }

        if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
            return ((Number) value).doubleValue();
        }

        // Timestamp , String
        return value;
    }
}
