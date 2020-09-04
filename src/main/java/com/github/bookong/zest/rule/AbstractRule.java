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

    private boolean manual;

    AbstractRule(String field, boolean nullable, boolean manual){
        this.field = field;
        this.nullable = nullable;
        this.manual = manual;
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

    public boolean isManual() {
        return manual;
    }
}
