package com.github.bookong.zest.runner.junit4;

import com.github.bookong.zest.runner.ZestClassRunner;
import com.github.bookong.zest.annotation.ZestTest;
import com.github.bookong.zest.runner.junit4.statement.ZestFilter;
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
 * @author Jiang Xu
 */
public class ZestSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner implements ZestClassRunner {

    private ZestJUnit4Worker worker;

    public ZestSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError{
        super(clazz);
        worker = new ZestJUnit4Worker(getTestClass(), this);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return ZestJUnit4Worker.computeTestMethods(getTestClass());
    }

    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        ZestTest zest = frameworkMethod.getAnnotation(ZestTest.class);
        if (zest == null) {
            super.runChild(frameworkMethod, notifier);
        } else {
            worker.runChild(frameworkMethod, notifier);
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
