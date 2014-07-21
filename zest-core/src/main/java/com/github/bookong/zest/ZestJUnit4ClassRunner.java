package com.github.bookong.zest;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.github.bookong.zest.core.ZestLauncher;
import com.github.bookong.zest.core.ZestStatement;
import com.github.bookong.zest.core.annotations.ZTest;

/**
 * @author jiangxu
 *
 */
public class ZestJUnit4ClassRunner extends BlockJUnit4ClassRunner {
	protected static final Log logger = LogFactory.getLog(ZestJUnit4ClassRunner.class);
	protected ZestLauncher zestLauncher = new ZestLauncher();

	public ZestJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		zestLauncher.init(clazz);
	}

	/**
	 * 忽略没有 Test 注解的方法这种错误，因为我们的测试方法是用 MTest 标识的
	 */
	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);
		ZestLauncher.ignoreNoRunnableMethodsError(errors);
	}

	/**
	 * 让 JUnit 接受 MTest 注解的方法
	 */
	@Override
	protected List<FrameworkMethod> getChildren() {
		List<FrameworkMethod> list = computeTestMethods();
		list.addAll(getTestClass().getAnnotatedMethods(ZTest.class));
		return list;
	}

	/**
	 * 如果是用 MTest 注解的方法，用 minonLauncher 来执行
	 */
	@Override
	protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
		ZTest mtest = frameworkMethod.getAnnotation(ZTest.class);
		if (mtest == null) {
			super.runChild(frameworkMethod, notifier);
		} else {
			Description description = describeChild(frameworkMethod);
			if (frameworkMethod.getAnnotation(Ignore.class) != null) {
				notifier.fireTestIgnored(description);
			} else {
				zestLauncher.run(frameworkMethod, methodBlock(frameworkMethod), description, notifier);
			}
		}
	}

	/**
	 * Returns a {@link Statement} that invokes {@code method} on {@code test}
	 */
	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new ZestStatement(method, test, zestLauncher);
	}
}