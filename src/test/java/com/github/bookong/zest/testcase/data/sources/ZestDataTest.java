package com.github.bookong.zest.testcase.data.sources;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.util.Messages;
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
        testLoadError("01.xml", Messages.parseSourcesError(), //
                      Messages.parseCommonChildrenList("Sources", "Source"));
    }

    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseCommonAttrUnknown("Sources", "U"));
    }
}
