package com.github.bookong.zest.runner.junit4.statement;

import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.ZestData;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Customized <em>Zest</em> Statement.
 *
 * @author Jiang Xu
 */
public class ZestStatement extends Statement {

    private final ZestFrameworkMethod zestMethod;
    private final Object              target;
    private ZestWorker                worker;
    private ZestData                  zestData;

    /**
     * Construct a new instance.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param target
     *          A test instance.
     * @param zestMethod
     *          A method on a test class to be invoked at the appropriate point in test execution.
     */
    public ZestStatement(ZestWorker worker, ZestData zestData, Object target, ZestFrameworkMethod zestMethod){
        this.target = target;
        this.worker = worker;
        this.zestMethod = zestMethod;
        this.zestData = zestData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evaluate() throws Throwable {
        worker.initSource(zestData);
        zestData.setStartTime(System.currentTimeMillis());
        invokeMethod(zestMethod, zestData);
        zestData.setEndTime(System.currentTimeMillis());
        worker.verifySource(zestData);
    }

    private void invokeMethod(FrameworkMethod method, ZestData zestData) throws Throwable {
        Object[] paramArrayOfObject = new Object[1];
        paramArrayOfObject[0] = zestData.getParam();
        method.invokeExplosively(target, paramArrayOfObject);
    }
}
