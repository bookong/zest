package com.github.bookong.zest.runner.sql;

import com.github.bookong.zest.mock.MockDataSource;
import com.github.bookong.zest.runner.junit5.ZestJUnit5Worker;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.ZestParam;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Jiang Xu
 */
public abstract class AbstractSqlZestWorkerTest extends ZestJUnit5Worker {

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
        logger.info(sql);
        ZestSqlHelper.execute(conn, sql);
    }

    protected <T extends ZestParam> ZestData run(String filename, MockDataSource mockDataSource,
                                                 Class<T> zestParamClass, Consumer<T> fun) {
        mockDataSource.setConnection(conn);
        loadAnnotation(this);
        String filePath = getClass().getResource(filename).getPath();
        ZestData zestData = new ZestData(filePath);

        T param = before(zestData, zestParamClass);
        fun.accept(param);
        after(zestData);
        return zestData;
    }
}
