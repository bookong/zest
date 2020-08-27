package com.github.bookong.zest.testcase.data.sort;

import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.SourceVerifyData;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    @Test
    public void testLoad01() {
        testLoadError("01.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseSortError(""), //
                      Messages.parseCommonAttrEmpty("Field"));
    }

    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseSortError("f_varchar"), //
                      Messages.parseSortDirection());
    }

    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseSortError("f_varchar"), //
                      Messages.parseCommonChildren("Sort"));
    }

    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseSortError("f_varchar"), //
                      Messages.parseCommonAttrDuplicate("Field", "f_varchar"));
    }

    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseTableSortExist("none"));
    }

    @Test
    public void testLoad06() {
        logger.info("Normal data");
        ZestData zestData = load("06.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());
        Assert.assertTrue(obj.getTableMap().get("tab") instanceof Table);
        Table table = (Table) obj.getTableMap().get("tab");
        Assert.assertEquals(" order by f_varchar desc, f_double asc, f_bigint asc", table.getSort());
    }

    @Test
    public void testLoad07() {
        logger.info("Normal data");
        ZestData zestData = load("07.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        AbstractTable obj = zestData.getSourceList().get(0).getVerifyData().getTableMap().get("tab");
        Assert.assertTrue(obj instanceof Collection);
        Collection tab = (Collection) obj;
        Assert.assertEquals("intObjValue: DESC,longObjValue: ASC,doubleObjValue: ASC", tab.getSort().toString());
    }

    @Test
    public void testLoad08() {
        testLoadError("08.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseCollectionSortExits("none"));
    }
}
