package zest.test;

import org.junit.runner.RunWith;

import zest.test.param.Param001;

import com.github.bookong.zest.ZestJUnit4ClassRunner;
import com.github.bookong.zest.core.annotations.ZTest;
import com.github.bookong.zest.core.testcase.JsonTestCaseLoader;
import com.github.bookong.zest.core.testcase.data.TestCaseData;

/**
 * @author jiangxu
 *
 */
@RunWith(ZestJUnit4ClassRunner.class)
public class JsonTestCaseLoaderTest {
	JsonTestCaseLoader testCaseLoader = new JsonTestCaseLoader();

	@ZTest
	public void test001(TestCaseData testCaseData, Param001 param) {
		System.out.println("1>" + param);
		System.out.println("2>" + testCaseData.toString());
	}
}
