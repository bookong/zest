package com.github.bookong.zest.core.testcase;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bookong.zest.core.testcase.data.TestCaseData;

/**
 * @author jiangxu
 *
 */
public abstract class AbstractTestCaseLoader {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void loadFromAbsolutePath(String filepath, TestCaseData testCaseData) {
		load(new File(filepath), testCaseData);
	}

	public abstract TestCaseData load(File file, TestCaseData testCaseData);
}
