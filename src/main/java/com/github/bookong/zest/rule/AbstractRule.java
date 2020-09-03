package com.github.bookong.zest.rule;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;

import java.util.Date;

/**
 * @author Jiang Xu
 */
public abstract class AbstractRule {

    private String  field;

    private boolean nullable;

    AbstractRule(String field, boolean nullable){
        this.field = field;
        this.nullable = nullable;
    }

    public abstract void verify(ZestData zestData, Object actual);

    protected Date getActualDataTime(String path, Object actual) {
        Date tmp = null;

        if (actual instanceof Date) {
            tmp = (Date) actual;

        } else {
            Assert.fail(Messages.verifyRuleDateType(path));
        }

        return tmp;
    }

    public boolean isNullable() {
        return nullable;
    }

    public String getField() {
        return field;
    }
}
