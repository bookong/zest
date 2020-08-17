package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;

import java.util.Date;

/**
 * @author Jiang Xu
 */
public abstract class AbstractRule {

    private boolean nullable;

    public AbstractRule(Field xmlField){
        this.nullable = xmlField.isNullable();
    }

    public abstract void assertIt(ZestData testCaseData, Source dataSource, Table table,
                                  int rowIdx, String columnName, Object value);

    public boolean isNullable() {
        return nullable;
    }

    protected long getActualDataTime(Source dataSource, Table table, int rowIdx,
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

    protected void assertNullable(Source dataSource, Table table, int rowIdx,
                                  String columnName, Object value) {
        if (!nullable && value == null) {
            Assert.fail(Messages.checkTableColNullable(dataSource.getId(), table.getName(), rowIdx, columnName));
        }
    }
}
