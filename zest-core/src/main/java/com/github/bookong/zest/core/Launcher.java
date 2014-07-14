package com.github.bookong.zest.core;

import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.core.testcase.data.TestParam;


/**
 * @author jiangxu
 *
 */
public interface Launcher {
	/** 读取当前指定的 test case 文件，并返回读取后的对象 */
	TestCaseData loadCurrTestCaseFile(TestParam testParam);
	/** 显示 test case 的描述信息 */
	void showTestCaseDesc();
	/** 初始化DB中数据 */
	void initDb();
	/** 检查目标DB中数据是否符合预期 */
	void checkTargetDb();
}
