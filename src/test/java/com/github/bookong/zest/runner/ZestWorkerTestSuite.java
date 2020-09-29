package com.github.bookong.zest.runner;

import com.github.bookong.zest.runner.sql.def.ZestWorkerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Jiang Xu
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ com.github.bookong.zest.runner.sql.def.ZestWorkerTest.class, //
                      com.github.bookong.zest.runner.mongo.def.ZestWorkerTest.class, //
})
public class ZestWorkerTestSuite {
}
