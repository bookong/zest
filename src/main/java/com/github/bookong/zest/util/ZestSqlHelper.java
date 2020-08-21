package com.github.bookong.zest.util;

import com.github.bookong.zest.common.ZestGlobalConstant;
import com.github.bookong.zest.exception.ZestException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * 辅助操作 SQL
 * 
 * @author Jiang Xu
 */
public class ZestSqlHelper {

    private static Logger logger = LoggerFactory.getLogger(ZestGlobalConstant.Logger.SQL);

    public static void close(Statement stat) {
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                logger.warn("Statement close, {}:{}", e.getClass().getName(), e.getMessage());
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.warn("ResultSet close, {}:{}", e.getClass().getName(), e.getMessage());
            }
        }
    }

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

    public static String query(Connection conn, String sql) {
        Statement stat = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();
        try {
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);

            while (rs.next()) {
                sb.append("-------------------------------------------\n"); //$NON-NLS-1$
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String colName = rs.getMetaData().getColumnName(i).toLowerCase();
                    Object colValue = rs.getObject(i);
                    String colType = (colValue == null ? "UNKNOWN" : colValue.getClass().getName()); //$NON-NLS-1$
                    colValue = colValue == null ? "NULL" : parseValue(colValue);
                    sb.append(String.format("%s (%s) : %s", colName, colType, //$NON-NLS-1$
                                            colValue)).append("\n"); // $NON-NLS-1$
                }
            }
            sb.append("==========================================="); //$NON-NLS-1$

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
