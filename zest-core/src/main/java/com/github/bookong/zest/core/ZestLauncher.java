package com.github.bookong.zest.core;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import com.github.bookong.zest.ZestClassRunner;
import com.github.bookong.zest.core.annotations.ZestAfter;
import com.github.bookong.zest.core.annotations.ZestBefore;
import com.github.bookong.zest.core.annotations.ZestDataSource;
import com.github.bookong.zest.core.annotations.ZestTest;
import com.github.bookong.zest.core.executer.AbstractExcuter;
import com.github.bookong.zest.core.executer.AbstractJdbcExcuter;
import com.github.bookong.zest.core.testcase.JsonTestCaseLoader;
import com.github.bookong.zest.core.testcase.data.DataBase;
import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.core.testcase.data.TestParam;
import com.github.bookong.zest.exceptions.LoadTestCaseFileException;
import com.github.bookong.zest.util.ReflectHelper;

public class ZestLauncher implements Launcher {
	private JsonTestCaseLoader testCaseLoader = new JsonTestCaseLoader();
	/** 被测试的对象 */
	private TestClass testObject;
	/** 当前要处理的 test case 文件路径 */
	private String currTestCaseFilePath;
	/** 从当前要处理的 json 中读取的测试用例 */
	private TestCaseData currTestCaseData;
	/** 要测试的数据库对应的 JDBC 连接对象 */
	private Map<String, Connection> connectionMap = new HashMap<String, Connection>();
	/** 要测试的数据库对应的执行器 */
	private Map<String, AbstractExcuter> executerMap = new HashMap<String, AbstractExcuter>();

	/** 在 BlockJUnit4ClassRunner 子类的构造函数中调用 */
	public void junit4ClassRunnerConstructor(TestClass testObject, ZestClassRunner zestClassRunner) {
		this.testObject = testObject;

		for (Field f : testObject.getClass().getDeclaredFields()) {
			ZestDataSource zestDataSource = f.getAnnotation(ZestDataSource.class);
			if (zestDataSource != null) {
				Object obj = ReflectHelper.getValueByFieldName(testObject.getClass(), f.getName());
				if (obj instanceof DataSource) {
					setConnection(zestDataSource.value(), zestClassRunner.getConnection((DataSource) obj));
				}

				try {
					setExecuter(zestDataSource.value(), zestDataSource.executerClazz().newInstance());
				} catch (Exception e) {
					throw new RuntimeException("Fail to set executer. Executer class:"
							+ zestDataSource.executerClazz().getName(), e);
				}
			}
		}
	}

	/** 在 BlockJUnit4ClassRunner 子类的 collectInitializationErrors 方法中调用 */
	public static void junit4ClassRunnerCollectInitializationErrors(List<Throwable> errors) {
		// 忽略没有 Test 注解的方法这种错误，因为我们的测试方法是用 ZestTest 标识的
		Iterator<Throwable> it = errors.iterator();
		while (it.hasNext()) {
			Throwable e = it.next();
			if ("No runnable methods".equals(e.getMessage())) {
				it.remove();
			}
		}
	}

	/** 在 BlockJUnit4ClassRunner 子类的 getChildren 方法中调用 */
	public List<FrameworkMethod> junit4ClassRunnerGetChildren(List<FrameworkMethod> list) {
		// 让 JUnit 接受 ZestTest 注解的方法
		for (FrameworkMethod method : testObject.getAnnotatedMethods(ZestTest.class)) {
			ZestTest ztest = method.getAnnotation(ZestTest.class);
			String dir = getDir(ztest, method);
			if (ztest.filenames().length == 0) {
				// 查找 dir 路径下所有文件
				File searchDir = new File(dir);
				File[] searchFiles = searchDir.listFiles();
				if (searchFiles != null) {
					for (File searchFile : searchFiles) {
						if (searchFile.isFile()) {
							list.add(new ZestFrameworkMethod(method, searchFile.getAbsolutePath()));
						}
					}
				}
			} else {
				for (String filename : ztest.filenames()) {
					list.add(new ZestFrameworkMethod(method, dir + filename));
				}
			}
		}
		return list;
	}

	/** 在 BlockJUnit4ClassRunner 子类的 runChild 方法中调用 */
	public void junit4ClassRunnerRunChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
		Description description = Description.createTestDescription(testObject.getJavaClass(),
				frameworkMethod.getName(), frameworkMethod.getAnnotations());

