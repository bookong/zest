package com.github.bookong.zest.util;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class ZestH2Helper {

    public static void restartTableSequence(DataSource dataSource) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            String sql1 = "select sequence_name from information_schema.sequences where current_value > 0";
            for (Map<String, Object> data : ZestSqlHelper.find(conn, sql1)) {
                String sql2 = String.format("alter sequence %s restart with 1", data.get("sequence_name"));
                Statement stat = null;
                try {
                    stat = conn.createStatement();
                    stat.execute(sql2);
                } finally {
                    ZestSqlHelper.close(stat);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
