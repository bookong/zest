package com.github.bookong.zest.runner.sql;

import com.github.bookong.zest.runner.AbstractZestJUnit5WorkerTest;
import com.github.bookong.zest.testcase.ZestParam;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

/**
 * @author Jiang Xu
 */
public abstract class AbstractSqlZestWorkerTest extends AbstractZestJUnit5WorkerTest {

    protected static Connection conn = null;

    @BeforeClass
    public static void setupClass() {
        logger.info("setupClass()");
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:mem:zestdb;MODE=MYSQL;DB_CLOSE_DELAY=-1");

        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @AfterClass
    public static void tearDownClass() {
        logger.info("tearDownClass()");
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                logger.warn("{} : {}", e.getClass().getName(), e.getMessage());
            }
        }
    }

    protected void createTable(Connection conn, String filename) throws Exception {
        List<String> lines = IOUtils.readLines(getClass().getResource(filename).openStream(), StandardCharsets.UTF_8);
        String sql = StringUtils.join(lines, '\n');
        int pos = sql.indexOf('`');
        String str = sql.substring(pos + 1);
        pos = str.indexOf('`');
        str = String.format("DROP TABLE IF EXISTS `%s`", str.substring(0, pos));
        logger.info(str);
        ZestSqlHelper.execute(conn, str);
        logger.info(sql);
        ZestSqlHelper.execute(conn, sql);
    }

}
