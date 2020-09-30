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
public class ZestWorkerTest extends AbstractSqlZestWorkerTest {

    @ZestSource("mysql")
    private MockDataSource mockDataSource = new MockDataSource();

    @Override
    protected void before() {
        mockDataSource.setConnection(conn);
    }

    @Before
    public void setup() throws Exception {
        logger.info("setup()");
        createTable(conn, "tab1.sql");
    }

    /** MySQL 数据类型 - 数字 */
    @Test
    public void test01() {
        run("01.xml", Param.class, param -> {
            System.out.println(ZestSqlHelper.query(conn, "select * from tab1"));
        });
    }

    /** MySQL 数据类型 - 日期 */
    @Test
    public void test02() {
        run("02.xml", Param.class, param -> {
            System.out.println(ZestSqlHelper.query(conn, "select * from tab1"));
        });
    }

    /** MySQL 数据类型 - 字符串 */
    @Test
    public void test03() {
        run("03.xml", Param.class, param -> {
            System.out.println(ZestSqlHelper.query(conn, "select * from tab1"));
        });
    }

    /** MySQL 数据类型 - 二进制内容 */
    @Test
    public void test04() {
        run("04.xml", Param.class, param -> {
            System.out.println(ZestSqlHelper.query(conn, "select * from tab1"));
        });
    }

    /** MySQL 数据类型 - json 类型 */
    @Test
    public void test05() {
        run("05.xml", Param.class, param -> {
            System.out.println(ZestSqlHelper.query(conn, "select * from tab1"));
        });
    }

    public static class Param extends ZestParam {

    }
}
