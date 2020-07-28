package zest;

import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.bookong.zest.core.Launcher;
import com.github.bookong.zest.core.XmlTestCaseDataLoader;
import com.github.bookong.zest.core.testcase.TestCaseData;

import zest.param.Param1;
import zest.param.Param3;
import zest.param.Param4;

/**
 *
 */
public class XmlTestCaseDataLoaderTest {

    private XmlTestCaseDataLoader loader;
    private Launcher              zestLauncher;
    private TestCaseData              zestData;

    @Before
    public void setUp() throws Exception {
        loader = new XmlTestCaseDataLoader();
        zestLauncher = new Launcher();
        zestData = new TestCaseData();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOK() {
        zestData.setTestParam(new Param1());
        try {
//            loader.loadFromAbsolutePath(XmlTestCaseDataLoaderTest.class.getResource("/zest/data1.xml").getPath(), zestData, zestLauncher);
            Param1 obj = (Param1) zestData.getTestParam();

            Assert.assertEquals("data1.xml", zestData.getFileName());
            Assert.assertEquals("可选描述信息", zestData.getDescription());

            Assert.assertEquals("Text 1", obj.getSuperClassStr());

            Assert.assertEquals(101L, obj.getLongBase());
            Assert.assertEquals(new Long(102L), obj.getLongClass());
            Assert.assertNull(obj.getLongNeedNull());

            Assert.assertEquals(104, obj.getIntBase());
            Assert.assertEquals(new Integer(105), obj.getIntClass());
            Assert.assertNull(obj.getIntNeedNull());

            Assert.assertTrue(obj.isBooleanBase());
            Assert.assertTrue(obj.getBooleanClass());
            Assert.assertNull(obj.getBooleanNeedNull());

            Assert.assertEquals(110D, obj.getDoubleBase(), 0.1);
            Assert.assertEquals(new Double(111D), obj.getDoubleClass());
            Assert.assertNull(obj.getDoubleNeedNull());

            Assert.assertEquals(113F, obj.getFloatBase(), 0.1);
            Assert.assertEquals(new Float(114F), obj.getFloatClass());
            Assert.assertNull(obj.getFloatNeedNull());

            Assert.assertEquals("new str", obj.getStr());
            Assert.assertNull(obj.getStrNeedNull());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Assert.assertEquals("2018-04-26 11:12:13", sdf.format(obj.getDate1()));
            Assert.assertEquals("2018-04-26 11:12:00", sdf.format(obj.getDate2()));
            Assert.assertEquals("2018-04-26 11:00:00", sdf.format(obj.getDate3()));
            Assert.assertEquals("2018-04-26 00:00:00", sdf.format(obj.getDate4()));
            Assert.assertEquals("2018-04-27 11:12:13", sdf.format(obj.getDate5()));
            Assert.assertNull(obj.getDateNeedNull());

            Assert.assertEquals(2, obj.getLongList().size());
            Assert.assertEquals(1L, obj.getLongList().get(0).longValue());
            Assert.assertEquals(2L, obj.getLongList().get(1).longValue());

            Assert.assertEquals(2, obj.getIntList().size());
            Assert.assertEquals(1, obj.getIntList().get(0).intValue());
            Assert.assertEquals(2, obj.getIntList().get(1).intValue());

            Assert.assertEquals(2, obj.getBooleanList().size());
            Assert.assertTrue(obj.getBooleanList().get(0).booleanValue());
            Assert.assertFalse(obj.getBooleanList().get(1).booleanValue());

            Assert.assertEquals(2, obj.getDoubleList().size());
            Assert.assertEquals(1.1D, obj.getDoubleList().get(0).doubleValue(), 0.1);
            Assert.assertEquals(2.2D, obj.getDoubleList().get(1).doubleValue(), 0.1);

            Assert.assertEquals(2, obj.getFloatList().size());
            Assert.assertEquals(1.11F, obj.getFloatList().get(0).floatValue(), 0.1);
            Assert.assertEquals(2.22F, obj.getFloatList().get(1).floatValue(), 0.1);

            Assert.assertEquals(2, obj.getStrList().size());
            Assert.assertEquals("str list value 1", obj.getStrList().get(0));
            Assert.assertEquals("str list value 2", obj.getStrList().get(1));

            Assert.assertEquals(2, obj.getDateList().size());
            Assert.assertEquals("2018-05-01 11:12:13", sdf.format(obj.getDateList().get(0)));
            Assert.assertEquals("2018-05-01 11:12:14", sdf.format(obj.getDateList().get(1)));

            Assert.assertEquals(2, obj.getLongMap().size());
            Assert.assertEquals(101L, obj.getLongMap().get("a").longValue());
            Assert.assertEquals(102L, obj.getLongMap().get("b").longValue());

            Assert.assertEquals(2, obj.getIntMap().size());
            Assert.assertEquals(101, obj.getIntMap().get("a").intValue());
            Assert.assertEquals(102, obj.getIntMap().get("b").intValue());

            Assert.assertEquals(2, obj.getBooleanMap().size());
            Assert.assertFalse(obj.getBooleanMap().get("a").booleanValue());
            Assert.assertTrue(obj.getBooleanMap().get("b").booleanValue());

            Assert.assertEquals(2, obj.getDoubleMap().size());
            Assert.assertEquals(101.1D, obj.getDoubleMap().get("a").doubleValue(), 0.1);
            Assert.assertEquals(102.2D, obj.getDoubleMap().get("b").doubleValue(), 0.1);

            Assert.assertEquals(2, obj.getFloatMap().size());
            Assert.assertEquals(101.11F, obj.getFloatMap().get("a").floatValue(), 0.1);
            Assert.assertEquals(102.22F, obj.getFloatMap().get("b").floatValue(), 0.1);

            Assert.assertEquals(2, obj.getStrMap().size());
            Assert.assertEquals("str list value 101", obj.getStrMap().get("a"));
            Assert.assertEquals("str list value 102", obj.getStrMap().get("b"));

            Assert.assertEquals(2, obj.getDateMap().size());
            Assert.assertEquals("2018-05-02 11:12:13", sdf.format(obj.getDateMap().get("a")));
            Assert.assertEquals("2018-05-02 11:12:14", sdf.format(obj.getDateMap().get("b")));

            Assert.assertEquals("sub object str", obj.getObj().getStr());

            Assert.assertEquals(2, obj.getObj().getLongList().size());
            Assert.assertEquals(901L, obj.getObj().getLongList().get(0).longValue());
            Assert.assertEquals(902L, obj.getObj().getLongList().get(1).longValue());

            Assert.assertEquals(2, obj.getObj().getIntList().size());
            Assert.assertEquals(901, obj.getObj().getIntList().get(0).intValue());
            Assert.assertEquals(902, obj.getObj().getIntList().get(1).intValue());

            Assert.assertEquals(2, obj.getObj().getBooleanList().size());
            Assert.assertTrue(obj.getObj().getBooleanList().get(0).booleanValue());
            Assert.assertFalse(obj.getObj().getBooleanList().get(1).booleanValue());

            Assert.assertEquals(2, obj.getObj().getDoubleList().size());
            Assert.assertEquals(901.1D, obj.getObj().getDoubleList().get(0).doubleValue(), 0.1);
            Assert.assertEquals(902.2D, obj.getObj().getDoubleList().get(1).doubleValue(), 0.1);

            Assert.assertEquals(2, obj.getObj().getFloatList().size());
            Assert.assertEquals(901.11F, obj.getObj().getFloatList().get(0).floatValue(), 0.1);
            Assert.assertEquals(902.22F, obj.getObj().getFloatList().get(1).floatValue(), 0.1);

            Assert.assertEquals(2, obj.getObj().getStrList().size());
            Assert.assertEquals("str list value 901", obj.getObj().getStrList().get(0));
            Assert.assertEquals("str list value 902", obj.getObj().getStrList().get(1));

            Assert.assertEquals(2, obj.getObj().getDateList().size());
            Assert.assertEquals("2018-05-11 11:12:13", sdf.format(obj.getObj().getDateList().get(0)));
            Assert.assertEquals("2018-05-11 11:12:14", sdf.format(obj.getObj().getDateList().get(1)));

            Assert.assertEquals(2, obj.getObj().getLongMap().size());
            Assert.assertEquals(90101L, obj.getObj().getLongMap().get("a").longValue());
            Assert.assertEquals(90102L, obj.getObj().getLongMap().get("b").longValue());

            Assert.assertEquals(2, obj.getObj().getIntMap().size());
            Assert.assertEquals(90101, obj.getObj().getIntMap().get("a").intValue());
            Assert.assertEquals(90102, obj.getObj().getIntMap().get("b").intValue());

            Assert.assertEquals(2, obj.getObj().getBooleanMap().size());
            Assert.assertFalse(obj.getObj().getBooleanMap().get("a").booleanValue());
            Assert.assertTrue(obj.getObj().getBooleanMap().get("b").booleanValue());

            Assert.assertEquals(2, obj.getObj().getDoubleMap().size());
            Assert.assertEquals(90101.1D, obj.getObj().getDoubleMap().get("a").doubleValue(), 0.1);
            Assert.assertEquals(90102.2D, obj.getObj().getDoubleMap().get("b").doubleValue(), 0.1);

            Assert.assertEquals(2, obj.getObj().getFloatMap().size());
            Assert.assertEquals(90101.11F, obj.getObj().getFloatMap().get("a").floatValue(), 0.1);
            Assert.assertEquals(90102.22F, obj.getObj().getFloatMap().get("b").floatValue(), 0.1);

            Assert.assertEquals(2, obj.getObj().getStrMap().size());
            Assert.assertEquals("str list value 90101", obj.getObj().getStrMap().get("a"));
            Assert.assertEquals("str list value 90102", obj.getObj().getStrMap().get("b"));

            Assert.assertEquals(2, obj.getObj().getDateMap().size());
            Assert.assertEquals("2018-05-12 11:12:13", sdf.format(obj.getObj().getDateMap().get("a")));
            Assert.assertEquals("2018-05-12 11:12:14", sdf.format(obj.getObj().getDateMap().get("b")));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件不存在
     */
    @Test
    public void testNoThisFile() {
        zestData.setTestParam(new Param1());
        try {
//            loader.loadFromAbsolutePath("/zest/NO_THIS_FILE.xml", zestData, zestLauncher);
            Assert.fail();
        } catch (Exception e) {
            // e.printStackTrace();
//             Assert.assertEquals(ParseTestCaseException.class.getName(), e.getClass().getName());
        }
    }

    /**
     * xml 格式错误
     */
    @Test
    public void testXmlParseFail() {
        zestData.setTestParam(new Param1());
        try {
//            loader.loadFromAbsolutePath(XmlTestCaseDataLoaderTest.class.getResource("/zest/data2.xml").getPath(), zestData, zestLauncher);
            Assert.fail();
        } catch (Exception e) {
            // e.printStackTrace();
//            Assert.assertEquals(ParseTestCaseException.class.getName(), e.getClass().getName());
        }
    }

    /**
     * List 内容对象类型不支持
     */
    @Test
    public void testListValueNotSupportedError() {
        zestData.setTestParam(new Param3());
        try {
//            loader.loadFromAbsolutePath(XmlTestCaseDataLoaderTest.class.getResource("/zest/data3.xml").getPath(), zestData, zestLauncher);
            Assert.fail();
        } catch (Exception e) {
            // e.printStackTrace();
//            Assert.assertEquals(ParseTestCaseException.class.getName(), e.getClass().getName());
        }
    }

    /**
     * Map 的 Value 对象类型不支持
     */
    @Test
    public void testMapKeyNotSupportedError() {
        zestData.setTestParam(new Param4());
        try {
//            loader.loadFromAbsolutePath(XmlTestCaseDataLoaderTest.class.getResource("/zest/data4.xml").getPath(), zestData, zestLauncher);
            Assert.fail();
        } catch (Exception e) {
            // e.printStackTrace();
//            Assert.assertEquals(ParseTestCaseException.class.getName(), e.getClass().getName());
        }
    }

}
