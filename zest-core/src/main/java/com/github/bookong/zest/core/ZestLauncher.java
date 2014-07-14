package com.github.bookong.zest.core;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.github.bookong.zest.core.annotations.ZTest;
import com.github.bookong.zest.core.testcase.JsonTestCaseLoader;
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
	/** 支持多数据源 */
	private Map<String, Connection> connectionMap = new HashMap<String, Connection>();

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
			return rightDir(testClass.getResource("").getPath() + StringUtils.uncapitalize(testClass.getSimpleName())
					+ File.separator + frameworkMethod.getName());
		}
	}

	private String rightDir(String dir) {
		if (dir.endsWith(File.separator)) {
			return dir;
		} else {
			return dir + File.separator;
		}
	}

	public void setConnection(String id, Connection conn) {
		connectionMap.put(id, conn);
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
		// TODO
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bookong.minion.core.TestCaseOperator#checkTargetDb()
	 */
	public void checkTargetDb() {
		// TODO Auto-generated method stub

	}

}