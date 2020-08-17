package com.github.bookong.zest.runner.junit4.statement;

import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.ZestData;

/**
 * @author jiangxu
 */
public class ZestStatement extends AbstractStatement {

    private final ZestFrameworkMethod zestMethod;
    private ZestWorker                worker;
    private ZestData                  zestData;

    public ZestStatement(ZestWorker worker, ZestData zestData, Object target, ZestFrameworkMethod zestMethod){
        super(target);
        this.worker = worker;
        this.zestMethod = zestMethod;
        this.zestData = zestData;
    }

    @Override
    public void evaluate() throws Throwable {
        worker.initDataSource(zestData);
        zestData.setStartTime(System.currentTimeMillis());
        invokeMethod(zestMethod, zestData);
        zestData.setEndTime(System.currentTimeMillis());
        worker.checkTargetDataSource(zestData);
    }
}
