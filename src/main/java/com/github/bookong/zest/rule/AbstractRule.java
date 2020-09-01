package com.github.bookong.zest.rule;

import com.github.bookong.zest.testcase.ZestData;
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

    public abstract void verify(ZestData zestData, String path, Object actual);

    long getActualDataTime(String path, Object actual) {
        long tmp = 0;
        if (actual instanceof Date) {
            tmp = ((Date) actual).getTime();

        } else if (actual instanceof Long) {
            tmp = (Long) actual;

        } else {
            Assert.fail(Messages.verifyRuleDateType(path));
        }

        return tmp;
    }

    void assertNullable(String path, Object actual) {
        if (!isNullable() && actual == null) {
            Assert.fail(Messages.verifyRuleNotNull(path));
        }
    }

    public boolean isNullable() {
        return nullable;
    }

    public String getPath() {
        return path;
    }
}