package com.github.bookong.zest.testcase.data.table;

import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.SourceInitData;
import com.github.bookong.zest.testcase.SourceVerifyData;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试元素 Zest/Sources/Source/Init|Verify/Table
 * 
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    /** 绑定 MongoDB 缺少 EntityClass 属性 */
    @Test
    public void testLoad01() {
        testLoadError("01.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("user"), //
                      Messages.parseCommonAttrEmpty("EntityClass"));
    }

    /** mongo 的 Init 部分正常数据 */
    @Test
    public void testLoad02() {
        ZestData zestData = load("02.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceInitData obj = zestData.getSourceList().get(0).getInitData();
        Assert.assertEquals(1, obj.getTableList().size());

        AbstractTable<?> abstractTable = obj.getTableList().get(0);
        Assert.assertTrue(abstractTable instanceof Collection);
        Collection collection = (Collection) abstractTable;
        Assert.assertEquals("tab", collection.getName());
        Assert.assertEquals("com.github.bookong.zest.testcase.param.Param", collection.getEntityClass().getName());
        Assert.assertFalse(collection.isIgnoreVerify());
    }

    /** mysql 的 Init 部分正常数据 */
    @Test
    public void testLoad03() {
        ZestData zestData = load("03.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceInitData obj = zestData.getSourceList().get(0).getInitData();
        Assert.assertEquals(1, obj.getTableList().size());

        AbstractTable<?> abstractTable = obj.getTableList().get(0);
        Assert.assertTrue(abstractTable instanceof Table);
        Table table = (Table) abstractTable;
        Assert.assertEquals("tab", table.getName());
        Assert.assertFalse(table.isIgnoreVerify());
    }

    /** Init 部分不支持的属性 */
    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonAttrUnknown("Table", "U"));
    }

    /** Init 下 Table 不支持的子元素 */
    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonChildrenUnknown("Table", "Other"));
    }

    /** Verify 下 Table 不支持的子元素 */
    @Test
    public void testLoad06() {
        testLoadError("06.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonChildrenUnknown("Table", "Other"));
    }

    /** mongo 的 Verify 部分正常数据，属性默认值 */
    @Test
    public void testLoad07() {
        ZestData zestData = load("07.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());

        AbstractTable<?> abstractTable = obj.getTableMap().get("tab");
        Assert.assertTrue(abstractTable instanceof Collection);
        Collection collection = (Collection) abstractTable;
        Assert.assertEquals("tab", collection.getName());
        Assert.assertEquals("com.github.bookong.zest.testcase.param.Param", collection.getEntityClass().getName());
        Assert.assertFalse(collection.isIgnoreVerify());
    }

    /** mongo 的 Verify 部分正常数据，属性给定值 */
    @Test
    public void testLoad08() {
        ZestData zestData = load("08.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());

        AbstractTable<?> abstractTable = obj.getTableMap().get("tab");
        Assert.assertTrue(abstractTable instanceof Collection);
        Collection collection = (Collection) abstractTable;
        Assert.assertTrue(collection.isIgnoreVerify());
    }

    /** mysql 的 Verify 部分正常数据，属性默认值 */
    @Test
    public void testLoad09() {
        ZestData zestData = load("09.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());

        AbstractTable<?> abstractTable = obj.getTableMap().get("tab");
        Assert.assertTrue(abstractTable instanceof Table);
        Table table = (Table) abstractTable;
        Assert.assertEquals("tab", table.getName());
        Assert.assertFalse(table.isIgnoreVerify());
    }

    /** mysql 的 Verify 部分正常数据，属性给定值 */
    @Test
    public void testLoad10() {
        ZestData zestData = load("10.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());

        AbstractTable<?> abstractTable = obj.getTableMap().get("tab");
        Assert.assertTrue(abstractTable instanceof Table);
        Table table = (Table) abstractTable;
        Assert.assertEquals("tab", table.getName());
        Assert.assertTrue(table.isIgnoreVerify());
    }
}
