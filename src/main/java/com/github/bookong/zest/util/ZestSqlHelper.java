package com.github.bookong.zest.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 辅助操作 SQL
 * 
 * @author jiangxu
 */
public class ZestSqlHelper {

    private static Logger logger = LoggerFactory.getLogger(ZestSqlHelper.class);

    /**
     * 安全的关闭 Statement
     * 
     * @param stat 待关闭的 Statement
     */
    public static void close(Statement stat) {
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                logger.error("", e);
            }
        }
    }

    /**
     * 安全的关闭 ResultSet
     * 
     * @param rs 待关闭的 ResultSet
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("", e);
            }
        }
    }

    /**
     * 从数据库中捞取数据并做适当的转换后返回
     * 
     * @param conn 数据库连接
     * @param sql 查询 SQL
     * @return 返回列表中，Map 的 key 为字段名，value 为字段值
     */
    public static List<Map<String, Object>> findDataInDatabase(Connection conn, String sql) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<String, Object>();
                dataList.add(rowData);
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String colName = rs.getMetaData().getColumnName(i).toLowerCase();
                    Object colValue = rs.getObject(i);

                    if (colValue == null) {
                        rowData.put(colName, colValue);

                    } else
                        if ((colValue instanceof Integer) || (colValue instanceof Long) || (colValue instanceof Byte)) {
                            rowData.put(colName, ((Number) colValue).longValue());

                        } else if ((colValue instanceof Double) || (colValue instanceof Float)
                                   || (colValue instanceof BigDecimal)) {
                                       rowData.put(colName, ((Number) colValue).doubleValue());

                                   } else
                            if (colValue instanceof NClob) {
                                NClob clob = (NClob) colValue;
                                rowData.put(colName, clob.getSubString(1, Long.valueOf(clob.length()).intValue()));

                            } else {
                                // Timestamp , String
                                rowData.put(colName, colValue);
                            }
                }
            }

            return dataList;
        } catch (Exception e) {
            close(rs);
            close(stat);
            throw new RuntimeException(e);
        }
    }

    /**
     * 在控制台显示查询到的结果内容
     * 
     * @param conn 数据库连接
     * @param sql 待查询 SQL
     */
    public static void showResultSetContent(Connection conn, String sql) {
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);

            while (rs.next()) {
                logger.info("-------------------------------------------"); //$NON-NLS-1$
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String colName = rs.getMetaData().getColumnName(i);
                    Object colValue = rs.getObject(i);
                    String colType = (colValue == null ? "UNKNOWN" : colValue.getClass().getName()); //$NON-NLS-1$
                    logger.info("{} ({}) : {}", colName, colType, colValue); //$NON-NLS-1$
                }
            }
            logger.info("==========================================="); //$NON-NLS-1$

        } catch (Exception e) {
            close(rs);
            close(stat);
            logger.error("", e);
        }
    }
}
