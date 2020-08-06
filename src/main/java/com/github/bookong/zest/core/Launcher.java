package com.github.bookong.zest.core;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.github.bookong.zest.core.testcase.AbstractDataConverter;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.apache.commons.lang.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bookong.zest.core.annotation.ZestAfter;
import com.github.bookong.zest.core.annotation.ZestBefore;
import com.github.bookong.zest.core.annotation.ZestDataSource;
import com.github.bookong.zest.core.annotation.ZestTest;
import com.github.bookong.zest.core.executer.AbstractExcuter;
import com.github.bookong.zest.core.executer.JdbcExcuter;
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

    private static Logger                            logger           = LoggerFactory.getLogger(Launcher.class);

    private XmlTestCaseDataLoader                    testCaseLoader   = new XmlTestCaseDataLoader();
    /** 被测试的对象 */
    private TestClass                                testClass;
    /** 当前要处理的 test case 文件路径 */
    private String                                   currTestCaseFilePath;
    /** 从当前要处理的 xml 中读取的测试用例 */
    private TestCaseData                             testCaseData;

    /** 要测试的数据库对应的 JDBC 连接对象 */
    private Map<String, Connection>                  connectionMap    = new HashMap<>();

    /** 要测试的数据库对应的执行器 */
    private Map<String, AbstractExcuter>             executerMap      = new HashMap<>();

    /** 要测试的数据库对应的数据转换器 */
    private Map<String, List<AbstractDataConverter>> dataConverterMap = new HashMap<>();

    private ZestClassRunner                          zestClassRunner;

    public Launcher(){
    }

    public Launcher(TestClass testClass, ZestClassRunner zestClassRunner){
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
        return String.format("%s%sdata%s%s%s%s%s", testCase.getJavaClass().getResource("").getPath(), File.separator, //
                             File.separator, //
                             testCase.getJavaClass().getSimpleName(), File.separator, //
                             frameworkMethod.getName(), File.separator);
    }

    void initDataSource() {
        for (TestCaseDataSource testCaseDataSource : testCaseData.getDataSources()) {
            AbstractExcuter executer = executerMap.get(testCaseDataSource.getId());
            if (executer instanceof JdbcExcuter) {
                Connection conn = connectionMap.get(testCaseDataSource.getId());
                ((JdbcExcuter) executer).initDatabase(conn, testCaseData, testCaseDataSource);
            }
        }
    }

    void checkTargetDataSource() {
        for (TestCaseDataSource dataSource : testCaseData.getDataSources()) {
            AbstractExcuter executer = executerMap.get(dataSource.getId());

            if (executer instanceof JdbcExcuter) {
                Connection conn = connectionMap.get(dataSource.getId());
                JdbcExcuter jdbcExcuter = (JdbcExcuter) executer;

                if (dataSource.getTargetData().isIgnoreCheck()) {
                    logger.info(Messages.ignoreTargetData(dataSource.getId()));
                } else {
                    jdbcExcuter.checkTargetDatabase(conn, testCaseData, dataSource);
                }

                jdbcExcuter.clearDatabase(conn, testCaseData, dataSource);
            }
        }
    }

    Connection getJdbcConn(String databaseName) {
        return connectionMap.get(databaseName);
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

    public TestCaseData getTestCaseData() {
        return testCaseData;
    }

    public Map<String, List<AbstractDataConverter>> getDataConverterMap() {
        return dataConverterMap;
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
        Statement statement = withZestBefores(this, test, zestStatement);
        statement = withBefores(test, statement);
        statement = withAfters(test, statement);
        statement = withZestAfters(this, test, statement);

        return statement;
    }

    private void loadAutowiredFieldFromTest(Object test) throws Exception {
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

    private void setConnection(String dataSourceId, Connection conn) {
        if (connectionMap.containsKey(dataSourceId)) {
            throw new RuntimeException(Messages.duplicateDs(dataSourceId));
        }

        connectionMap.put(dataSourceId, conn);
        loadAllTableColSqlTypes(dataSourceId, conn);
    }

    private void setExecuter(String dataSourceId, Class<? extends AbstractExcuter> executerClass) {
        if (executerMap.containsKey(dataSourceId)) {
            return;
        }

        try {
            executerMap.put(dataSourceId, executerClass.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(Messages.initExecuter(executerClass.getName()), e);
        }
    }

    private void setDataConverter(String dataSourceId, Class<? extends AbstractDataConverter>[] dataConverterClasses) {
        for (Class<? extends AbstractDataConverter> dataConverterClass : dataConverterClasses) {
            try {
                dataConverterMap.computeIfAbsent(dataSourceId,
                                                 o -> new ArrayList<>()).add(dataConverterClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(Messages.initDc(dataConverterClass.getName()), e);
            }
        }
    }

    private Statement withBefores(Object target, Statement statement) {
        List<FrameworkMethod> befores = testClass.getAnnotatedMethods(Before.class);
        return befores.isEmpty() ? statement : new RunBefores(statement, befores, target);
    }

    private Statement withAfters(Object target, Statement statement) {
        List<FrameworkMethod> afters = testClass.getAnnotatedMethods(After.class);
        return afters.isEmpty() ? statement : new RunAfters(statement, afters, target);
    }

    private Statement withZestBefores(Launcher launcher, Object target, Statement statement) {
        List<FrameworkMethod> befores = testClass.getAnnotatedMethods(ZestBefore.class);
        return befores.isEmpty() ? statement : new RunZestBefores(launcher, target, statement, befores);
    }

    private Statement withZestAfters(Launcher launcher, Object target, Statement statement) {
        List<FrameworkMethod> afters = testClass.getAnnotatedMethods(ZestAfter.class);
        return afters.isEmpty() ? statement : new RunZestAfters(launcher, target, statement, afters);
    }

    private void loadAllTableColSqlTypes(String dataSourceId, Connection conn) {
        DatabaseMetaData dbMetaData;
        ResultSet rs = null;
        try {
            dbMetaData = conn.getMetaData();
            List<String> tableNames = new ArrayList<>();
            rs = dbMetaData.getTables(null, null, null, new String[] { "TABLE" });
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            ZestSqlHelper.close(rs);

            for (String tableName : tableNames) {
                rs = conn.getMetaData().getColumns(null, "%", tableName, "%");
                while (rs.next()) {
                    testCaseData.putRmdbTableColSqlTypes(dataSourceId, tableName, rs.getString("column_name"),
                                                         rs.getInt("data_type"));
                }
                ZestSqlHelper.close(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(Messages.parseDbMeta(), e);
        } finally {
            ZestSqlHelper.close(rs);
        }
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
        testCaseLoader.loadFromAbsolutePath(this, currTestCaseFilePath, testCaseData);

        logger.info("[Zest] Test Case \"{}\"", testCaseData.getDescription());
        logger.info(currTestCaseFilePath);
    }

}
