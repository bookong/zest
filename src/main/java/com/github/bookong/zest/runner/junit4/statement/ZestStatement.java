package com.github.bookong.zest.runner.junit4.statement;

import com.github.bookong.zest.runner.ZestLauncher;

/**
 * @author jiangxu
 */
public class ZestStatement extends AbstractStatement {

    private final ZestFrameworkMethod zestMethod;

    public ZestStatement(ZestLauncher launcher, Object target, ZestFrameworkMethod zestMethod){
        super(launcher, target);
        this.zestMethod = zestMethod;
    }

    @Override
    public void evaluate() throws Throwable {
        getLauncher().initDataSource();
        getLauncher().getTestCaseData().setStartTime(System.currentTimeMillis());
        invokeMethod(zestMethod);
        getLauncher().getTestCaseData().setEndTime(System.currentTimeMillis());
        getLauncher().checkTargetDataSource();
    }
}
