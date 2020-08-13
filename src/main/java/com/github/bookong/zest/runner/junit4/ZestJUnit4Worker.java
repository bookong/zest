package com.github.bookong.zest.runner.junit4;

import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.ZestTestParam;
import com.github.bookong.zest.runner.ZestClassRunner;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.runner.junit4.annotation.ZestDataSource;
import com.github.bookong.zest.runner.junit4.annotation.ZestTest;
import com.github.bookong.zest.runner.junit4.statement.ZestFrameworkMethod;
import com.github.bookong.zest.runner.junit4.statement.ZestStatement;
import com.github.bookong.zest.util.ZestReflectHelper;
import com.github.bookong.zest.util.ZestTestCaseUtil;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jiangxu
 */
public class ZestJUnit4Worker extends ZestWorker {

    private TestClass       testClass;

    private ZestClassRunner zestClassRunner;

    public ZestJUnit4Worker(TestClass testClass, ZestClassRunner zestClassRunner){
        super();
        this.testClass = testClass;
        this.zestClassRunner = zestClassRunner;
    }

    public static List<FrameworkMethod> computeTestMethods(TestClass testCase) {
        List<FrameworkMethod> list = new ArrayList<>(testCase.getAnnotatedMethods(Test.class));

        for (FrameworkMethod method : testCase.getAnnotatedMethods(ZestTest.class)) {
            ZestTest zestTest = method.getAnnotation(ZestTest.class);
            String dir = getDir(testCase, method);
            String extName = ".".concat(zestTest.extName());

            if (StringUtils.isBlank(zestTest.value())) {
                File searchDir = new File(dir);
                File[] searchFiles = searchDir.listFiles();
                if (searchFiles != null) {
                    for (File searchFile : searchFiles) {
                        if (searchFile.isFile() && searchFile.getName().endsWith(extName)) {
                            list.add(new ZestFrameworkMethod(method, searchFile.getAbsolutePath()));
                        }
                    }
                }
            } else {
                list.add(new ZestFrameworkMethod(method, dir.concat(zestTest.value()).concat(extName)));
            }
        }

        return Collections.unmodifiableList(list);
    }

    private static String getDir(TestClass testCase, FrameworkMethod frameworkMethod) {
        return ZestTestCaseUtil.getDir(testCase.getJavaClass(), frameworkMethod.getName());
    }

    public void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        Description description = Description.createTestDescription(testClass.getJavaClass(), frameworkMethod.getName(),
                                                                    frameworkMethod.getAnnotations());

        if (ignoreTest()) {
            // 整个测试类忽略
            notifier.fireTestIgnored(description);

        } else if (frameworkMethod.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);

        } else {
            runTestCase((ZestFrameworkMethod) frameworkMethod, description, notifier);
        }
    }

    private void runTestCase(ZestFrameworkMethod frameworkMethod, Description description, RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();
        try {
            Statement statement = methodBlock(frameworkMethod);
            statement.evaluate();

        } catch (AssumptionViolatedException e) {
            eachNotifier.addFailedAssumption(e);

        } catch (AssertionError e) {
            eachNotifier.addFailure(e);

        } catch (Throwable e) {
            eachNotifier.addFailure(new RuntimeException(Messages.statementEvaluate(frameworkMethod.getTestCaseFilePath()),
                                                         e));
        } finally {
            eachNotifier.fireTestFinished();
        }
    }

    private Statement methodBlock(ZestFrameworkMethod method) throws Exception {
        Object test;
        try {
            test = new ReflectiveCallable() {

                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return zestClassRunner.createTest();
                }
            }.run();
        } catch (Throwable e) {
            return new Fail(e);
        }

        testCaseData = new TestCaseData();
        loadAutowiredFieldFromTest(test);
        loadTestCaseData(method);

        ZestStatement zestStatement = new ZestStatement(this, test, method);
        Statement statement = withBefores(test, zestStatement);
        statement = withAfters(test, statement);

        return statement;
    }

    protected void loadAutowiredFieldFromTest(Object test) throws Exception {
        connectionMap.clear();
        executerMap.clear();

        Class<?> clazz = test.getClass();
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                ZestDataSource zestDataSource = f.getAnnotation(ZestDataSource.class);
                if (zestDataSource != null) {
                    Object obj = ZestReflectHelper.getValue(test, f.getName());
                    if (obj instanceof DataSource) {
                        Connection conn = getConnection((DataSource) obj);
                        setConnection(zestDataSource.value(), conn);
                    } else {
                        throw new RuntimeException(Messages.parseDs());
                    }

                    setExecuter(zestDataSource.value(), zestDataSource.executerClass());
                    setDataConverter(zestDataSource.value(), zestDataSource.dataConverterClasses());
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    private boolean ignoreTest() {
        Class<?> clazz = testClass.getJavaClass();
        while (clazz != null) {
            if (clazz.getAnnotation(Ignore.class) != null) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }

        return false;
    }

    private Statement withBefores(Object target, Statement statement) {
        List<FrameworkMethod> befores = testClass.getAnnotatedMethods(Before.class);
        return befores.isEmpty() ? statement : new RunBefores(statement, befores, target);
    }

    private Statement withAfters(Object target, Statement statement) {
        List<FrameworkMethod> afters = testClass.getAnnotatedMethods(After.class);
        return afters.isEmpty() ? statement : new RunAfters(statement, afters, target);
    }

    private void loadTestCaseData(ZestFrameworkMethod method) throws Exception {
        Class<?>[] paramClasses = method.getMethod().getParameterTypes();
        ZestTestParam testParam = null;
        for (Class<?> paramClass : paramClasses) {
            if (ZestTestParam.class.isAssignableFrom(paramClass)) {
                if (testParam != null) {
                    throw new RuntimeException(Messages.initParam());
                }
                testParam = (ZestTestParam) paramClass.newInstance();
            }
        }

        if (testParam == null) {
            throw new RuntimeException(Messages.initParam());
        }

        testCaseData.setTestParam(testParam);
        currTestCaseFilePath = method.getTestCaseFilePath();
        ZestTestCaseUtil.loadFromAbsolutePath(this, currTestCaseFilePath, testCaseData);

        logger.info(Messages.statementRun(testCaseData.getDescription()));
        logger.info(currTestCaseFilePath);
    }

    @Override
    protected Connection getConnection(DataSource dataSource) {
        return zestClassRunner.getConnection(dataSource);
    }
}