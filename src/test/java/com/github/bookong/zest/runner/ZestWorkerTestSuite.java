package com.github.bookong.zest.runner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Jiang Xu
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ com.github.bookong.zest.runner.sql.ZestWorkerTest.class, //
})
public class ZestWorkerTestSuite {
}
