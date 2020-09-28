package com.github.bookong.zest.runner;

import com.github.bookong.zest.runner.sql.def.DefaultSqlZestWorkerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Jiang Xu
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ DefaultSqlZestWorkerTest.class, //
})
public class ZestWorkerTestSuite {
}
