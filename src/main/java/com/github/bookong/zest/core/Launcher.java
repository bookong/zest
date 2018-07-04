package com.github.bookong.zest.core;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bookong.zest.core.annotations.ZestAfter;
import com.github.bookong.zest.core.annotations.ZestBefore;
import com.github.bookong.zest.core.annotations.ZestDataSource;
import com.github.bookong.zest.core.annotations.ZestTest;
import com.github.bookong.zest.core.executer.AbstractExcuter;
import com.github.bookong.zest.core.executer.AbstractJdbcExcuter;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.core.testcase.ZestTestParam;
import com.github.bookong.zest.runner.ZestClassRunner;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;

/**
 * @author jiangxu
 */
public class Launcher {

    private static Logger                logger         = LoggerFactory.getLogger(Launcher.class);

    private XmlTestCaseDataLoader        testCaseLoader = new XmlTestCaseDataLoader();
    /** 被测试的对象 */
    private TestClass                    testObject;
    /** 当前要处理的 test case 文件路径 */
    private String                       currTestCaseFilePath;
    /** 从当前要处理的 xml 中读取的测试用例 */
    private TestCaseData                 testCaseData;

    /** 要测试的数据库对应的 JDBC 连接对象 */
    private Map<String, Connection>      connectionMap  = new HashMap<String, Connection>();
    /** 要测试的数据库对应的执行器 */
    private Map<String, AbstractExcuter> executerMap    = new HashMap<String, AbstractExcuter>();

    private ZestClassRunner              zestClassRunner;

    /** 在 BlockJUnit4ClassRunner 子类的构造函数中调用 */
    public void junit4ClassRunnerConstructor(TestClass testObject, ZestClassRunner zestClassRunner) {
        this.testObject = testObject;
        this.zestClassRunner = zestClassRunner;
    }

    /** 在 BlockJUnit4ClassRunner 子类的 collectInitializationErrors 方法中调用 */
    public static void junit4ClassRunnerCollectInitializationErrors(List<Throwable> errors) {
        // 忽略没有 Test 注解的方法这种错误，因为我们的测试方法是用 ZestTest 标识的
        Iterator<Throwable> it = errors.iterator();
        while (it.hasNext()) {
            Throwable e = it.next();
            if ("No runnable methods".equals(e.getMessage())) { // $NON-NLS-1$
                it.remove();
            }
        }
    }

    /** 在 BlockJUnit4ClassRunner 子类的 getChildren 方法中调用 */
    public List<FrameworkMethod> junit4ClassRunnerGetChildren(List<FrameworkMethod> list) {
        List<FrameworkMethod> results = new ArrayList<>(list.size() + 1);
        results.addAll(list);
        // 让 JUnit 接受 ZestTest 注解的方法
        for (FrameworkMethod method : testObject.getAnnotatedMethods(ZestTest.class)) {
            ZestTest ztest = method.getAnnotation(ZestTest.class);
            String dir = getDir(ztest, method);
            if (StringUtils.isBlank(ztest.value())) {
                // 查找 dir 路径下所有文件
                File searchDir = new File(dir);
                File[] searchFiles = searchDir.listFiles();
                if (searchFiles != null) {
                    for (File searchFile : searchFiles) {
                        if (searchFile.isFile() && searchFile.getName().endsWith(".xml")) {
                            results.add(new ZestFrameworkMethod(method, searchFile.getAbsolutePath()));
                        }
                    }
                }
            } else {
                results.add(new ZestFrameworkMethod(method, dir + ztest.value()));
            }
        }
        return results;
    }

    /** 在 BlockJUnit4ClassRunner 子类的 runChild 方法中调用 */
    public void junit4ClassRunnerRunChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        Description description = Description.createTestDescription(testObject.getJavaClass(), frameworkMethod.getName(), frameworkMethod.getAnnotations());

