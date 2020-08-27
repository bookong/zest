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
    public void clear(ZestWorker worker, ZestData zestData, Source source) {
        Set<String> tableNames = findAllTableNames(source);
        Connection conn = worker.getOperator(source.getId(), Connection.class);

        for (String tableName : tableNames) {
            ZestSqlHelper.execute(conn, String.format("truncate table `%s`", tableName));
        }
    }

    @Override
    protected void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable data) {
        if (!(data instanceof Table)) {
            throw new ZestException(Messages.executorMatch());
        }

        Table table = (Table) data;
        Connection conn = worker.getOperator(source.getId(), Connection.class);
        ZestSqlHelper.insert(conn, table);
    }

    @Override
    protected void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable data) {
        if (!(data instanceof Table)) {
            throw new ZestException(Messages.executorMatch());
        }

        Table table = (Table) data;
        Connection conn = worker.getOperator(source.getId(), Connection.class);
        List<Map<String, Object>> dataInDb = ZestSqlHelper.find(conn, table);
        Assert.assertEquals(Messages.checkTableSize(source.getId(), table.getName()), table.getDataList().size(),
                            dataInDb.size());
        for (int i = 0; i < table.getDataList().size(); i++) {
            Row expected = table.getDataList().get(i);
            Map<String, Object> actual = dataInDb.get(i);
            verifyRow(zestData, source, table, i + 1, expected, actual);
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

    protected void verifyRow(ZestData zestData, Source source, Table table, int rowIdx, Row expectedRow,
                             Map<String, Object> actualRow) {
        expectedRow.verify(zestData, source, table, rowIdx, actualRow);
    }

}
