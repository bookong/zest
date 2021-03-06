package com.github.bookong.zest.testcase.data.data;

import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.mongo.Document;
import com.github.bookong.zest.testcase.param.Param;
import com.github.bookong.zest.testcase.sql.Row;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * 测试元素 Zest/Sources/Source/Init|Verify/Table/Data
 *
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    /** mysql 类型 Init 下，正常数据 */
    @Test
    public void testLoad01() {
        ZestData zestData = load("01.xml");
        AbstractTable<?> abstractTable = zestData.getSourceList().get(0).getInitData().getTableList().get(0);
        Assert.assertTrue(abstractTable instanceof Table);
        Table table = (Table) abstractTable;
        Assert.assertEquals(1, table.getDataList().size());

        Row row = table.getDataList().get(0);
        Assert.assertEquals(5, row.getDataMap().size());

        Assert.assertEquals(12, ((Integer) row.getDataMap().get("f_integer")).intValue());
        Assert.assertEquals("str", String.valueOf(row.getDataMap().get("f_varchar")));
        Assert.assertEquals("2020-09-28T11:12:13.000+0800",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_date"))));
        Assert.assertEquals("2020-09-29T11:12:13.000+0800",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_time"))));
        Assert.assertEquals("2020-09-30T11:12:13.000+0800",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_timestamp"))));
    }

    /** mongo 类型 Init 下，正常数据 */
    @Test
    public void testLoad02() {
        ZestData zestData = load("02.xml");
        AbstractTable<?> abstractTable = zestData.getSourceList().get(0).getInitData().getTableList().get(0);
        Assert.assertTrue(abstractTable instanceof Collection);
        Collection collection = (Collection) abstractTable;
        Assert.assertEquals(1, collection.getDataList().size());

        Document document = collection.getDataList().get(0);
        Assert.assertTrue(document.getData() instanceof Param);
        Param data = (Param) document.getData();

        Assert.assertEquals(12, data.getIntValue());
        Assert.assertEquals("str", data.getStrValue());
        Assert.assertEquals("2020-09-28T11:12:13.000+0800", ZestDateUtil.formatDateNormal(data.getDate1()));
    }

    /** mysql 类型 Init 下，库表中没有这个字段 */
    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseDataError(1), //
                      Messages.parseDataTableRowExist("none", "tab"));

    }

    /** mongo 类型 Init 下，实体上没有这个字段，但 JSON 转换对象时不影响 */
    @Test
    public void testLoad04() {
        ZestData zestData = load("04.xml");
        AbstractTable<?> abstractTable = zestData.getSourceList().get(0).getInitData().getTableList().get(0);
        Assert.assertTrue(abstractTable instanceof Collection);
        Collection collection = (Collection) abstractTable;
        Assert.assertEquals(1, collection.getDataList().size());

        Document document = collection.getDataList().get(0);
        Assert.assertTrue(document.getData() instanceof Param);
        Param data = (Param) document.getData();

        Assert.assertEquals(12, data.getIntValue());
        Assert.assertEquals("str", data.getStrValue());
        Assert.assertEquals("2020-09-28T11:12:13.000+0800", ZestDateUtil.formatDateNormal(data.getDate1()));
    }

    /** 不支持的属性 */
    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseDataError(1), //
                      Messages.parseCommonAttrUnknown("Data", "U"));

    }

    /** 不支持的子元素 */
    @Test
    public void testLoad06() {
        testLoadError("06.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseDataError(1), //
                      Messages.parseCommonChildren("Data"));

    }

    /** mysql 类型 Verify 下，正常数据 */
    @Test
    public void testLoad07() {
        ZestData zestData = load("07.xml");
        AbstractTable<?> abstractTable = zestData.getSourceList().get(0).getVerifyData().getTableMap().get("tab");
        Assert.assertTrue(abstractTable instanceof Table);
        Table table = (Table) abstractTable;
        Assert.assertEquals(1, table.getDataList().size());

        Row row = table.getDataList().get(0);
        Assert.assertEquals(5, row.getDataMap().size());

        Assert.assertEquals(12, ((Integer) row.getDataMap().get("f_integer")).intValue());
        Assert.assertEquals("str", String.valueOf(row.getDataMap().get("f_varchar")));
        Assert.assertEquals("2020-09-28T11:12:13.000+0800",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_date"))));
        Assert.assertEquals("2020-09-29T11:12:13.000+0800",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_time"))));
        Assert.assertEquals("2020-09-30T11:12:13.000+0800",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_timestamp"))));
    }

    /** mongo 类型 Verify 下，正常数据 */
    @Test
    public void testLoad08() {
        ZestData zestData = load("08.xml");
        AbstractTable<?> abstractTable = zestData.getSourceList().get(0).getVerifyData().getTableMap().get("tab");
        Assert.assertTrue(abstractTable instanceof Collection);
        Collection collection = (Collection) abstractTable;
        Assert.assertEquals(1, collection.getDataList().size());

        Document document = collection.getDataList().get(0);
        Assert.assertTrue(document.getData() instanceof Param);
        Param data = (Param) document.getData();

        Assert.assertEquals(12, data.getIntValue());
        Assert.assertEquals("str", data.getStrValue());
        Assert.assertEquals("2020-09-28T11:12:13.000+0800", ZestDateUtil.formatDateNormal(data.getDate1()));
    }

    /** mongo 类型 Verify 下，正常数据，EntityClass 在 Init 下所以 Verify 下可以省略 */
    @Test
    public void testLoad09() {
        ZestData zestData = load("09.xml");
        AbstractTable<?> abstractTable = zestData.getSourceList().get(0).getVerifyData().getTableMap().get("tab");
        Assert.assertTrue(abstractTable instanceof Collection);
        Collection collection = (Collection) abstractTable;
        Assert.assertEquals(1, collection.getDataList().size());

        Document document = collection.getDataList().get(0);
        Assert.assertTrue(document.getData() instanceof Param);
        Param data = (Param) document.getData();

        Assert.assertEquals(12, data.getIntValue());
        Assert.assertEquals("str", data.getStrValue());
        Assert.assertEquals("2020-09-28T11:12:13.000+0800", ZestDateUtil.formatDateNormal(data.getDate1()));
    }

    /** Date 与 Rules 相对位置不对 */
    @Test
    public void testLoad10() {
        testLoadError("10.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesOrder());

    }
}
