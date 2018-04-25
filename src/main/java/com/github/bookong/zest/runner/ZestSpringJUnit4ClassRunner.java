package com.github.bookong.zest.runner;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.bookong.zest.core.Launcher;
import com.github.bookong.zest.core.ZestFilter;
import com.github.bookong.zest.core.annotations.ZestTest;

/**
 * @author jiangxu
 */
public class ZestSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner implements ZestClassRunner {

    protected Launcher zestLauncher = new Launcher();

    public ZestSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError{
        super(clazz);
        zestLauncher.junit4ClassRunnerConstructor(getTestClass(), this);
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);
        Launcher.junit4ClassRunnerCollectInitializationErrors(errors);
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        return zestLauncher.junit4ClassRunnerGetChildren(computeTestMethods());
    }

    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        ZestTest ztest = frameworkMethod.getAnnotation(ZestTest.class);
        if (ztest == null) {
            super.runChild(frameworkMethod, notifier);
        } else {
            zestLauncher.junit4ClassRunnerRunChild(frameworkMethod, notifier);
        }
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        super.filter(new ZestFilter(filter));
    }

    @Override
    public Connection getConnection(DataSource dataSource) {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public Object createTest() throws Exception {
        return super.createTest();
    }
}
