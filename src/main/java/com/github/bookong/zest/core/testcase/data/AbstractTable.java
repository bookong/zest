package com.github.bookong.zest.core.testcase.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bookong.zest.exceptions.ParseTestCaseException;
import com.github.bookong.zest.util.LoadTestCaseUtils;

/**
 * @author jiangxu
 *
 */
public abstract class AbstractTable {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractTable.class);
	
	/** 表名 */
	private String tableName;
	/** 列数据的元数据 */
	private Map<String, ColumnMetaData> columnMetaDatas = new HashMap<String, ColumnMetaData>();
	/** 表中的数据 */
	private List<LinkedHashMap<String, Object>> datas = new ArrayList<LinkedHashMap<String, Object>>();
	
	public AbstractTable(String tableName) {
		this.tableName = tableName;
	}
	
	/** 如果是 JDBC 连接，从 JDBC 中得到元数据 */
	protected void loadColumnMetaDatas(Connection conn) {
		ResultSet rs = null;
		try {
			rs = conn.getMetaData().getColumns(null, "%", tableName, "%");
			while (rs.next()) {
				ColumnMetaData md = new ColumnMetaData();
				md.setColumnName(rs.getString("column_name"));
				md.setDbTypeName(rs.getString("type_name"));
				md.setDbTypeLenght(rs.getInt("character_maximum_length"));
				md.setDbSqlType(rs.getInt("data_type"));
				md.setAutoIncrement("YES".equalsIgnoreCase(rs.getString("is_autoincrement")));
				md.setNullable("YES".equalsIgnoreCase(rs.getString("is_nullable")));
				
				if ("bit".equalsIgnoreCase(md.getDbTypeName()) 
						|| "boolean".equalsIgnoreCase(md.getDbTypeName())) {
					md.setColumnClass(ColumnMetaData.TYPE_BOOLEAN);
					
				} else if ("tinyint".equalsIgnoreCase(md.getDbTypeName())
						|| "byte".equalsIgnoreCase(md.getDbTypeName())
						|| "integer".equalsIgnoreCase(md.getDbTypeName())
						|| "int".equalsIgnoreCase(md.getDbTypeName())) {
					md.setColumnClass(ColumnMetaData.TYPE_LONG);
					
				} else if ("datetime".equalsIgnoreCase(md.getDbTypeName())
						|| "smalldatetime".equalsIgnoreCase(md.getDbTypeName())
						|| "date".equalsIgnoreCase(md.getDbTypeName()) 
						|| "time".equalsIgnoreCase(md.getDbTypeName())
						|| "timestamp".equalsIgnoreCase(md.getDbTypeName())) {
					md.setColumnClass(ColumnMetaData.TYPE_DATE);
					
				} else if ("char".equalsIgnoreCase(md.getDbTypeName())
						|| "varchar".equalsIgnoreCase(md.getDbTypeName())
						|| "text".equalsIgnoreCase(md.getDbTypeName())
						|| "clob".equalsIgnoreCase(md.getDbTypeName())
						|| "nchar".equalsIgnoreCase(md.getDbTypeName())
						|| "nvarchar".equalsIgnoreCase(md.getDbTypeName())
						|| "ntext".equalsIgnoreCase(md.getDbTypeName())) {
					md.setColumnClass(ColumnMetaData.TYPE_STRING);
					
				} else if ("decimal".equalsIgnoreCase(md.getDbTypeName())
						|| "number".equalsIgnoreCase(md.getDbTypeName())
						|| "money".equalsIgnoreCase(md.getDbTypeName())
						|| "smallmoney".equalsIgnoreCase(md.getDbTypeName())
						|| "currency".equalsIgnoreCase(md.getDbTypeName())
						|| "numeric".equalsIgnoreCase(md.getDbTypeName())
						|| "float".equalsIgnoreCase(md.getDbTypeName())
						|| "double".equalsIgnoreCase(md.getDbTypeName())) {
					md.setColumnClass(ColumnMetaData.TYPE_DOUBLE);
					
				} else {
					logger.warn("Fail to parse database info table \"{}\", column \"{}\" type \"{}\" ",
							tableName, md.getColumnName(), md.getDbTypeName());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to load meta data from JDBC", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.warn("Fail to close ResultSet");
				}
			}
		}
	}
	
	/** 加载列数据 */
	protected Object loadColData(String colName, Object data) {
		try {
			ColumnMetaData columnMetaData = columnMetaDatas.get(colName);
			if (columnMetaData == null) {
				columnMetaData = parseColumnMetaData(colName, data);
				columnMetaDatas.put(colName, columnMetaData);
			}
			
			if (data == null || JSONUtils.isNull(data)) {
				return null;
			} else if (data instanceof JSONObject) {
				return getColumnData(columnMetaData, ((JSONObject)data).values().iterator().next());
			} else {
				return getColumnData(columnMetaData, data);
			}
			
		} catch (ParseTestCaseException e) {
			throw new RuntimeException("Fail to load column \"" + colName + "\", data:\"" + data + "\"", e);
		}
	}
	
	/** 读取列数据 */
	private Object getColumnData(ColumnMetaData md, Object data) {
		if (data == null || JSONUtils.isNull(data)) {
			return null;
		}

		if (md.getColumnClass().isAssignableFrom(ColumnMetaData.TYPE_STRING)) {
			if (data instanceof String) {
				return (String) data;
			}
			throw new RuntimeException("Column type must be String, but now is " + data.getClass().getName());
			
		} else if (md.getColumnClass().isAssignableFrom(ColumnMetaData.TYPE_LONG)) {
			if ((data instanceof Long) || (data instanceof Integer)) {
				return ((Number) data).longValue();
			}
			throw new RuntimeException("Column type must be Long or Integer, but now is " + data.getClass().getName());
			
		} else if (md.getColumnClass().isAssignableFrom(ColumnMetaData.TYPE_DOUBLE)) {
			if ((data instanceof Double) || (data instanceof Float) || (data instanceof Number)) {
				return ((Number) data).doubleValue();
			}
			throw new RuntimeException("Column type must be Double Float or Number, but now is "
					+ data.getClass().getName());
			
		} else if (md.getColumnClass().isAssignableFrom(ColumnMetaData.TYPE_DATE)) {
			if (data instanceof String) {
				return LoadTestCaseUtils.parseDate(((String) data).trim());
			}
			throw new RuntimeException("Column type must be String, but now is " + data.getClass().getName());
			
		} else if (md.getColumnClass().isAssignableFrom(ColumnMetaData.TYPE_BOOLEAN)) {
			if (data instanceof Boolean) {
				return (Boolean)data;
			}
			throw new RuntimeException("Column type must be Boolean, but now is " + data.getClass().getName());
			
		} else {
			throw new RuntimeException("Unknown column type " + md.getColumnClass().getName());
		}
	}
	
	/** 从 JSON 文件中分析数据类型 */
	private ColumnMetaData parseColumnMetaData(String colName, Object value) {
		ColumnMetaData md = new ColumnMetaData();
		md.setColumnName(colName);

		if (value.getClass().isAssignableFrom(Integer.class) || value.getClass().isAssignableFrom(Long.class)) {
			md.setColumnClass(ColumnMetaData.TYPE_LONG);

		} else if (value.getClass().isAssignableFrom(Double.class) || value.getClass().isAssignableFrom(Float.class)
				|| value.getClass().isAssignableFrom(Number.class)) {
			md.setColumnClass(ColumnMetaData.TYPE_DOUBLE);

		} else if (value.getClass().isAssignableFrom(String.class)) {
			md.setColumnClass(ColumnMetaData.TYPE_STRING);

		} else if (value.getClass().isAssignableFrom(Boolean.class)) {
			md.setColumnClass(ColumnMetaData.TYPE_BOOLEAN);

		} else if (value instanceof JSONObject) {
			md.setColumnClass(parseColClazz(colName, (JSONObject) value));

		} else {
			throw new RuntimeException("Fail to parse column meta data");
		}

		return md;
	}
	
	/** 
	 * 如果数据是通过 {string:"tab1 name"} 这种方式描述，得到列的数据类型。
	 * 目前支持的数据类型(不区分大小写)：string, long, double, date, boolean
	 */
	private static Class<?> parseColClazz(String colName, JSONObject json) {
		if (json.keySet().size() == 0) {
			throw new RuntimeException("Must specify the type (eg. \"{date:null}\")");
		}

		String colTypeDesc = String.valueOf(json.keys().next());
		for (Class<?> clazz : ColumnMetaData.allTypes()) {
			if (clazz.getSimpleName().equalsIgnoreCase(colTypeDesc)){
				return clazz;
			}
		}
		
		throw new RuntimeException("Unknown type \"" + colTypeDesc + "\"");
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, ColumnMetaData> getColumnMetaDatas() {
		return columnMetaDatas;
	}

	public void setColumnMetaDatas(Map<String, ColumnMetaData> columnMetaDatas) {
		this.columnMetaDatas = columnMetaDatas;
	}

	public List<LinkedHashMap<String, Object>> getDatas() {
		return datas;
	}

	public void setDatas(List<LinkedHashMap<String, Object>> datas) {
		this.datas = datas;
	}

}