        if (frameworkMethod.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            runTestCase((ZestFrameworkMethod) frameworkMethod, description, notifier);
        }
    }

    /** 运行单元测试 */
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
            e.printStackTrace();
            eachNotifier.addFailure(new RuntimeException(Messages.getString("launcher.failToEvaluateStatement", frameworkMethod.getTestCaseFilePath()), e));
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
        Statement statement = withZestBefores(this, test, zestStatement);
        statement = withBefores(test, statement);
        statement = withZestAfters(this, test, statement);
        statement = withAfters(test, statement);

        return statement;
    }

    /** 加载自动注入的内容 */
    private void loadAutowiredFieldFromTest(Object test) {
        connectionMap.clear();
        executerMap.clear();

        Class<?> clazz = test.getClass();
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                ZestDataSource zestDataSource = f.getAnnotation(ZestDataSource.class);
                if (zestDataSource != null) {
                    Object obj = ZestReflectHelper.getValueByFieldName(test, f.getName());
                    if (obj instanceof DataSource) {
                        Connection conn = zestClassRunner.getConnection((DataSource) obj);
                        setConnection(zestDataSource.id(), conn);
                        loadAllTableColSqlTypes(zestDataSource.id(), conn);
                    }

                    try {
                        setExecuter(zestDataSource.id(), zestDataSource.executerClazz().newInstance());
                    } catch (Exception e) {
                        throw new RuntimeException("Fail to set executer. Executer class:" + zestDataSource.executerClazz().getName(), e);
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /** 从数据库里获取所有表的列的类型 */
    private void loadAllTableColSqlTypes(String testDataSourceId, Connection conn) {
        DatabaseMetaData dbMetaData = null;
        ResultSet rs = null;
        try {
            dbMetaData = conn.getMetaData();
            List<String> tableNames = new ArrayList<>();
            rs = dbMetaData.getTables(null, null, null, new String[] { "TABLE" });
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
            rs = null;

            for (String tableName : tableNames) {
                rs = conn.getMetaData().getColumns(null, "%", tableName, "%");
                while (rs.next()) {
                    testCaseData.putRmdbTableColSqlTypes(testDataSourceId, tableName, rs.getString("column_name"), rs.getInt("data_type"));
                }
                rs.close();
                rs = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse Database MetaData", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e2) {
                    logger.error("", e2);
                }
            }
        }
    }

    private void loadTestCaseData(ZestFrameworkMethod method) throws Exception {
        Class<?>[] paramClasses = method.getMethod().getParameterTypes();
        ZestTestParam testParam = null;
        for (Class<?> paramClass : paramClasses) {
            if (ZestTestParam.class.isAssignableFrom(paramClass)) {
                if (testParam != null) {
                    throw new RuntimeException("The method must have and only one parameterized annotation with @ZestParam");
                }
                testParam = ZestTestParam.class.cast(paramClass.newInstance());
            }
        }

        if (testParam == null) {
            throw new RuntimeException("The method must have and only one ZestTestParam");
        }

        testCaseData.setTestParam(testParam);
        currTestCaseFilePath = method.getTestCaseFilePath();
        testCaseLoader.loadFromAbsolutePath(currTestCaseFilePath, testCaseData, this);

        logger.info("[Zest] Test Case \"" + testCaseData.getDescription() + "\"");
        logger.info(currTestCaseFilePath);
    }

    private Statement withBefores(Object target, Statement statement) {
        List<FrameworkMethod> befores = testObject.getAnnotatedMethods(Before.class);
        return befores.isEmpty() ? statement : new RunBefores(statement, befores, target);
    }

    private Statement withAfters(Object target, Statement statement) {
        List<FrameworkMethod> afters = testObject.getAnnotatedMethods(After.class);
        return afters.isEmpty() ? statement : new RunAfters(statement, afters, target);
    }

    private Statement withZestBefores(Launcher launcher, Object target, Statement statement) {
        List<FrameworkMethod> befores = testObject.getAnnotatedMethods(ZestBefore.class);
        return befores.isEmpty() ? statement : new RunZestBefores(launcher, target, statement, befores);
    }

    private Statement withZestAfters(Launcher launcher, Object target, Statement statement) {
        List<FrameworkMethod> afters = testObject.getAnnotatedMethods(ZestAfter.class);
        return afters.isEmpty() ? statement : new RunZestAfters(launcher, target, statement, afters);
    }

    private String getDir(ZestTest zest, FrameworkMethod frameworkMethod) {
        return rightDir(testObject.getJavaClass().getResource("").getPath() + "datas" + File.separator + testObject.getJavaClass().getSimpleName() + File.separator
                        + frameworkMethod.getName());

    }

    private String rightDir(String dir) {
        if (dir.endsWith(File.separator)) {
            return dir;
        } else {
            return dir + File.separator;
        }
    }

    public void setConnection(String databaseName, Connection conn) {
        connectionMap.put(databaseName, conn);
    }

    public void setExecuter(String databaseName, AbstractExcuter excuter) {
        executerMap.put(databaseName, excuter);
    }

    public void initDataSource() {
        for (TestCaseDataSource testCaseDataSource : testCaseData.getDataSources()) {
            AbstractExcuter executer = executerMap.get(testCaseDataSource.getId());
            if (executer instanceof AbstractJdbcExcuter) {
                Connection conn = connectionMap.get(testCaseDataSource.getId());
                ((AbstractJdbcExcuter) executer).initDatabase(conn, testCaseData, testCaseDataSource);
            }
            // FIXME 以后可能有 Mongo 的 Excuter
        }

    }

    public void checkTargetDataSource() {
        for (TestCaseDataSource testCaseDataSource : testCaseData.getDataSources()) {
            AbstractExcuter executer = executerMap.get(testCaseDataSource.getId());

            if (executer instanceof AbstractJdbcExcuter) {
                Connection conn = connectionMap.get(testCaseDataSource.getId());
                AbstractJdbcExcuter jdbcExcuter = (AbstractJdbcExcuter) executer;

                if (testCaseDataSource.isIgnoreTargetData()) {
                    logger.info(String.format("DataSource (Id : %1$s) ignore verify", testCaseDataSource.getId()));
                } else {
                    jdbcExcuter.checkTargetDatabase(conn, testCaseData, testCaseDataSource);
                }
                jdbcExcuter.clearDatabase(conn, testCaseData, testCaseDataSource);
            }
            // FIXME 以后可能有 Mongo 的 Excuter
        }
    }

    public Connection getJdbcConn(String databaseName) {
        return connectionMap.get(databaseName);
    }

    public TestClass getTestObject() {
        return testObject;
    }

    public TestCaseData getTestCaseData() {
        return testCaseData;
    }
}