		if (frameworkMethod.getAnnotation(Ignore.class) != null) {
			notifier.fireTestIgnored(description);
		} else {
			runTestCase((ZestFrameworkMethod)frameworkMethod, description, notifier);
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
			e.printStackTrace();
			eachNotifier.addFailure(new RuntimeException("Fail to evaluate statement, test case in (" + frameworkMethod.getTestCaseFilePath() + ")",
					e));
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
					return testObject.getOnlyConstructor().newInstance();
				}
			}.run();
		} catch (Throwable e) {
			return new Fail(e);
		}
		
		loadTestCaseData(method);
		showTestCaseDesc();
		
		ZestStatement zestStatement = new ZestStatement(this, test, method);
		Statement statement = withZestBefores(this, test, zestStatement);
		statement = withBefores(test, statement);
		statement = withZestAfters(this, test, statement);
		statement = withAfters(test, statement);
		
		return statement;
	}
	
	private void loadTestCaseData(ZestFrameworkMethod method) {
		try {
			Class<?>[] paramClasses = method.getMethod().getParameterTypes();
			TestParam testParam = null;
			int testParamCount = 0;
			for (Class<?> paramClass : paramClasses) {
				if (TestParam.class.isAssignableFrom(paramClass)) {
					testParamCount++;
					testParam = (TestParam)paramClass.newInstance();
				}
			}
			
			if (testParamCount != 1) {
				throw new RuntimeException("Parameters of the method must have only one type of value TestParam.");
			}
			
			currTestCaseFilePath = method.getTestCaseFilePath();
			loadCurrTestCaseFile(testParam);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
		if (StringUtils.isNotBlank(zest.absoluteDir())) {
			return rightDir(zest.absoluteDir());
		} else if (StringUtils.isNotBlank(zest.relativePath())) {
			URL url = getClass().getClassLoader().getResource(zest.relativePath());
			if (url == null) {
				throw new LoadTestCaseFileException("Wrong relative path (" + zest.relativePath() + ")");
			}
			return rightDir(url.getPath());
		} else {
			return rightDir(testObject.getClass().getResource("").getPath()
					+ StringUtils.lowerCase(testObject.getClass().getSimpleName()) + File.separator
					+ StringUtils.lowerCase(frameworkMethod.getName()));
		}
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

	@Override
	public void loadCurrTestCaseFile(TestParam testParam) {
		currTestCaseData = new TestCaseData();
		currTestCaseData.setParam(testParam);
		testCaseLoader.loadFromAbsolutePath(currTestCaseFilePath, currTestCaseData);
	}

	@Override
	public void showTestCaseDesc() {
		System.out.println("[Zest] Test Case \"" + currTestCaseData.getDesc() + "\"");
		System.out.println(currTestCaseFilePath);
	}

	@Override
	public void initDb() {
		for (Entry<String, DataBase> entry : currTestCaseData.getDataBases().entrySet()) {
			String databaseName = entry.getKey();
			AbstractExcuter executer = executerMap.get(databaseName);
			if (executer instanceof AbstractJdbcExcuter) {
				Connection connection = connectionMap.get(databaseName);
				((AbstractJdbcExcuter) executer).initDatabase(connection, entry.getValue(),
						currTestCaseData.getCurrDbTimeDiff());
			}
			// FIXME 以后可能有 Mongo 的 Excuter
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bookong.minion.core.TestCaseOperator#checkTargetDb()
	 */
	public void checkTargetDb() {
		for (Entry<String, DataBase> entry : currTestCaseData.getDataBases().entrySet()) {
			String databaseName = entry.getKey();
			if (entry.getValue().isIgnoreTargetDbVerify()) {
				System.out.println("Ignore target database verify. Database name:" + entry.getKey());
			} else {
				AbstractExcuter executer = executerMap.get(databaseName);
				if (executer instanceof AbstractJdbcExcuter) {
					Connection connection = connectionMap.get(databaseName);
					((AbstractJdbcExcuter) executer).checkTargetDatabase(connection, entry.getValue(),
							currTestCaseData.getCurrDbTimeDiff());
				}
				// FIXME 以后可能有 Mongo 的 Excuter
			}
		}
	}

	public Connection getJdbcConn(String databaseName) {
		return connectionMap.get(databaseName);
	}

	@Override
	public TestClass getTestObject() {
		return testObject;
	}

	@Override
	public TestCaseData getCurrTestCaseData() {
		return currTestCaseData;
	}
}