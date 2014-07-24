package zest.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import zest.test.param.Param00;

import com.github.bookong.zest.ZestJUnit4ClassRunner;
import com.github.bookong.zest.core.annotations.ZestBefore;
import com.github.bookong.zest.core.annotations.ZestTest;
import com.github.bookong.zest.core.testcase.JsonTestCaseLoader;
import com.github.bookong.zest.core.testcase.data.TestCaseData;

/**
 * @author jiangxu
 *
 */
@RunWith(ZestJUnit4ClassRunner.class)
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
//		System.out.println(testCaseData.toString());
	}
	
	@ZestBefore
	public void zestBefore(Param00 param) {
		System.out.println("=====> before");
	}
	
	@ZestTest(relativePath="zest/test", filenames={"001.json","002.json","003.json"})
	public void test003B(Param00 pram) {
		System.out.println("here");
	}
	
}
