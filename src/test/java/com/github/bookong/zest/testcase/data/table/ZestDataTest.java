package com.github.bookong.zest.testcase.data.table;

import com.github.bookong.zest.testcase.SourceInitData;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
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
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationMatch("org.springframework.data.mongodb.core.MongoOperations",
                                                         "Init", "Collection"));
    }

    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseSourceOperationMatch("org.springframework.data.mongodb.core.MongoOperations",
                                                         "Verify", "Collection"));
    }

    @Test
    public void testLoad03() {
        logger.info("Normal data");
        ZestData zestData = load("03.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceInitData obj = zestData.getSourceList().get(0).getInitData();
        Assert.assertEquals(1, obj.getInitDataList().size());
        Assert.assertEquals("tab", obj.getInitDataList().get(0).getName());
        Assert.assertFalse(obj.getInitDataList().get(0).isIgnoreVerify());
    }

    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonAttrUnknown("Table", "U"));
    }

    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonChildrenUnknown("Table", "Other"));
    }

    @Test
    public void testLoad06() {
        testLoadError("06.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseCommonChildrenUnknown("Table", "Other"));
    }
}
