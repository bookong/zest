package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.support.xml.data.Field;

/**
 * @author jiangxu
 */
public abstract class AbstractRule {

    private boolean nullable;

    public AbstractRule(Field xmlField){
        this.nullable = xmlField.isNullable();
    }

    public boolean isNullable() {
        return nullable;
    }
}
