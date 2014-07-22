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

/**
 * @author jiangxu
 *
 */
public class SqlHelper {
	
	public static void safeClose(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				System.out.println("Fail to close Statement.");
			}
		}
	}
	
	public static void safeClose(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				System.out.println("Fail to close ResultSet.");
			}
		}
	}
	
	/** 从数据库中捞取数据并做适当的转换后返回 */
	public static List<Map<String, Object>> findDataInDatabase(Connection connection, String sql) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.createStatement();
			rs = stat.executeQuery(sql);

			while (rs.next()) {
				Map<String, Object> rowData = new HashMap<String, Object>();
				datas.add(rowData);
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					String colName = rs.getMetaData().getColumnName(i).toLowerCase();
					Object colValue = rs.getObject(i);

					if (colValue == null) {
						rowData.put(colName, colValue);
						
					} else if ((colValue instanceof Integer) 
							|| (colValue instanceof Long)
							|| (colValue instanceof Byte)) {
						rowData.put(colName, ((Number) colValue).longValue());
						
					} else if ((colValue instanceof Double) 
							|| (colValue instanceof Float)
							|| (colValue instanceof BigDecimal)) {
						rowData.put(colName, ((Number) colValue).doubleValue());
					
					} else if (colValue instanceof NClob) {
						NClob clob = (NClob)colValue;
						rowData.put(colName, clob.getSubString(1, Long.valueOf(clob.length()).intValue()));
						
					} else {
						// Timestamp , String
						rowData.put(colName, colValue);
					}
				}
			}

			return datas;
		} catch (Exception e) {
			safeClose(rs);
			safeClose(stat);
			throw new RuntimeException(e);
		}
	}
	
	/** 显示查询到的结果内容 */
	public static void showResultSetContent(Connection connection, String sql) {
		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.createStatement();
			rs = stat.executeQuery(sql);
			
			while(rs.next()) {
				System.out.println("-------------------------------------------");
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					String colName = rs.getMetaData().getColumnName(i);
					Object colValue = rs.getObject(i);
					String colType = (colValue == null ? "UNKNOWN" : colValue.getClass().getName());
					System.out.println(colName.toLowerCase() + "(" + colType + "):" + colValue + "\t");
				}
			}
			System.out.println("===========================================");
			
		} catch (Exception e) {
			safeClose(rs);
			safeClose(stat);
			e.printStackTrace();
		}
	}
}
