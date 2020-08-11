package com.github.bookong.zest.runner;

import com.github.bookong.zest.core.Launcher;
import com.github.bookong.zest.core.ZestFilter;
import com.github.bookong.zest.core.annotation.ZestTest;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * @author jiangxu
 */
public class ZestSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner implements ZestClassRunner {

    private Launcher zestLauncher;

    public ZestSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError{
        super(clazz);
        zestLauncher = new Launcher(getTestClass(), this);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return Launcher.computeTestMethods(getTestClass());
    }

    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        ZestTest zest = frameworkMethod.getAnnotation(ZestTest.class);
        if (zest == null) {
            super.runChild(frameworkMethod, notifier);
        } else {
            zestLauncher.runChild(frameworkMethod, notifier);
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
