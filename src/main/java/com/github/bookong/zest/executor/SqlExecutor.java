package com.github.bookong.zest.executor;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Row;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.junit.Assert;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 简单的 Sql 的执行器
 *
 * @author Jiang Xu
 */
public class SqlExecutor extends AbstractExecutor {

    @Override
    public void clear(ZestWorker worker, ZestData zestData, Source source) {
        Connection conn = worker.getOperator(source.getId(), Connection.class);
        for (String tableName : findAllTableNames(source)) {
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
        List<Map<String, Object>> actualList = ZestSqlHelper.find(conn, table);

        Assert.assertEquals(Messages.checkTableSize(source.getId(), table.getName()), table.getDataList().size(),
                            actualList.size());

        for (int i = 0; i < table.getDataList().size(); i++) {
            Row expected = table.getDataList().get(i);
            Map<String, Object> actual = actualList.get(i);
            expected.verify(this, conn, zestData, source, table, i + 1, actual);
        }
    }

    /**
     * 自定义的数据转换
     */
    public Object parseRowValue(String tableName, String fieldName, Integer fieldSqlType,
                                Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * 自定义加载数据库的 SqlType
     */
    public void loadSqlTypes(Connection conn, Map<String, Integer> sqlTypes) {
        throw new UnsupportedOperationException();
    }

    /**
     * 自定义验证 Row ，子类可以覆盖
     */
    public void verifyRow(Connection conn, ZestData zestData, Source source, Table table, int rowIdx,
                          Map<String, Object> actualRow) {
        throw new UnsupportedOperationException();
    }
}
