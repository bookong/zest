package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.runner.junit5.ZestJUnit5Worker;
import com.github.bookong.zest.support.rule.CurrentTimeRule;
import com.github.bookong.zest.support.rule.FromCurrentTimeRule;
import com.github.bookong.zest.support.rule.RegExpRule;
import com.github.bookong.zest.testcase.mock.MockConnection;
import com.github.bookong.zest.testcase.mock.MockMongoOperations;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.param.Param;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        testLoadError("008.xml", Messages.parseSourcesError(), //
                      Messages.parseCommonChildrenList("Sources", "Source"));
    }

    @Test
    public void testLoad009() {
        testLoadError("009.xml", Messages.parseSourcesError(), //
                      Messages.parseCommonAttrUnknown("Sources", "U"));
    }

    @Test
    public void testLoad010() {
        testLoadError("010.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), Messages.parseSourceNecessary());
    }

    @Test
    public void testLoad011() {
        testLoadError("011.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), Messages.parseSourceNecessary());
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
        testLoadError("013.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError(""), //
                      Messages.parseCommonAttrNeed("Source", "Id"));
    }

    @Test
    public void testLoad014() {
        testLoadError("014.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseCommonAttrDuplicate("Id", "mysql"));
    }

    @Test
    public void testLoad015() {
        testLoadError("015.xml", Messages.parseParamError(), //
                      Messages.parseCommonChildrenList("Param", "ParamField"));
    }

    @Test
    public void testLoad016() {
        testLoadError("016.xml", Messages.parseParamError(), //
                      Messages.parseCommonAttrNeed("ParamField", "Name"));
    }

    @Test
    public void testLoad017() {
        testLoadError("017.xml", Messages.parseParamError(), //
                      Messages.parseCommonAttrDuplicate("Name", "strValue"));
    }

    @Test
    public void testLoad018() {
        testLoadError("018.xml", Messages.parseParamError(), //
                      Messages.parseParamNone("none"));
    }

    @Test
    public void testLoad019() {
        testLoadError("019.xml", Messages.parseParamError(), //
                      Messages.parseParamObjLoad("intValue"), //
                      "For input string: \"str value\"");
    }

    @Test
    public void testLoad020() {
        testLoadError("020.xml", Messages.parseParamError(), //
                      Messages.parseParamObjLoad("date1"), //
                      Messages.parseDate("str value"));
    }

    @Test
    public void testLoad021() {
        testLoadError("021.xml", Messages.parseParamError(), //
                      Messages.parseParamObjLoad("nonsupportMapObj"), //
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
        testLoadError("023.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseCommonAttrUnknown("Source", "U,V"));
    }

    @Test
    public void testLoad024() {
        testLoadError("024.xml", Messages.parseParamError(), //
                      Messages.parseCommonAttrUnknown("Param", "U"));
    }

    @Test
    public void testLoad025() {
        testLoadError("025.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCommonAttrUnknown("Init", "U"));
    }

    @Test
    public void testLoad026() {
        testLoadError("026.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseCommonAttrUnknown("Verify", "U"));
    }

    @Test
    public void testLoad027() {
        testLoadError("027.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationMatch("java.sql.Connection", "Init", "Table"));
    }

    @Test
    public void testLoad028() {
        testLoadError("028.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationMatch("org.springframework.data.mongodb.core.MongoOperations",
                                                         "Init", "Collection"));
    }

    @Test
    public void testLoad029() {
        testLoadError("029.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseSourceOperationMatch("java.sql.Connection", "Verify", "Table"));
    }

    @Test
    public void testLoad030() {
        testLoadError("030.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseSourceOperationMatch("org.springframework.data.mongodb.core.MongoOperations",
                                                         "Verify", "Collection"));
    }

    @Test
    public void testLoad031() {
        testLoadError("031.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("unknown"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationUnknown("java.lang.Object"));
    }

    @Test
    public void testLoad032() {
        testLoadError("032.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("none"), //
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

    @Test
    public void testLoad035() {
        testLoadError("035.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonAttrUnknown("Table", "U"));
    }

    @Test
    public void testLoad036() {
        testLoadError("036.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseCommonAttrNeed("Collection", "EntityClass"));
    }

    @Test
    public void testLoad037() {
        testLoadError("037.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseCommonClassFound("none"));
    }

    @Test
    public void testLoad038() {
        testLoadError("038.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseCommonAttrUnknown("Collection", "U"));
    }

    @Test
    public void testLoad039() {
        testLoadError("039.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsPosition());
    }

    @Test
    public void testLoad040() {
        testLoadError("040.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonChildrenUnknown("Table", "Other"));
    }

    @Test
    public void testLoad041() {
        testLoadError("041.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonChildrenUnknown("Table", "Other"));
    }

    @Test
    public void testLoad042() {
        testLoadError("042.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseCommonChildrenList("Sorts", "Sort"));
    }

    @Test
    public void testLoad043() {
        testLoadError("043.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseCommonAttrUnknown("Sorts", "U"));
    }

    @Test
    public void testLoad044() {
        testLoadError("044.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseSortError(""), //
                      Messages.parseCommonAttrNeed("Sort", "Field"));
    }

    @Test
    public void testLoad045() {
        testLoadError("045.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseSortError("f_varchar"), //
                      Messages.parseSortDirection());
    }

    @Test
    public void testLoad046() {
        testLoadError("046.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseSortError("f_varchar"), //
                      Messages.parseCommonChildren("Sort"));
    }

    @Test
    public void testLoad047() {
        testLoadError("047.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseSortError("f_varchar"), //
                      Messages.parseCommonAttrDuplicate("Field", "f_varchar"));
    }

    @Test
    public void testLoad048() {
        testLoadError("048.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseTableSortExist("none"));
    }

    @Test
    public void testLoad049() {
        logger.info("Normal data");
        ZestData zestData = load("049.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getVerifyDataMap().size());
        Assert.assertTrue(obj.getVerifyDataMap().get("tab") instanceof Table);
        Table table = (Table) obj.getVerifyDataMap().get("tab");
        Assert.assertEquals(" order by f_varchar desc, f_double asc, f_bigint asc", table.getSort());
    }

    @Test
    public void testLoad050() {
        testLoadError("050.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseSortsPosition());
    }

    @Test
    public void testLoad051() {
        testLoadError("051.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsPosition());
    }

    @Test
    public void testLoad052() {
        testLoadError("052.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseSortsPosition());
    }

    @Test
    public void testLoad053() {
        logger.info("Normal data");
        ZestData zestData = load("053.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        AbstractTable obj = zestData.getSourceList().get(0).getVerifyData().getVerifyDataMap().get("tab");
        Assert.assertTrue(obj instanceof Collection);
        Collection tab = (Collection) obj;
        Assert.assertEquals("intObjValue: DESC,longObjValue: ASC,doubleObjValue: ASC", tab.getSort().toString());
    }

    @Test
    public void testLoad054() {
        testLoadError("054.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseCollectionSortExits("none"));
    }

    @Test
    public void testLoad055() {
        logger.info("Normal data");
        ZestData zestData = load("055.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getVerifyDataMap().size());
        Assert.assertTrue(obj.getVerifyDataMap().get("tab") instanceof Table);
        Table table = (Table) obj.getVerifyDataMap().get("tab");
        Assert.assertEquals(3, table.getRuleList().size());

        RegExpRule regExpRule = (RegExpRule) table.getRuleList().get(0);
        Assert.assertEquals("f_varchar", regExpRule.getPath());
        Assert.assertEquals("^[0-9]*$", regExpRule.getRegExp());

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleList().get(1);
        Assert.assertEquals("f_time", currentTimeRule.getPath());
        Assert.assertEquals(1000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleList().get(2);
        Assert.assertEquals("f_date", fromCurrentTimeRule.getPath());
        Assert.assertEquals(1000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.DAY_OF_YEAR, fromCurrentTimeRule.getUnit());
    }

    @Test
    public void testLoad056() {
        logger.info("Normal data");
        ZestData zestData = load("056.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getVerifyDataMap().size());
        Collection table = (Collection) obj.getVerifyDataMap().get("tab");
        Assert.assertEquals(3, table.getRuleList().size());

        RegExpRule regExpRule = (RegExpRule) table.getRuleList().get(0);
        Assert.assertEquals("obj.str", regExpRule.getPath());
        Assert.assertEquals("^[0-9]*$", regExpRule.getRegExp());

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleList().get(1);
        Assert.assertEquals("date1", currentTimeRule.getPath());
        Assert.assertEquals(1000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleList().get(2);
        Assert.assertEquals("date2", fromCurrentTimeRule.getPath());
        Assert.assertEquals(1000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.DAY_OF_YEAR, fromCurrentTimeRule.getUnit());
    }

    @Test
    public void testLoad057() {
        logger.info("Normal data");
        ZestData zestData = load("057.xml");
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Table table = (Table) obj.getVerifyDataMap().get("tab");

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleList().get(0);
        Assert.assertEquals("f_time", currentTimeRule.getPath());
        Assert.assertEquals(2000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleList().get(1);
        Assert.assertEquals("f_date", fromCurrentTimeRule.getPath());
        Assert.assertEquals(2000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.HOUR_OF_DAY, fromCurrentTimeRule.getUnit());
    }

    private void testLoadError(String filename, String... errorMessages) {
        try {
            load(filename);
            Assert.fail("Should raise an exception");
        } catch (ZestException e) {
            logger.info(e.getMessage());
            Assert.assertEquals(getExpectMessage(filename, errorMessages), e.getMessage());
        }
    }

    private ZestData load(String filename) {
        Connection conn = new MockConnection();
        MongoOperations mongoOperations = new MockMongoOperations();

        Map<String, Object> sourceOperations = (Map<String, Object>) ZestReflectHelper.getValue(worker,
                                                                                                "sourceOperations");
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

}
