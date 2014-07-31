package com.github.bookong.zest.core;

import java.lang.annotation.Annotation;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import com.github.bookong.zest.core.annotations.ZestBefore;
import com.github.bookong.zest.core.annotations.ZestJdbcConn;
import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.core.testcase.data.TestParam;

public class ZestStatement extends Statement {
	private final TestClass testCalss;
    private final FrameworkMethod testMethod;
    private Object target;
    private Launcher launcher;

    public ZestStatement(TestClass testCase, FrameworkMethod testMethod, Object target, Launcher launcher) {
    	this.testCalss = testCase;
        this.testMethod = testMethod;
        this.target = target;
        this.launcher = launcher;
    }

    @Override
    public void evaluate() throws Throwable {
    	Class<?>[] paramClasses = testMethod.getMethod().getParameterTypes(); // 方法所有的参数定义
    	TestCaseData testCase = launcher.loadCurrTestCaseFile(createTestParam(paramClasses));
    	launcher.showTestCaseDesc();
    	
    	
		Annotation[][] allParamAnnotations = testMethod.getMethod().getParameterAnnotations();
		Object[] paramArrayOfObject = new Object[paramClasses.length];
		for (int i=0; i<paramClasses.length; i++) {
			paramArrayOfObject[i] = genParamObject(paramClasses[i], allParamAnnotations[i], testCase);
		}
		
		runZestBeforeMethod(testCase);
		
		launcher.initDb();
		testMethod.invokeExplosively(target, paramArrayOfObject);

		launcher.checkTargetDb();
    }
    
    private void runZestBeforeMethod(TestCaseData testCase) throws Throwable {
    	for (FrameworkMethod zestBeforeMethod : testCalss.getAnnotatedMethods(ZestBefore.class)) {
    		Class<?>[] paramClasses = zestBeforeMethod.getMethod().getParameterTypes();
    		Annotation[][] allParamAnnotations = zestBeforeMethod.getMethod().getParameterAnnotations();
    		Object[] paramArrayOfObject = new Object[paramClasses.length];
    		for (int i=0; i<paramClasses.length; i++) {
    			paramArrayOfObject[i] = genParamObject(paramClasses[i], allParamAnnotations[i], testCase);
    		}
			zestBeforeMethod.invokeExplosively(target, paramArrayOfObject);
		}
    }
    
	private Object genParamObject(Class<?> paramClass, Annotation[] paramAnnotations, TestCaseData testCase) throws InstantiationException, IllegalAccessException {
		for (Annotation item : paramAnnotations) {
			if (item instanceof ZestJdbcConn) {
				ZestJdbcConn zestJdbcConn = (ZestJdbcConn) item;
				return launcher.getJdbcConn(zestJdbcConn.value());
			}
		}
		
		if (TestParam.class.isAssignableFrom(paramClass)) {
			return testCase.getParam();
		} else if (TestCaseData.class.isAssignableFrom(paramClass)) {
			return testCase;
		} else {
			throw new RuntimeException("Unknown test param class " + paramClass.getName());
		}
	}
	
	private TestParam createTestParam(Class<?>[] paramClasses) throws Exception {
		TestParam testParam = null;
		for (Class<?> paramClass : paramClasses) {
			if (TestParam.class.isAssignableFrom(paramClass)) {
				if (testParam != null) {
					throw new RuntimeException("Parameters of the method must have only one type of value TestParam.");
				}
				testParam = (TestParam)paramClass.newInstance();
			}
		}
		
		if (testParam == null) {
			throw new RuntimeException("Parameters of the method must have only one type of value TestParam.");
		}
		
		return testParam;
	}
}