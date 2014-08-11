package com.github.bookong.zest.core.testcase.data;

import java.util.Date;

/**
 * 列数据的元数据
 * @author jiangxu
 */
public class ColumnMetaData {
	/** 数据加载后存储的类型 Long */
	public static final Class<?> TYPE_LONG 		= Long.class;
	/** 数据加载后存储的类型 Double */
	public static final Class<?> TYPE_DOUBLE 	= Double.class;
	/** 数据加载后存储的类型 String */
	public static final Class<?> TYPE_STRING 	= String.class;
	/** 数据加载后存储的类型 Date */
	public static final Class<?> TYPE_DATE 		= Date.class;
	/** 数据加载后存储的类型 Boolean */
	public static final Class<?> TYPE_BOOLEAN 	= Boolean.class;
	
	/** 列名称 */
	private String columnName;
	/** 列对应的数据类型 */
	private Class<?> columnClass;
	/** 数据库中的类型名称 */
	private String dbTypeName;
	/** 数据库中的类型长度 */
	private int dbTypeLenght;
	/** 数据库中 SQL 类型 */
	private int dbSqlType;
	/** 是否是自增类型 */
	private boolean isAutoIncrement = false;
	/** 可否为空 */
	private boolean isNullable = true;
	
	/** 所有存储的类型 */
	public static Class<?>[] allTypes() {
		return new Class<?>[]{TYPE_LONG, TYPE_DOUBLE, TYPE_STRING, TYPE_DATE, TYPE_BOOLEAN};
	}
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public Class<?> getColumnClass() {
		return columnClass;
	}
	public void setColumnClass(Class<?> columnClass) {
		this.columnClass = columnClass;
	}
	public String getDbTypeName() {
		return dbTypeName;
	}
	public void setDbTypeName(String dbTypeName) {
		this.dbTypeName = dbTypeName;
	}
	public int getDbTypeLenght() {
		return dbTypeLenght;
	}
	public void setDbTypeLenght(int dbTypeLenght) {
		this.dbTypeLenght = dbTypeLenght;
	}
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}
	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}
	public boolean isNullable() {
		return isNullable;
	}
	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public int getDbSqlType() {
		return dbSqlType;
	}

	public void setDbSqlType(int dbSqlType) {
		this.dbSqlType = dbSqlType;
	}
}
