package com.github.bookong.zest.thirdparty.dbunit;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;

import com.github.bookong.zest.core.testcase.data.InitTable;
import com.github.bookong.zest.util.DateUtils;

/**
 * @author jiangxu
 *
 */
public class ZestTable extends AbstractTable {
	private InitTable initTable;
	private final ITableMetaData metaData;
	private long currDbTimeDiff;

	public ZestTable(String tabName, InitTable initTable, long currDbTimeDiff) {
		this.initTable = initTable;
		this.currDbTimeDiff = currDbTimeDiff;
		if (initTable.getDatas().size() > 0) {
			metaData = createMetaData(tabName, initTable.getDatas().get(0), initTable.getColDataTypes());
		} else {
			metaData = new DefaultTableMetaData(tabName, new Column[0]);
		}
	}

	private static ITableMetaData createMetaData(String tabName, LinkedHashMap<String, Object> rowData,
			Map<String, Class<?>> colDataTypes) {
		List<Column> columnList = new ArrayList<Column>();
		for (String columnName : rowData.keySet()) {
			Column column = new Column(columnName, DataType.UNKNOWN);
			columnList.add(column);
		}
		Column[] columns = (Column[]) columnList.toArray(new Column[0]);
		return new DefaultTableMetaData(tabName, columns);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.dataset.ITable#getRowCount()
	 */
	public int getRowCount() {
		return initTable.getDatas().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.dataset.ITable#getTableMetaData()
	 */
	public ITableMetaData getTableMetaData() {
		return metaData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.dataset.ITable#getValue(int, java.lang.String)
	 */
	public Object getValue(int row, String column) throws DataSetException {
		assertValidRowIndex(row);
		Object obj = initTable.getDatas().get(row).get(column);
		if (obj != null && (obj instanceof Date)) {
			return DateUtils.getDateInDB((Date)obj, currDbTimeDiff);
		}
		return obj;
	}

}
