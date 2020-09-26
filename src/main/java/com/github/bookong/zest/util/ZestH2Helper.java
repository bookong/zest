/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.util;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

/**
 * Processing function for H2 database.
 *
 * @author Jiang Xu
 */
public class ZestH2Helper {

    /**
     * Since the H2 database will not automatically restore the auto-increment sequence after truncate is executed, it
     * needs to be reset manually.
     * 
     * @param dataSource
     *          {@code DataSource} of H2 database.
     */
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
