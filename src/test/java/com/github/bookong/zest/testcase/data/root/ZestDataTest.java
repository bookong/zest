package com.github.bookong.zest.testcase.data.root;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    @Override
    protected void initZestData(String filename, ZestData zestData) {
    }

    @Test
    public void testLoad01() {
        testLoadError("01.xml", Messages.parseZest());
    }

    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseZestNecessary());
    }

    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseZestNecessary());
    }

    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseZestNecessary());
    }

    @Test
    public void testLoad05() {
        logger.info("Normal data");
        ZestData zestData = load("05.xml");
        Assert.assertFalse(zestData.isTransferTime());
        Assert.assertEquals(0L, zestData.getCurrentTimeDiff());
        Assert.assertEquals("test case description", zestData.getDescription());
    }

    @Test
    public void testLoad06() {
        logger.info("Normal data");
        ZestData zestData = load("06.xml");
        Assert.assertEquals(3.4F, zestData.getVersion(), 0.1F);
        Assert.assertTrue(zestData.isTransferTime());
        Assert.assertTrue(zestData.getCurrentTimeDiff() != 0L);
    }

    @Test
    public void testLoad07() {
        testLoadError("07.xml", Messages.parseCommonAttrUnknown("Zest", "U"));
    }
}
