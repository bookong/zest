package com.github.bookong.zest.testcase.data.sorts;

import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.util.Messages;
import org.junit.Test;

/**
 * 测试元素 Zest/Sources/Source/Verify/Sorts
 *
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    /** 不支持的子元素 */
    @Test
    public void testLoad01() {
        testLoadError("01.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseCommonChildrenList("Sorts", "Sort"));
    }

    /** 不支持的属性 */
    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseCommonAttrUnknown("Sorts", "U"));
    }

    /** mysql 必须在 Verify/Table 元素下 */
    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsPosition());
    }

    /** mongo 必须在 Verify/Table 元素下 */
    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsPosition());
    }
}
