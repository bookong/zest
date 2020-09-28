package com.github.bookong.zest.testcase.data.source;

import com.github.bookong.zest.testcase.SourceVerifyData;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试元素 Zest/Sources/Source[/Init|Verify]
 * 
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    /** 不支持的子元素 */
    @Test
    public void testLoad01() {
        testLoadError("01.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceNecessary());
    }

    /** 子元素顺序不正确 */
    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), Messages.parseSourceNecessary());
    }

    /** 一个正确的例子 */
    @Test
    public void testLoad03() {
        ZestData zestData = load("03.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        Assert.assertEquals("mysql", zestData.getSourceList().get(0).getId());
    }

    /** 缺少 Id 属性 */
    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError(""), //
                      Messages.parseCommonAttrEmpty("Id"));
    }

    /** Id 属性重复 */
    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseCommonAttrDuplicate("Id", "mysql"));
    }

    /** 不支持的属性 */
    @Test
    public void testLoad06() {
        testLoadError("06.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseCommonAttrUnknown("Source", "U,V"));
    }

    /** 元素 Zest/Sources/Source/Init 不支持的属性 */
    @Test
    public void testLoad07() {
        testLoadError("07.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCommonAttrUnknown("Init", "U"));
    }

    /** 元素 Zest/Sources/Source/Verify 不支持的属性 */
    @Test
    public void testLoad08() {
        testLoadError("08.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseCommonAttrUnknown("Verify", "U"));
    }

    /** 不支持的的操作器 */
    @Test
    public void testLoad09() {
        testLoadError("09.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("unknown"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationUnknown("java.lang.Object"));
    }

    /** 没有绑定操作器 */
    @Test
    public void testLoad10() {
        testLoadError("10.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("none"), //
                      Messages.parseSourceInitError(), //
                      Messages.operatorUnbound("none"));
    }

    /** 正常数据 */
    @Test
    public void testLoad11() {
        ZestData zestData = load("11.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertTrue(obj.isIgnoreVerify());
    }

    /** 正常数据，取属性默认值 */
    @Test
    public void testLoad12() {
        ZestData zestData = load("12.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertFalse(obj.isIgnoreVerify());
    }
}
