package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.runner.junit5.ZestJUnit5Worker;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class ZestDataTest {

    private Logger     logger = LoggerFactory.getLogger(getClass());

    private ZestWorker worker = new ZestJUnit5Worker();

    @Test
    public void testLoad001() {
        testLoadError("001.xml", Messages.parseZest());
    }

    @Test
    public void testLoad002() {
        testLoadError("002.xml", Messages.parseZestNecessary());
    }

    @Test
    public void testLoad003() {
        testLoadError("003.xml", Messages.parseZestNecessary());
    }

    @Test
    public void testLoad004() {
        testLoadError("004.xml", Messages.parseZestNecessary());
    }

    @Test
    public void testLoad005() {
        ZestData zestData = load("005.xml");
        Assert.assertFalse(zestData.isTransferTime());
        Assert.assertEquals(0L, zestData.getCurrentTimeDiff());
        Assert.assertEquals("test case description", zestData.getDescription());
    }

    @Test
    public void testLoad006() {
        ZestData zestData = load("006.xml");
        Assert.assertEquals(3.4F, zestData.getVersion(), 0.1F);
        Assert.assertTrue(zestData.isTransferTime());
        Assert.assertTrue(zestData.getCurrentTimeDiff() != 0L);
    }

    @Test
    public void testLoad007() {
        testLoadError("007.xml", Messages.parseCommonAttrUnknown("Zest", "U"));
    }

    @Test
    public void testLoad008() {
        testLoadError("008.xml", Messages.parseSourcesType());
    }

    @Test
    public void testLoad009() {
        testLoadError("009.xml", Messages.parseCommonAttrUnknown("Sources", "U"));
    }

    @Test
    public void testLoad010() {
        testLoadError("010.xml", Messages.parseSourceNecessary());
    }

    @Test
    public void testLoad011() {
        testLoadError("011.xml", Messages.parseSourceNecessary());
    }

    @Test
    public void testLoad012() {
        ZestData zestData = load("012.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        Assert.assertEquals("mysql", zestData.getSourceList().get(0).getId());
    }

    @Test
    public void testLoad013() {
        testLoadError("013.xml", Messages.parseSourceIdEmpty());
    }

    @Test
    public void testLoad014() {
        testLoadError("014.xml", Messages.parseSourceIdDuplicate("mysql"));
    }

    @Test
    public void testLoad015() {
        testLoadError("015.xml", Messages.parseParamType());
    }

    @Test
    public void testLoad016() {
        testLoadError("016.xml", Messages.parseParamNameEmpty());
    }

    @Test
    public void testLoad017() {
        testLoadError("017.xml", Messages.parseParamNameDuplicate("strValue"));
    }

    @Test
    public void testLoad018() {
        testLoadError("018.xml", Messages.parseParamNone("none"));
    }

    @Test
    public void testLoad019() {
        testLoadError("019.xml", Messages.parseParamObjLoad("intValue"), "For input string: \"str value\"");
    }

    @Test
    public void testLoad020() {
        testLoadError("020.xml", Messages.parseParamObjLoad("date1"), Messages.parseDate("str value"));
    }

    @Test
    public void testLoad021() {
        testLoadError("021.xml", Messages.parseParamObjLoad("nonsupportMapObj"), Messages.parseParamNonsupportMap());
    }

    @Test
    public void testLoad022() {
        ZestData zestData = load("022.xml");
        Param param = (Param) zestData.getParam();
        Assert.assertEquals(1, param.intValue);
        Assert.assertEquals(2, param.intObjValue.intValue());
        Assert.assertEquals(3, param.longValue);
        Assert.assertEquals(4, param.longObjValue.longValue());
        Assert.assertTrue(param.boolValue);
        Assert.assertTrue(param.boolObjValue);
        Assert.assertEquals(5.5, param.doubleValue, 0.1);
        Assert.assertEquals(6.5, param.doubleObjValue, 0.1);
        Assert.assertEquals(7.5, param.floatValue, 0.1);
        Assert.assertEquals(8.5, param.floatObjValue, 0.1);
        Assert.assertEquals("hello", param.strValue);
        Assert.assertEquals("2020-08-10 13:14:15", DateFormatUtils.format(param.date1, "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("2020-08-10 13:14:00", DateFormatUtils.format(param.date2, "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("2020-08-10 13:00:00", DateFormatUtils.format(param.date3, "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("2020-08-10 00:00:00", DateFormatUtils.format(param.date4, "yyyy-MM-dd HH:mm:ss"));

        Assert.assertEquals(2, param.listObj.size());
        Assert.assertEquals("1: param1 str", param.listObj.get(0).getStr());
        Assert.assertEquals("1: param2 str", param.listObj.get(0).getObj2().getStr());

        Assert.assertEquals("2: param1 str", param.listObj.get(1).getStr());
        Assert.assertEquals("2: param2 str", param.listObj.get(1).getObj2().getStr());

        Assert.assertEquals("param1 str", param.obj.getStr());
        Assert.assertEquals("param2 str", param.obj.getObj2().getStr());
    }

    private void testLoadError(String filename, String... errorMessages) {
        try {
            load(filename);
            Assert.fail();
        } catch (ZestException e) {
            logger.info(e.getMessage());
            Assert.assertEquals(getExpectMessage(filename, errorMessages), e.getMessage());
        }
    }

    private ZestData load(String filename) {
        String filePath = ZestDataTest.class.getResource(filename).getPath();
        ZestData zestData = new ZestData(filePath);
        zestData.setTestParam(new Param());
        zestData.load(worker);
        return zestData;
    }

    private String getExpectMessage(String filename, String... errorMessages) {
        String filePath = ZestDataTest.class.getResource(filename).getPath();
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.parse(filePath));
        for (String errorMessage : errorMessages) {
            sb.append("\n").append(errorMessage);
        }
        return sb.toString();
    }

    public static class Param implements ZestParam {

        private int                 intValue;
        private Integer             intObjValue;
        private long                longValue;
        private Long                longObjValue;
        private boolean             boolValue;
        private Boolean             boolObjValue;
        private double              doubleValue;
        private Double              doubleObjValue;
        private float               floatValue;
        private Float               floatObjValue;
        private String              strValue;
        private Date                date1;
        private Date                date2;
        private Date                date3;
        private Date                date4;
        private List<Param1>        listObj;
        private Param1              obj;
        private Map<String, String> nonsupportMapObj;
    }

    public static class Param1 {

        private String str;
        private Param2 obj2;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public Param2 getObj2() {
            return obj2;
        }

        public void setObj2(Param2 obj2) {
            this.obj2 = obj2;
        }
    }

    public static class Param2 {

        private String str;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }
}
