package com.github.bookong.zest.testcase.data.source;

import com.github.bookong.zest.testcase.SourceVerifyData;
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
                      Messages.parseSourceError("mysql"), Messages.parseSourceNecessary());
    }

    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), Messages.parseSourceNecessary());
    }

    @Test
    public void testLoad03() {
        logger.info("Normal data");
        ZestData zestData = load("03.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        Assert.assertEquals("mysql", zestData.getSourceList().get(0).getId());
    }

    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError(""), //
                      Messages.parseCommonAttrNeed("Source", "Id"));
    }

    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseCommonAttrDuplicate("Id", "mysql"));
    }

    @Test
    public void testLoad06() {
        testLoadError("06.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseCommonAttrUnknown("Source", "U,V"));
    }

    @Test
    public void testLoad07() {
        testLoadError("07.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCommonAttrUnknown("Init", "U"));
    }

    @Test
    public void testLoad08() {
        testLoadError("08.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseCommonAttrUnknown("Verify", "U"));
    }

    @Test
    public void testLoad09() {
        testLoadError("09.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("unknown"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationUnknown("java.lang.Object"));
    }

    @Test
    public void testLoad10() {
        testLoadError("10.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("none"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationNone());
    }

    @Test
    public void testLoad11() {
        logger.info("Normal data");
        ZestData zestData = load("11.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertTrue(obj.isIgnoreCheck());
        Assert.assertTrue(obj.isOnlyCheckCoreData());
    }
}
