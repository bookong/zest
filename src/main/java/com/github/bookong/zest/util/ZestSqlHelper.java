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

import com.github.bookong.zest.common.ZestGlobalConstant;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.testcase.sql.Row;
import com.github.bookong.zest.testcase.sql.Table;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.*;
import java.util.*;

/**
 * Auxiliary operation SQL.
 * 
 * @author Jiang Xu
 */
public class ZestSqlHelper {

    private static Logger logger = LoggerFactory.getLogger(ZestGlobalConstant.Logger.SQL);

    /**
     * Close {@link Statement}.
     *
     * @param stat
     *          a {@link Statement} object.
     */
    public static void close(Statement stat) {
        if (stat != null) {
            try {
                stat.close();
            } catch (Exception e) {
                logger.warn("Statement close, {}:{}", e.getClass().getName(), e.getMessage());
            }
        }
    }

    /**
     * Close {@link ResultSet}.
     *
     * @param rs
     *          a {@link ResultSet} object.
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                logger.warn("ResultSet close, {}:{}", e.getClass().getName(), e.getMessage());
            }
        }
    }

    /**
     * Insert Table all data.
     *
     * @param conn
     *          Database connection object.
     * @param table
     *          Table object.
     */
    public static void insert(Connection conn, Table table) {
        for (Row row : table.getDataList()) {
            insert(conn, table, row);
        }
    }

    /**
     * Insert Table row data.
     *
     * @param conn
     *          Database connection object.
     * @param table
     *          Table object.
     * @param row
     *          Table row object.
     */
    public static void insert(Connection conn, Table table, Row row) {
        StringBuilder sqlBuff = new StringBuilder(128);
        StringBuilder valueBuff = new StringBuilder(128);
        sqlBuff.append("insert into ").append("`").append(table.getName()).append("`(");

        int idx = 0;
        Object[] params = new Object[row.getDataMap().size()];
        Iterator<String> it = row.getDataMap().keySet().iterator();
        while (it.hasNext()) {
            String columnName = it.next();
            Object value = row.getDataMap().get(columnName);
            params[idx++] = value;
            sqlBuff.append("`").append(columnName).append("`");
            valueBuff.append("?");
            if (it.hasNext()) {
                sqlBuff.append(", ");
                valueBuff.append(", ");
            }
        }
        sqlBuff.append(") values(").append(valueBuff.toString()).append(")");
        execute(conn, sqlBuff.toString(), params);
    }

    /**
     * All data in the query table.
     *
     * @param conn
     *          Database connection object.
     * @param table
     *          Table object.
     * @return all row data.
     */
    public static List<Map<String, Object>> find(Connection conn, Table table) {
        String sql = String.format("select * from `%s`", table.getName());
        if (StringUtils.isNotBlank(table.getSort())) {
            sql = sql.concat(table.getSort());
        }

        return find(conn, sql, table.getSqlTypes());
    }

    /**
     * Query all data in the table based on SQL.
     *
     * @param dataSource
     *          a {@link DataSource} object.
     * @param sql
     *          SQL content.
     * @return all fetch data.
     */
    public static List<Map<String, Object>> find(DataSource dataSource, String sql) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            return find(conn, sql, null);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    /**
     * Query all data in the table based on SQL.
     * @param conn
     *          Database connection object.
     * @param sql
     *          SQL content.
     * @return all fetch data.
     */
    public static List<Map<String, Object>> find(Connection conn, String sql) {
        return find(conn, sql, null);
    }

