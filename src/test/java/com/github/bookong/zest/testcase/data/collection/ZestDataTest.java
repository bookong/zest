package com.github.bookong.zest.testcase.data.collection;

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
                      Messages.parseSourceInitError(), //
                      Messages.parseSourceOperationMatch("java.sql.Connection", "Init", "Table"));
    }

    @Test
    public void testLoad02() {
        testLoadError("02.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseSourceOperationMatch("java.sql.Connection", "Verify", "Table"));
    }

    @Test
    public void testLoad03() {
        testLoadError("03.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseCommonAttrNeed("Collection", "EntityClass"));
    }

    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseCommonClassFound("none"));
    }

    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mongo"), //
                      Messages.parseSourceInitError(), //
                      Messages.parseCollectionError("tab"), //
                      Messages.parseCommonAttrUnknown("Collection", "U"));
    }
}
