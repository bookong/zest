package zest;



import java.sql.Connection;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.TestClass;

import zest.param.Param;
import zest.param.ParamSub1;

import com.github.bookong.zest.core.Launcher;
import com.github.bookong.zest.core.testcase.JsonTestCaseLoader;
import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.core.testcase.data.TestParam;
import com.github.bookong.zest.util.DateUtils;

/**
 * @author jiangxu
 *
 */
public class JsonTestCaseLoaderTest implements Launcher {
	private JsonTestCaseLoader testCaseLoader = new JsonTestCaseLoader();
	private TestCaseData testCaseData;
	
	@Before
	public void setUp() throws Exception {
		testCaseData = new TestCaseData();
		testCaseData.setParam(new Param());
	}
	
	@Test
	public void testDesc() {
		testCaseLoader.loadFromAbsolutePath(getClass().getResource("001.json").getPath(), testCaseData, this);
		Assert.assertEquals("desc", "Test 001", testCaseData.getDesc());
		Assert.assertFalse("isTransferTime", testCaseData.isTransferTime());
	}
	
	@Test
	public void testData() {
		testCaseLoader.loadFromAbsolutePath(getClass().getResource("002.json").getPath(), testCaseData, this);
		Assert.assertTrue("isTransferTime", testCaseData.isTransferTime());
		Assert.assertTrue("currDbTimeDiff", (testCaseData.getCurrDbTimeDiff() > 0));
		Assert.assertEquals("database size", 2, testCaseData.getDataBases().size());
		
		Param param = (Param)testCaseData.getParam();
		Assert.assertEquals("param -> baseLong", 123L, param.getBaseLong());
		Assert.assertEquals("param -> classLong", Long.valueOf(456), param.getClassLong());
		Assert.assertNull("param -> nullLong", param.getNullLong());
		
		Assert.assertEquals("param -> baseLong", 123L, param.getBaseLong());
		Assert.assertEquals("param -> classLong", Long.valueOf(456), param.getClassLong());
		Assert.assertNull("param -> nullLong", param.getNullLong());
		
		Assert.assertEquals("param -> baseInt", 1234, param.getBaseInt());
		Assert.assertEquals("param -> classInt", Integer.valueOf(5678), param.getClassInt());
		Assert.assertNull("param -> nullInt", param.getNullInt());
		
		Assert.assertEquals("param -> baseBool", true, param.isBaseBool());
		Assert.assertEquals("param -> classBool", Boolean.TRUE, param.getClassBool());
		Assert.assertNull("param -> nullBool", param.getNullBool());
		
		Assert.assertEquals("param -> baseDouble", 55.0, param.getBaseDouble(), 0);
		Assert.assertEquals("param -> classDouble", Double.valueOf(4.56), param.getClassDouble());
		Assert.assertNull("param -> nullDouble", param.getNullDouble());
		
		Assert.assertEquals("param -> baseFloat", 56.0F, param.getBaseFloat(), 0);
		Assert.assertEquals("param -> classFloat", Float.valueOf(66.7F), param.getClassFloat());
		Assert.assertNull("param -> nullFloat", param.getNullFloat());
		
		Assert.assertEquals("param -> str","abc", param.getStr());
		Assert.assertNull("param -> nullStr", param.getNullStr());
		
		Assert.assertEquals("param -> date1","2014-12-23", new SimpleDateFormat("yyyy-MM-dd").format(param.getDate1()));
		Assert.assertEquals("param -> date2","2014-12-23 12", new SimpleDateFormat("yyyy-MM-dd HH").format(param.getDate2()));
		Assert.assertEquals("param -> date3","2014-12-23 12:33", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(param.getDate3()));
		Assert.assertEquals("param -> date4","2014-12-23 12:33:44", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(param.getDate4()));
		Assert.assertNull("param -> dateNull", param.getDateNull());
		
		ParamSub1 paramSub1 = param.getParamSub1();
		Assert.assertEquals("param -> paramSub1", "xxx", paramSub1.getStr());
		
		Assert.assertEquals("param -> paramSub1 -> subObjList size", 2, paramSub1.getSubObjList().size());
		Assert.assertEquals("param -> paramSub1 -> subObjList[0].str", "subObjList value 1", paramSub1.getSubObjList().get(0).getStr());
		Assert.assertEquals("param -> paramSub1 -> subObjList[1].str", "subObjList value 2", paramSub1.getSubObjList().get(1).getStr());
		
		Assert.assertEquals("param -> paramSub1 -> subObjMap size", 2, paramSub1.getSubObjMap().size());
		Assert.assertEquals("param -> paramSub1 -> subObjMap[key1].str", "subObjMap value 1", paramSub1.getSubObjMap().get("key1").getStr());
		Assert.assertEquals("param -> paramSub1 -> subObjMap[key2].str", "subObjMap value 2", paramSub1.getSubObjMap().get("key2").getStr());
		
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyLong size", 2, paramSub1.getSubObjMapKeyLong().size());
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyLong[1].str", "subObjMapKeyLong value 1", paramSub1.getSubObjMapKeyLong().get(Long.valueOf(1)).getStr());
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyLong[2].str", "subObjMapKeyLong value 2", paramSub1.getSubObjMapKeyLong().get(Long.valueOf(2)).getStr());
		
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyIntger size", 2, paramSub1.getSubObjMapKeyIntger().size());
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyIntger[1].str", "subObjMapKeyIntger value 1", paramSub1.getSubObjMapKeyIntger().get(Integer.valueOf(1)).getStr());
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyIntger[2].str", "subObjMapKeyIntger value 2", paramSub1.getSubObjMapKeyIntger().get(Integer.valueOf(2)).getStr());
		
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyDouble size", 2, paramSub1.getSubObjMapKeyDouble().size());
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyDouble[1].str", "subObjMapKeyDouble value 1", paramSub1.getSubObjMapKeyDouble().get(Double.valueOf(1.1)).getStr());
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyDouble[2].str", "subObjMapKeyDouble value 2", paramSub1.getSubObjMapKeyDouble().get(Double.valueOf(1.2)).getStr());
		
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyFloat size", 2, paramSub1.getSubObjMapKeyFloat().size());
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyFloat[1].str", "subObjMapKeyFloat value 1", paramSub1.getSubObjMapKeyFloat().get(Float.valueOf(2.1F)).getStr());
		Assert.assertEquals("param -> paramSub1 -> subObjMapKeyFloat[2].str", "subObjMapKeyFloat value 2", paramSub1.getSubObjMapKeyFloat().get(Float.valueOf(2.2F)).getStr());
	}
	

	@Override
	public TestClass getTestObject() {
		return null;
	}

	@Override
	public void loadCurrTestCaseFile(TestParam testParam) {
	}

	@Override
	public TestCaseData getCurrTestCaseData() {
		return null;
	}

	@Override
	public void initDb() {
	}

	@Override
	public void checkTargetDb() {
	}

	@Override
	public Connection getJdbcConn(String databaseName) {
		return null;
	}
}
