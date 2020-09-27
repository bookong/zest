package com.github.bookong.zest.testcase.data.root;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试根元素 Zest
 * 
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    @Override
    protected void initZestData(String filename, ZestData zestData) {
    }

    /** 不是正确的 XML 格式 */
    @Test
    public void testLoad00() {
        try {
            load("00.xml");
            Assert.fail("Should raise an exception");
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /** 根元素必须是 Zest */
    @Test
    public void testLoad01() {
        testLoadError("01.xml", Messages.parseZest());
    }

    /** Zest 下没有子元素 */
    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseZestNecessary());
    }

    /** Zest 子元素不符合固定的顺序 */
    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseZestNecessary());
    }

    /** Zest 下多了不支持的子元素 */
    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseZestNecessary());
    }

    /** 最简单的正确例子，检查属性默认值 */
    @Test
    public void testLoad05() {
        logger.info("Normal data");
        ZestData zestData = load("05.xml");
        Assert.assertFalse(zestData.isTransferTime());
        Assert.assertEquals(1.0F, zestData.getVersion(), 0.01F);
        Assert.assertEquals(0L, zestData.getCurrentTimeDiff());
        Assert.assertEquals("test case description", zestData.getDescription());
    }

    /** 最简单的正确例子，不使用默认属性值 */
    @Test
    public void testLoad06() {
        logger.info("Normal data");
        ZestData zestData = load("06.xml");
        Assert.assertEquals(3.4F, zestData.getVersion(), 0.01F);
        Assert.assertTrue(zestData.isTransferTime());
        Assert.assertTrue(zestData.getCurrentTimeDiff() != 0L);
    }

    /** Zest 上有了不识别的属性 */
    @Test
    public void testLoad07() {
        testLoadError("07.xml", Messages.parseCommonAttrUnknown("Zest", "U"));
    }
}
