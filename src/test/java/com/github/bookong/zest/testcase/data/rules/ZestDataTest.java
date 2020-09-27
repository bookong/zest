package com.github.bookong.zest.testcase.data.rules;

import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.util.Messages;
import org.junit.Test;

/**
 * 测试元素 Zest/Sources/Source/Verify/Rules
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
                      Messages.parseRulesError(), //
                      Messages.parseCommonChildrenList("Rules", "Rule"));
    }

    /** 不支持的属性 */
    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseCommonAttrUnknown("Rules", "U"));
    }

    /** mysql 类型，必须在 Verify/Table 元素下 */
    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesPosition());
    }

    /** mongo 必须在 Verify/Table 元素下 */
    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesPosition());
    }
}
