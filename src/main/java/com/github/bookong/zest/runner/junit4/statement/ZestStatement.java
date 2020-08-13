package com.github.bookong.zest.runner.junit4.statement;

import com.github.bookong.zest.runner.ZestWorker;

/**
 * @author jiangxu
 */
public class ZestStatement extends AbstractStatement {

    private final ZestFrameworkMethod zestMethod;

    public ZestStatement(ZestWorker launcher, Object target, ZestFrameworkMethod zestMethod){
        super(launcher, target);
        this.zestMethod = zestMethod;
    }

    @Override
    public void evaluate() throws Throwable {
        getWorker().initDataSource();
        getWorker().getTestCaseData().setStartTime(System.currentTimeMillis());
        invokeMethod(zestMethod);
        getWorker().getTestCaseData().setEndTime(System.currentTimeMillis());
        getWorker().checkTargetDataSource();
    }
}
