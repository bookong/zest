package com.github.bookong.zest.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * @author jiangxu
 *
 */
public class RunZestAfters extends AbstractStatement {
	private final Statement fNext;
    private final List<FrameworkMethod> fAfters;

    public RunZestAfters(Launcher launcher, Object target, Statement next, List<FrameworkMethod> afters) {
    	super(launcher, target);
    	fNext = next;
        fAfters = afters;
    }

    @Override
    public void evaluate() throws Throwable {
        List<Throwable> errors = new ArrayList<Throwable>();
        try {
            fNext.evaluate();
        } catch (Throwable e) {
            errors.add(e);
        } finally {
            for (FrameworkMethod each : fAfters) {
                try {
                	invokeMethod(each);
                } catch (Throwable e) {
                    errors.add(e);
                }
            }
        }
        MultipleFailureException.assertEmpty(errors);
    }

}
