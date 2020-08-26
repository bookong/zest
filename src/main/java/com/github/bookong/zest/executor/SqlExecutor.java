package com.github.bookong.zest.executor;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.sql.Row;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
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
 * @author Jiang Xu
 */
public class SqlExecutor extends AbstractExecutor {

    @Override
    protected void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable data) {
        if (!(data instanceof Table)) {
            throw new ZestException(Messages.executorMatch());
        }

        Table table = (Table) data;
        Connection conn = worker.getOperator(source.getId(), Connection.class);
        List<Row> rowList = table.getDataList();
        if (rowList.isEmpty()) {
            return;
        }

        Set<String> columnNames = getColumnNames(table);
        if (columnNames.isEmpty()) {
            return;
        }

        for (int i = 0; i < table.getDataList().size(); i++) {
            Row row = table.getDataList().get(i);
            String sql = genInsertSql(columnNames, table);
            Object[] params = new Object[columnNames.size()];
            int idx = 0;
            for (String columnName : columnNames) {
                params[idx++] = row.getExpectedDataMap().get(columnName);
            }
            insert(conn, sql, params);
        }
    }

    @Override
    protected void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable data) {
        if (!(data instanceof Table)) {
            throw new ZestException(Messages.executorMatch());
        }

        Table table = (Table) data;
        Connection conn = worker.getOperator(source.getId(), Connection.class);
        List<Map<String, Object>> dataInDb = findData(conn, table);
        Assert.assertEquals(Messages.checkTableSize(source.getId(), table.getName()), table.getDataList().size(),
                            dataInDb.size());
        for (int i = 0; i < table.getDataList().size(); i++) {
            Row expected = table.getDataList().get(i);
            Map<String, Object> actual = dataInDb.get(i);
            verifyRow(zestData, source, table, i + 1, expected, actual);
        }
    }

    @Override
    public void clear(ZestWorker worker, ZestData zestData, Source source) {
        Set<String> tableNames = findAllTableNames(source);
        Connection conn = worker.getOperator(source.getId(), Connection.class);

        for (String tableName : tableNames) {
            truncateTable(conn, tableName);
        }
    }

    /**
     * 自定义的数据转换
     *
     * @param tableName
     * @param fieldName
     * @param fieldSqlType
     * @param value
     * @return
     * @throws UnsupportedOperationException
     */
    public Object parseRowValue(String tableName, String fieldName, Integer fieldSqlType,
                                Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * 自定义加载数据库的 SqlType
     * 
     * @param conn
     * @param sqlTypes
     */
    public void loadSqlTypes(Connection conn, Map<String, Integer> sqlTypes) {
        throw new UnsupportedOperationException();
    }

    protected Set<String> getColumnNames(Table table) {
        Set<String> columnNames = new LinkedHashSet<>(table.getDataList().get(0).getExpectedDataMap().size() + 1);
        for (Row row : table.getDataList()) {
            columnNames.addAll(row.getExpectedDataMap().keySet());
        }
        return columnNames;
    }

    protected String genInsertSql(Set<String> columnNames, Table table) {
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

        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(e);
        }
    }

    protected void truncateTable(Connection conn, String tableName) {
        ZestSqlHelper.execute(conn, String.format("truncate table `%s`", tableName));
    }

    protected void insert(Connection conn, String sql, Object[] params) {
        ZestSqlHelper.execute(conn, sql, params);
    }

    protected void verifyRow(ZestData zestData, Source source, Table table, int rowIdx, Row expectedRow,
                             Map<String, Object> actualRow) {
        expectedRow.verify(zestData, source, table, rowIdx, actualRow);
    }

    protected List<Map<String, Object>> findData(Connection conn, Table table) {
        String sql = String.format("select * from `%s`", table.getName());
        if (StringUtils.isNotBlank(table.getSort())) {
            sql = sql.concat(table.getSort());
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
            throw new ZestException(e);
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
