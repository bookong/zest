package com.github.bookong.zest.testcase.data.sorts;

import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.util.Messages;
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
                      Messages.parseCommonChildrenList("Sorts", "Sort"));
    }

    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsError(), //
                      Messages.parseCommonAttrUnknown("Sorts", "U"));
    }

    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseSortsPosition());
    }

    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseSortsPosition());
    }
}
