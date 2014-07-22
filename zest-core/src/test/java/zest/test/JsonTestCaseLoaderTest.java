package zest.test;

import org.junit.Before;
import org.junit.Test;

import zest.test.param.Param00;

import com.github.bookong.zest.core.testcase.JsonTestCaseLoader;
import com.github.bookong.zest.core.testcase.data.TestCaseData;

/**
 * @author jiangxu
 *
 */
public class JsonTestCaseLoaderTest {
	JsonTestCaseLoader testCaseLoader = new JsonTestCaseLoader();
	TestCaseData testCaseData = null;
	
	@Before
	public void setUp() throws Exception {
		testCaseData = new TestCaseData();
	}

	@Test
	public void test001() {
		testCaseData.setParam(new Param00());
		testCaseLoader.loadFromAbsolutePath(getClass().getResource("001.json").getPath(), testCaseData);
	}
	
	@Test
	public void test002() {
		testCaseData.setParam(new Param00());
		testCaseLoader.loadFromAbsolutePath(getClass().getResource("002.json").getPath(), testCaseData);
	}

	@Test
	public void test003() {
		testCaseData.setParam(new Param00());
		testCaseLoader.loadFromAbsolutePath(getClass().getResource("003.json").getPath(), testCaseData);
		System.out.println(testCaseData.toString());
	}
}
