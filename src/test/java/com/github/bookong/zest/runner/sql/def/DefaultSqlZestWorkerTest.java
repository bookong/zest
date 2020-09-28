package com.github.bookong.zest.runner.sql.def;

import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.mock.MockDataSource;
import com.github.bookong.zest.runner.sql.AbstractSqlZestWorkerTest;
import com.github.bookong.zest.testcase.ZestParam;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jiang Xu
 */
public class DefaultSqlZestWorkerTest extends AbstractSqlZestWorkerTest {

    @ZestSource("mysql")
    private MockDataSource mockDataSource = new MockDataSource();

    @Before
    public void setup() throws Exception {
        logger.info("setup()");
        createTable(conn, "tab1.sql");
    }

    @Test
    public void test01() {
        run("01.xml", mockDataSource, Param.class, param -> {
            System.out.println(ZestSqlHelper.query(conn, "select * from tab1"));
        });
    }

    public static class Param extends ZestParam {

    }
}
