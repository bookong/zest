package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.runner.junit5.ZestJUnit5Worker;
import com.github.bookong.zest.testcase.mock.MockConnection;
import com.github.bookong.zest.testcase.mock.MockMongoOperations;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import java.sql.Connection;
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
        logger.info("Normal data");
        ZestData zestData = load("005.xml");
        Assert.assertFalse(zestData.isTransferTime());
        Assert.assertEquals(0L, zestData.getCurrentTimeDiff());
        Assert.assertEquals("test case description", zestData.getDescription());
    }

    @Test
    public void testLoad006() {
        logger.info("Normal data");
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
        testLoadError("010.xml", Messages.parseSourceError("mysql"), Messages.parseSourceNecessary());
    }

    @Test
    public void testLoad011() {
        testLoadError("011.xml", Messages.parseSourceError("mysql"), Messages.parseSourceNecessary());
    }

    @Test
    public void testLoad012() {
        logger.info("Normal data");
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
        testLoadError("019.xml", Messages.parseParamObjLoad("intValue"), //
                      "For input string: \"str value\"");
    }

    @Test
    public void testLoad020() {
        testLoadError("020.xml", Messages.parseParamObjLoad("date1"), //
                      Messages.parseDate("str value"));
    }

    @Test
    public void testLoad021() {
        testLoadError("021.xml", Messages.parseParamObjLoad("nonsupportMapObj"), //
                      Messages.parseParamNonsupportMap());
    }

    @Test
    public void testLoad022() {
        logger.info("Normal data");
        ZestData zestData = load("022.xml");
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
        Assert.assertEquals("2020-08-10 13:14:15", DateFormatUtils.format(param.getDate1(), //
                                                                          "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("2020-08-10 13:14:00", DateFormatUtils.format(param.getDate2(), //
                                                                          "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("2020-08-10 13:00:00", DateFormatUtils.format(param.getDate3(), //
                                                                          "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("2020-08-10 00:00:00", DateFormatUtils.format(param.getDate4(), //
                                                                          "yyyy-MM-dd HH:mm:ss"));

        Assert.assertEquals(2, param.getListObj().size());
        Assert.assertEquals("1: param1 str", param.getListObj().get(0).getStr());
        Assert.assertEquals("1: param2 str", param.getListObj().get(0).getObj2().getStr());

        Assert.assertEquals("2: param1 str", param.getListObj().get(1).getStr());
        Assert.assertEquals("2: param2 str", param.getListObj().get(1).getObj2().getStr());

        Assert.assertEquals("param1 str", param.getObj().getStr());
        Assert.assertEquals("param2 str", param.getObj().getObj2().getStr());
    }

    @Test
    public void testLoad023() {
        testLoadError("023.xml", Messages.parseSourceError("mysql"), //
                      Messages.parseCommonAttrUnknown("Source", "U,V"));
    }

    @Test
    public void testLoad024() {
        testLoadError("024.xml", Messages.parseCommonAttrUnknown("Param", "U"));
    }

    @Test
    public void testLoad025() {
        testLoadError("025.xml", Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCommonAttrUnknown("Init", "U"));
    }

    @Test
    public void testLoad026() {
        testLoadError("026.xml", Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseCommonAttrUnknown("Verify", "U"));
    }

    @Test
    public void testLoad027() {
        testLoadError("027.xml", Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationMatch("java.sql.Connection", "Init", "Table"));
    }

    @Test
    public void testLoad028() {
        testLoadError("028.xml", Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationMatch("org.springframework.data.mongodb.core.MongoOperations", "Init", "Collection"));
    }

    @Test
    public void testLoad029() {
        testLoadError("029.xml", Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseSourceOperationMatch("java.sql.Connection", "Verify", "Table"));
    }

    @Test
    public void testLoad030() {
        testLoadError("030.xml", Messages.parseSourceError("mongo"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseSourceOperationMatch("org.springframework.data.mongodb.core.MongoOperations", "Verify", "Collection"));
    }

    @Test
    public void testLoad031() {
        testLoadError("031.xml", Messages.parseSourceError("unknown"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationUnknown("java.lang.Object"));
    }

    @Test
    public void testLoad032() {
        testLoadError("032.xml", Messages.parseSourceError("none"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationNone());
    }

    @Test
    public void testLoad033() {
        logger.info("Normal data");
        ZestData zestData = load("033.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertTrue(obj.isIgnoreCheck());
        Assert.assertTrue(obj.isOnlyCheckCoreData());
    }

    @Test
    public void testLoad034() {
        logger.info("Normal data");
        ZestData zestData = load("034.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceInitData obj = zestData.getSourceList().get(0).getInitData();
        Assert.assertEquals(1, obj.getInitDataList().size());
        Assert.assertEquals("tab", obj.getInitDataList().get(0).getName());
        Assert.assertFalse(obj.getInitDataList().get(0).isIgnoreVerify());
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
        Connection conn = new MockConnection();
        MongoOperations mongoOperations = new MockMongoOperations();

        Map<String, Object> sourceOperations = (Map<String, Object>) ZestReflectHelper.getValue(worker, "sourceOperations");
        sourceOperations.put("mysql", conn);
        sourceOperations.put("mongo", mongoOperations);
        sourceOperations.put("unknown", new Object());

        String filePath = ZestDataTest.class.getResource(filename).getPath();
        ZestData zestData = new ZestData(filePath);
        Param param = new Param();
        zestData.setParam(param);
        param.setZestData(zestData);
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

    public static class Param extends ZestParam {

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

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public Integer getIntObjValue() {
            return intObjValue;
        }

        public void setIntObjValue(Integer intObjValue) {
            this.intObjValue = intObjValue;
        }

        public long getLongValue() {
            return longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public Long getLongObjValue() {
            return longObjValue;
        }

        public void setLongObjValue(Long longObjValue) {
            this.longObjValue = longObjValue;
        }

        public boolean isBoolValue() {
            return boolValue;
        }

        public void setBoolValue(boolean boolValue) {
            this.boolValue = boolValue;
        }

        public Boolean getBoolObjValue() {
            return boolObjValue;
        }

        public void setBoolObjValue(Boolean boolObjValue) {
            this.boolObjValue = boolObjValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public Double getDoubleObjValue() {
            return doubleObjValue;
        }

        public void setDoubleObjValue(Double doubleObjValue) {
            this.doubleObjValue = doubleObjValue;
        }

        public float getFloatValue() {
            return floatValue;
        }

        public void setFloatValue(float floatValue) {
            this.floatValue = floatValue;
        }

        public Float getFloatObjValue() {
            return floatObjValue;
        }

        public void setFloatObjValue(Float floatObjValue) {
            this.floatObjValue = floatObjValue;
        }

        public String getStrValue() {
            return strValue;
        }

        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }

        public Date getDate1() {
            return date1;
        }

        public void setDate1(Date date1) {
            this.date1 = date1;
        }

        public Date getDate2() {
            return date2;
        }

        public void setDate2(Date date2) {
            this.date2 = date2;
        }

        public Date getDate3() {
            return date3;
        }

        public void setDate3(Date date3) {
            this.date3 = date3;
        }

        public Date getDate4() {
            return date4;
        }

        public void setDate4(Date date4) {
            this.date4 = date4;
        }

        public List<Param1> getListObj() {
            return listObj;
        }

        public void setListObj(List<Param1> listObj) {
            this.listObj = listObj;
        }

        public Param1 getObj() {
            return obj;
        }

        public void setObj(Param1 obj) {
            this.obj = obj;
        }

        public Map<String, String> getNonsupportMapObj() {
            return nonsupportMapObj;
        }

        public void setNonsupportMapObj(Map<String, String> nonsupportMapObj) {
            this.nonsupportMapObj = nonsupportMapObj;
        }
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
