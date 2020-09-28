package com.github.bookong.zest.testcase.data.param;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.testcase.param.Param;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 测试元素 Zest/Param
 * 
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    @Override
    protected void initZestData(String filename, ZestData zestData) {
    }

    /** 不支持的子元素 */
    @Test
    public void testLoad01() {
        testLoadError("01.xml", Messages.parseParamError(), //
                      Messages.parseCommonChildrenList("Param", "ParamField"));
    }

    /** 属性 Name 为空 */
    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseParamError(), //
                      Messages.parseCommonAttrEmpty("Name"));
    }

    /** 属性 Name 重复 */
    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseParamError(), //
                      Messages.parseCommonAttrDuplicate("Name", "strValue"));
    }

    /** 测试参数属性中没有对应 Name 字段指定的值 */
    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseParamError(), //
                      Messages.parseParamNone("none"));
    }

    /** XML 给定的值无法转换成测试参数属性的类型 */
    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseParamError(), //
                      Messages.parseParamObjLoad("intValue"), //
                      "For input string: \"str value\"");
    }

    /** XML 给定的内容不符合规定的时间格式 */
    @Test
    public void testLoad06() {
        testLoadError("06.xml", Messages.parseParamError(), //
                      Messages.parseParamObjLoad("date1"), //
                      Messages.parseDataDate("str value"), //
                      "Unparseable date: \"str value\"");
    }

    /** 不支持想测试参数中 map 类型的字段赋值 */
    @Test
    public void testLoad07() {
        testLoadError("07.xml", Messages.parseParamError(), //
                      Messages.parseParamObjLoad("nonsupportMapObj"), //
                      Messages.parseParamNonsupportMap());
    }

    /** 一个正常赋值的例子 */
    @Test
    public void testLoad08() {
        ZestData zestData = load("08.xml");
        Param param = (Param) zestData.getParam();
        Assert.assertEquals(1, param.getIntValue());
        Assert.assertEquals(2, param.getIntObjValue().intValue());
        Assert.assertEquals(3, param.getLongValue());
        Assert.assertEquals(4, param.getLongObjValue().longValue());
        Assert.assertTrue(param.isBoolValue());
        Assert.assertTrue(param.getBoolObjValue());
        Assert.assertEquals(5.5, param.getDoubleValue(), 0.1);
        Assert.assertEquals(6.5, param.getDoubleObjValue(), 0.1);
        Assert.assertEquals(7.5, param.getFloatValue(), 0.1);
        Assert.assertEquals(8.5, param.getFloatObjValue(), 0.1);
        Assert.assertEquals("hello", param.getStrValue());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Assert.assertEquals("2020-08-10 13:14:15", dateFormat.format(param.getDate1()));

        Assert.assertEquals(2, param.getListObj().size());
        Assert.assertEquals("1: param1 str", param.getListObj().get(0).getStr());
        Assert.assertEquals("1: param2 str", param.getListObj().get(0).getObj2().getStr());

        Assert.assertEquals("2: param1 str", param.getListObj().get(1).getStr());
        Assert.assertEquals("2: param2 str", param.getListObj().get(1).getObj2().getStr());

        Assert.assertEquals("param1 str", param.getObj().getStr());
        Assert.assertEquals("param2 str", param.getObj().getObj2().getStr());
    }

    /** 不支持的属性 */
    @Test
    public void testLoad09() {
        testLoadError("09.xml", Messages.parseParamError(), //
                      Messages.parseCommonAttrUnknown("Param", "U"));
    }
}
