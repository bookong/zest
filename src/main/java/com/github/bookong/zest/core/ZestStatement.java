package com.github.bookong.zest.core;


/**
 * @author jiangxu
 *
 */
public class ZestStatement extends AbstractStatement {
	private final ZestFrameworkMethod zestMethod;
	
	public ZestStatement(Launcher launcher, Object target, ZestFrameworkMethod zestMethod) {
		super(launcher, target);
		this.zestMethod = zestMethod;
	}

	@Override
	public void evaluate() throws Throwable {
		getLauncher().initDb();
		invokeMethod(zestMethod);
		getLauncher().checkTargetDb();
	}
}