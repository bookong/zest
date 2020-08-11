package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.core.testcase.SqlDataSourceTable;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;

import java.util.Date;

/**
 * @author jiangxu
 */
public abstract class AbstractRule {

    private boolean nullable;

    public AbstractRule(Field xmlField){
        this.nullable = xmlField.isNullable();
    }

    public abstract void assertIt(TestCaseData testCaseData, TestCaseDataSource dataSource, SqlDataSourceTable table,
                                  int rowIdx, String columnName, Object value);

    public boolean isNullable() {
        return nullable;
    }

    protected long getActualDataTime(TestCaseDataSource dataSource, SqlDataSourceTable table, int rowIdx,
                                     String columnName, Object value) {
        long tmp = 0;
        if (value instanceof Date) {
            tmp = ((Date) value).getTime();

        } else if (value instanceof Long) {
            tmp = (Long) value;

        } else {
            Assert.fail(Messages.checkTableColDate(dataSource.getId(), table.getName(), rowIdx, columnName));
        }

        return tmp;
    }

    protected void assertNullable(TestCaseDataSource dataSource, SqlDataSourceTable table, int rowIdx,
                                  String columnName, Object value) {
        if (!nullable && value == null) {
            Assert.fail(Messages.checkTableColNullable(dataSource.getId(), table.getName(), rowIdx, columnName));
        }
    }
}