    /**
     * Query all data in the table based on SQL.
     * @param conn
     *          Database connection object.
     * @param sql
     *          SQL content.
     * @param sqlTypes
     *          {@link Types} map.
     * @return all fetch data.
     */
    public static List<Map<String, Object>> find(Connection conn, String sql, Map<String, Integer> sqlTypes) {
        Statement stat = null;
        ResultSet rs = null;
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> obj = new HashMap<>();
                list.add(obj);
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String name = rs.getMetaData().getColumnName(i).toLowerCase();
                    Integer sqlType = null;
                    if (sqlTypes != null) {
                        sqlType = sqlTypes.get(name);
                    }

                    if (sqlType != null) {
                        switch (sqlType) {
                            case Types.BLOB:
                                obj.put(name, findValue(readBlobContent(rs.getBlob(i))));
                                break;
                            case Types.CLOB:
                                obj.put(name, findValue(readBlobContent(rs.getClob(i))));
                                break;
                            default:
                                obj.put(name, findValue(rs.getObject(i)));
                        }
                    } else {
                        obj.put(name, findValue(rs.getObject(i)));
                    }
                }
            }

            return list;
        } catch (Exception e) {
            ZestSqlHelper.close(rs);
            ZestSqlHelper.close(stat);
            throw new ZestException(e);
        }
    }

    private static String readBlobContent(Clob clob) throws Exception {
        if (clob == null) {
            return null;
        }
        return StringUtils.join(IOUtils.readLines(clob.getCharacterStream()));
    }

    private static String readBlobContent(Blob blob) throws Exception {
        if (blob == null) {
            return null;
        }
        return StringUtils.join(IOUtils.readLines(blob.getBinaryStream(), StandardCharsets.UTF_8));
    }

    private static Object findValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer || value instanceof Long || value instanceof Byte) {
            return ((Number) value).longValue();
        }

        if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
            return ((Number) value).doubleValue();
        }

        // Timestamp , String
        return value;
    }

    /**
     * Execute a SQL.
     *
     * @param conn
     *          Database connection object.
     * @param sql
     *          SQL content.
     */
    public static void execute(Connection conn, String sql) {
        Statement stat = null;
        try {
            logger.debug("   SQL: {}", sql);
            stat = conn.createStatement();
            stat.executeUpdate(sql);
        } catch (Exception e) {
            throw new ZestException(e);
        } finally {
            close(stat);
        }
    }

    /**
     * Execute a SQL.
     *
     * @param conn
     *          Database connection object.
     * @param sql
     *          Pre-compiled SQL content.
     * @param values
     *          Parameters of pre-compiled SQL.
     */
    public static void execute(Connection conn, String sql, Object[] values) {
        PreparedStatement stat = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("   SQL: {}", sql);
                if (values != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < values.length; i++) {
                        Object value = values[i];
                        if (value == null) {
                            sb.append("NULL");
                        } else {
                            sb.append("(").append(value.getClass().getSimpleName()).append(")");
                            sb.append(parseValue(value));
                        }

                        if (i < values.length - 1) {
                            sb.append(", ");
                        }
                    }
                    logger.debug("PARAMS: {}", sb.toString());
                }

            }

            stat = conn.prepareStatement(sql);
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    stat.setObject(i + 1, values[i]);
                }
            }
            stat.execute();
        } catch (Exception e) {
            throw new ZestException(e);
        } finally {
            close(stat);
        }
    }

    /**
     * Query data only for display.
     *
     * @param dataSource
     *          a {@link DataSource} object.
     * @param sql
     *          SQL content.
     * @return text for display.
     */
    public static String query(DataSource dataSource, String sql) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            return query(conn, sql);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    /**
     * Query data only for display.
     *
     * @param conn
     *          Database connection object.
     * @param sql
     *          SQL content.
     * @return text for display.
     */
    public static String query(Connection conn, String sql) {
        Statement stat = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();
        try {
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);

            while (rs.next()) {
                sb.append("-------------------------------------------\n");
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String colName = rs.getMetaData().getColumnName(i).toLowerCase();
                    Object colValue = rs.getObject(i);
                    String colType = (colValue == null ? "UNKNOWN" : colValue.getClass().getName());
                    colValue = colValue == null ? "NULL" : parseValue(colValue);
                    sb.append(String.format("%s (%s) : %s", colName, colType, colValue)).append("\n");
                }
            }
            sb.append("===========================================");

        } catch (Exception e) {
            logger.error("", e);
            close(rs);
            close(stat);
        }

        return sb.toString();
    }

    private static String parseValue(Object value) {
        if (value instanceof Date) {
            return ZestDateUtil.formatDateNormal((Date) value);
        } else {
            String str = String.valueOf(value);
            str = StringUtils.replaceEach(str, //
                                          new String[] { "\t", "\r", "\n", "\"", "\\" }, //
                                          new String[] { "\\t", "\\r", "\\n", "\\\"", "\\\\" });
            if (value instanceof String) {
                return String.format("\"%s\"", str);
            } else {
                return str;
            }
        }
    }

}
