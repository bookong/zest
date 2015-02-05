package com.github.bookong.zest.core;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author jiangxu
 *
 */
public class RunZestBefores extends AbstractStatement {
    private final Statement fNext;
    private final List<FrameworkMethod> fBefores;

    public RunZestBefores(Launcher launcher, Object target, Statement next, List<FrameworkMethod> befores) {
    	super(launcher, target);
        fNext = next;
        fBefores = befores;
    }
    

    @Override
    public void evaluate() throws Throwable {
        for (FrameworkMethod before : fBefores) {
            invokeMethod(before);
        }
        fNext.evaluate();
    }
}