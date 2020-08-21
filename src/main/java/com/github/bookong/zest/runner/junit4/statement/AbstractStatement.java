package com.github.bookong.zest.runner.junit4.statement;

import com.github.bookong.zest.testcase.ZestData;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author Jiang Xu
 */
public abstract class AbstractStatement extends Statement {

    private final Object target;

    public AbstractStatement(Object target){
        this.target = target;
    }

    public void invokeMethod(FrameworkMethod method, ZestData zestData) throws Throwable {
        Object[] paramArrayOfObject = new Object[1];
        paramArrayOfObject[0] = zestData.getParam();
        method.invokeExplosively(target, paramArrayOfObject);
    }
}
