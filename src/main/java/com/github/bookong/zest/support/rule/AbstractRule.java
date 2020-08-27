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

    public abstract void verify(ZestData zestData, Source source, Table table, int rowIdx, String path, Object actual);

    long getActualDataTime(Source source, Table table, int rowIdx, String path, Object actual) {
        long tmp = 0;
        if (actual instanceof Date) {
            tmp = ((Date) actual).getTime();

        } else if (actual instanceof Long) {
            tmp = (Long) actual;

        } else {
            Assert.fail(Messages.checkTableColDate(source.getId(), table.getName(), rowIdx, path));
        }

        return tmp;
    }

    void assertNullable(String path, Object actual) {
        if (!isNullable() && actual == null) {
            Assert.fail(Messages.verifyRowDataNull(path));
        }
    }

    public boolean isNullable() {
        return nullable;
    }

    public String getPath() {
        return path;
    }
}
