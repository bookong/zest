package com.github.bookong.zest.core;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.github.bookong.zest.core.annotations.ZTest;
import com.github.bookong.zest.core.executer.AbstractExcuter;
import com.github.bookong.zest.core.executer.AbstractJdbcExcuter;
import com.github.bookong.zest.core.testcase.JsonTestCaseLoader;
import com.github.bookong.zest.core.testcase.data.DataBase;
import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.core.testcase.data.TestParam;
import com.github.bookong.zest.exceptions.LoadTestCaseFileException;

public class ZestLauncher implements Launcher {
	private JsonTestCaseLoader testCaseLoader = new JsonTestCaseLoader();
	/** 被测试的类 */
	private Class<?> testClass;
	/** 当前要处理的 test case 文件路径 */
	private String currTestCaseFilePath;
	/** 从当前要处理的 json 中读取的测试用例 */
	private TestCaseData currTestCaseData;
	/** 要测试的数据库对应的 JDBC 连接对象 */
	private Map<String, Connection> connectionMap = new HashMap<String, Connection>();
	/** 要测试的数据库对应的执行器 */
	private Map<String, AbstractExcuter> executerMap = new HashMap<String, AbstractExcuter>();

	public void init(Class<?> clazz) {
		this.testClass = clazz;
	}

	public static void ignoreNoRunnableMethodsError(List<Throwable> errors) {
		for (int i = errors.size() - 1; i >= 0; i--) {
			Throwable e = errors.get(i);
			if ("No runnable methods".equals(e.getMessage())) {
				errors.remove(e);
			}
		}
	}

	public void run(FrameworkMethod frameworkMethod, Statement statement, Description description, RunNotifier notifier) {
		EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
		eachNotifier.fireTestStarted();

		try {
			ZTest minionTest = frameworkMethod.getAnnotation(ZTest.class);
			String dir = getDir(minionTest, frameworkMethod);

			if (minionTest.filenames().length == 0) {
				// 查找 dir 路径下所有文件
				boolean notFoundFile = true;
				File searchDir = new File(dir);
				File[] searchFiles = searchDir.listFiles();
				if (searchFiles != null) {
					for (File searchFile : searchFiles) {
						if (searchFile.isFile()) {
							notFoundFile = false;
							runTestCase(searchFile.getAbsolutePath(), statement);
						}
					}
				}

				if (notFoundFile) {
					throw new LoadTestCaseFileException("Not specified test case file (" + description + ")");
				}
			} else {
				for (String filename : minionTest.filenames()) {
					runTestCase(dir + filename, statement);
				}
			}

		} catch (AssumptionViolatedException e) {
			eachNotifier.addFailedAssumption(e);
		} catch (Throwable e) {
			eachNotifier.addFailure(e);
		} finally {
			eachNotifier.fireTestFinished();
		}
	}

	private void runTestCase(String filepath, Statement statement) {
		try {
			currTestCaseFilePath = filepath;
			statement.evaluate();
		} catch (Throwable e) {
			throw new RuntimeException("Fail to evaluate statement, test case in (" + filepath + ")", e);
		}
	}

	private String getDir(ZTest zest, FrameworkMethod frameworkMethod) {
		if (StringUtils.isNotBlank(zest.absoluteDir())) {
			return rightDir(zest.absoluteDir());
		} else if (StringUtils.isNotBlank(zest.relativePath())) {
			URL url = getClass().getClassLoader().getResource(zest.relativePath());
			if (url == null) {
				throw new LoadTestCaseFileException("Wrong relative path (" + zest.relativePath() + ")");
			}
			return rightDir(url.getPath());
		} else {
			return rightDir(testClass.getResource("").getPath() + StringUtils.lowerCase(testClass.getSimpleName())
					+ File.separator + StringUtils.lowerCase(frameworkMethod.getName()));
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

	/* (non-Javadoc)
	 * @see com.github.bookong.zest.core.Launcher#loadCurrTestCaseFile(com.github.bookong.zest.core.testcase.data.TestParam)
	 */
	public TestCaseData loadCurrTestCaseFile(TestParam testParam) {
		currTestCaseData = new TestCaseData();
		currTestCaseData.setParam(testParam);
		testCaseLoader.loadFromAbsolutePath(currTestCaseFilePath, currTestCaseData);
		return currTestCaseData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bookong.minion.core.TestCaseOperator#showTestCaseDesc()
	 */
	public void showTestCaseDesc() {
		System.out.println("[Zest] Test Case \"" + currTestCaseData.getDesc() + "\"");
		System.out.println(currTestCaseFilePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bookong.minion.core.TestCaseOperator#initDb()
	 */
	public void initDb() {
		for (Entry<String, DataBase> entry : currTestCaseData.getDataBases().entrySet()) {
			String databaseName = entry.getKey();
			AbstractExcuter executer = executerMap.get(databaseName);
			if (executer instanceof AbstractJdbcExcuter) {
				Connection connection = connectionMap.get(databaseName);
				((AbstractJdbcExcuter) executer).initDatabase(connection, entry.getValue(), currTestCaseData.getCurrDbTimeDiff());
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
					((AbstractJdbcExcuter) executer).checkTargetDatabase(connection, entry.getValue(), currTestCaseData.getCurrDbTimeDiff());
				}
				// FIXME 以后可能有 Mongo 的 Excuter
			}
		}
	}

	public Connection getJdbcConn(String databaseName) {
		return connectionMap.get(databaseName);
	}
}