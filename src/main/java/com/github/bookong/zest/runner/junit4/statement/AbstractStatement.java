package com.github.bookong.zest.runner.junit4.statement;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.util.Messages;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.github.bookong.zest.testcase.ZestTestParam;

/**
 * @author jiangxu
 */
public abstract class AbstractStatement extends Statement {

    private final ZestWorker worker;
    private final Object     target;

    public AbstractStatement(ZestWorker worker, Object target){
        this.worker = worker;
        this.target = target;
    }

    public void invokeMethod(FrameworkMethod method) throws Throwable {
        method.invokeExplosively(target, genParamArrayOfObject(method));
    }

    /** 产生调用方法的参数数组 */
    protected Object[] genParamArrayOfObject(FrameworkMethod method) {
        Class<?>[] paramClasses = method.getMethod().getParameterTypes();
        if (paramClasses.length > 1) {
            throw new ZestException(Messages.initParam());
        }

        Class<?> paramClass = paramClasses[0];
        if (!ZestTestParam.class.isAssignableFrom(paramClass)) {
            throw new ZestException(Messages.initParam());
        }

        Object[] paramArrayOfObject = new Object[1];
        paramArrayOfObject[0] = worker.getTestCaseData().getTestParam();

        return paramArrayOfObject;
    }

    public ZestWorker getWorker() {
        return worker;
    }

    public Object getTarget() {
        return target;
    }

}
