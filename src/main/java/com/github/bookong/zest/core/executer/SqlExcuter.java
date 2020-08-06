package com.github.bookong.zest.core.executer;

import com.github.bookong.zest.core.testcase.*;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 简单的 Sql 的执行器
 * 
 * @author jiangxu
 */
public class SqlExcuter extends AbstractExcuter {

    /**
     * 初始化数据库中数据
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
                String sql = genInsertSql(conn, columnNames, table);
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
     * @param testCaseDataSource
     */
    public void checkTargetDatabase(Connection conn, TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) {

    }

    /**
     * 清空数据
     */
    public void clearDatabase(Connection conn, TestCaseDataSource dataSource) {
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

    protected String genInsertSql(Connection conn, Set<String> columnNames, SqlDataSourceTable table) {
        try {
            StringBuilder sb = new StringBuilder(128);
            sb.append("insert into ").append(getDbSchema(conn)).append("`").append(table.getName()).append("`(");
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
        ZestSqlHelper.execute(conn, String.format("truncate table %s`%s`", getDbSchema(conn), tableName));
    }

    protected void insert(Connection conn, String sql, Object[] params) {
        ZestSqlHelper.execute(conn, sql, params);
    }

    protected String getDbSchema(Connection conn) {
        try {
            return String.format("`%s`.", conn.getSchema());
        } catch (Throwable e) {
            return StringUtils.EMPTY;
        }
    }
}
