package com.github.bookong.zest.testcase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Jiang Xu
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ com.github.bookong.zest.testcase.data.collection.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.param.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.root.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.rule.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.rules.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.sort.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.sorts.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.source.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.sources.ZestDataTest.class, //
                      com.github.bookong.zest.testcase.data.table.ZestDataTest.class, //

})
public class ZestDataTestSuite {
}
