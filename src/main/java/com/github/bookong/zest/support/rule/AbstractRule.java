package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;

import java.util.Date;

/**
 * @author Jiang Xu
 */
public abstract class AbstractRule {

    private String  path;

    private boolean nullable;

    AbstractRule(String path, boolean nullable){
        this.path = path;
        this.nullable = nullable;
    }

    public abstract void assertIt(ZestData zestData, Source source, Table table, int rowIdx, String columnName,
                                  Object value);

    long getActualDataTime(Source source, Table table, int rowIdx, String columnName, Object value) {
        long tmp = 0;
        if (value instanceof Date) {
            tmp = ((Date) value).getTime();

        } else if (value instanceof Long) {
            tmp = (Long) value;

        } else {
            Assert.fail(Messages.checkTableColDate(source.getId(), table.getName(), rowIdx, columnName));
        }

        return tmp;
    }

    void assertNullable(Source source, Table table, int rowIdx, String columnName, Object value) {
        if (!nullable && value == null) {
            Assert.fail(Messages.checkTableColNullable(source.getId(), table.getName(), rowIdx, columnName));
        }
    }

    public boolean isNullable() {
        return nullable;
    }

    public String getPath() {
        return path;
    }
}
