package com.github.bookong.zest.testcase;

import org.junit.Test;

/**
 * @author Jiang Xu
 */
public class ZestDataTest {

    @Test
    public void testLoad() {
        System.out.println(ZestDataTest.class.getResource("001.xml").getPath());

    }
}
