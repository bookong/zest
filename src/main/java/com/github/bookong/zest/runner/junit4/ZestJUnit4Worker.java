package com.github.bookong.zest.runner.junit4;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.ZestParam;
import com.github.bookong.zest.runner.ZestClassRunner;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.annotation.ZestTest;
import com.github.bookong.zest.runner.junit4.statement.ZestFrameworkMethod;
import com.github.bookong.zest.runner.junit4.statement.ZestStatement;
import com.github.bookong.zest.util.ZestUtil;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Xu
 */
public class ZestJUnit4Worker extends ZestWorker {

    private TestClass       testClass;

    private ZestClassRunner zestClassRunner;

    ZestJUnit4Worker(TestClass testClass, ZestClassRunner zestClassRunner){
        super();
        this.testClass = testClass;
        this.zestClassRunner = zestClassRunner;
    }

    static List<FrameworkMethod> computeTestMethods(TestClass testCase) {
        List<FrameworkMethod> list = new ArrayList<>(testCase.getAnnotatedMethods(Test.class));

        for (FrameworkMethod method : testCase.getAnnotatedMethods(ZestTest.class)) {
            ZestTest zestTest = method.getAnnotation(ZestTest.class);
            String dir = ZestUtil.getDir(testCase, method);

            if (StringUtils.isBlank(zestTest.value())) {
                File searchDir = new File(dir);
                File[] searchFiles = searchDir.listFiles();
                if (searchFiles != null) {
                    for (File searchFile : searchFiles) {
                        if (searchFile.isFile() && searchFile.getName().endsWith(zestTest.extName())) {
                            list.add(new ZestFrameworkMethod(method, searchFile.getAbsolutePath()));
                        }
                    }
                }
            } else {
                list.add(new ZestFrameworkMethod(method, dir.concat(zestTest.value()).concat(zestTest.extName())));
            }
        }

        return list;
    }

    void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
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

        } catch (Throwable e) {
            eachNotifier.addFailure(e);

        } finally {
            eachNotifier.fireTestFinished();
        }
    }

    private Statement methodBlock(ZestFrameworkMethod method) {
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

        loadAnnotation(test);

        ZestData zestData = new ZestData(method.getFilePath());
        loadZestData(zestData, method);

        ZestStatement zestStatement = new ZestStatement(this, zestData, test, method);
        Statement statement = withBefores(test, zestStatement);
        statement = withAfters(test, statement);

        logger.info(Messages.run(zestData.getDescription()));
        logger.info(method.getFilePath());

        return statement;
    }

    private void loadZestData(ZestData zestData, ZestFrameworkMethod method) {
        try {
            Class<?>[] paramClasses = method.getMethod().getParameterTypes();

            if (paramClasses.length > 1) {
                throw new ZestException(Messages.parseParamInit(method.getName()));
            }

            Class<?> paramClass = paramClasses[0];
            if (!ZestParam.class.isAssignableFrom(paramClass)) {
                throw new ZestException(Messages.parseParamInit(method.getName()));
            }

            ZestParam param = (ZestParam) paramClass.newInstance();
            zestData.setParam(param);
            param.setZestData(zestData);

            ZestUtil.loadZestData(this, zestData);

        } catch (Exception e) {
            throw new ZestException(Messages.failRun(), e);
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

}
