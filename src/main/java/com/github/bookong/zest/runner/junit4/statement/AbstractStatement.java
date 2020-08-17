package com.github.bookong.zest.runner.junit4.statement;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.github.bookong.zest.testcase.ZestParam;

/**
 * @author jiangxu
 */
public abstract class AbstractStatement extends Statement {

    private final Object target;

    public AbstractStatement(Object target){
        this.target = target;
    }

    public void invokeMethod(FrameworkMethod method, ZestData zestData) throws Throwable {
        method.invokeExplosively(target, genParamArrayOfObject(method, zestData));
    }

    /** 产生调用方法的参数数组 */
    protected Object[] genParamArrayOfObject(FrameworkMethod method, ZestData zestData) {
        Class<?>[] paramClasses = method.getMethod().getParameterTypes();
        if (paramClasses.length > 1) {
            throw new ZestException(Messages.initParam());
        }

        Class<?> paramClass = paramClasses[0];
        if (!ZestParam.class.isAssignableFrom(paramClass)) {
            throw new ZestException(Messages.initParam());
        }

        Object[] paramArrayOfObject = new Object[1];
        paramArrayOfObject[0] = zestData.getParam();

        return paramArrayOfObject;
    }

}
