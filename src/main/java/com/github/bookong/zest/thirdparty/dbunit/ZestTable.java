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
import org.dbunit.dataset.datatype.StringDataType;

import com.github.bookong.zest.core.testcase.data.ColumnMetaData;
import com.github.bookong.zest.core.testcase.data.InitTable;
import com.github.bookong.zest.core.testcase.data.TestCaseData;
import com.github.bookong.zest.util.ZestDateUtils;

/**
 * @author jiangxu
 *
 */
public class ZestTable extends AbstractTable {
	private InitTable initTable;
	private final ITableMetaData metaData;
	private TestCaseData testCaseData;

	public ZestTable(TestCaseData testCaseData, String tabName, InitTable initTable) {
		this.initTable = initTable;
		this.testCaseData = testCaseData;
		
		if (initTable.getDatas().size() > 0) {
			metaData = createMetaData(tabName, initTable.getDatas().get(0), initTable.getColumnMetaDatas());
		} else {
			metaData = new DefaultTableMetaData(tabName, new Column[0]);
		}
	}

	private ITableMetaData createMetaData(String tabName, LinkedHashMap<String, Object> rowData,
			Map<String, ColumnMetaData> columnMetaDatas) {
		
		List<Column> columnList = new ArrayList<Column>();
		for (String columnName : rowData.keySet()) {
			ColumnMetaData cm = columnMetaDatas.get(columnName);
			Column column = new Column(columnName, new StringDataType(columnName, cm.getDbSqlType()));
			columnList.add(column);
		}
		Column[] columns = (Column[]) columnList.toArray(new Column[0]);
		return new DefaultTableMetaData(tabName, columns);
	}

	
	/* (non-Javadoc)
	 * @see org.dbunit.dataset.ITable#getRowCount()
	 */
	public int getRowCount() {
		return initTable.getDatas().size();
	}

	/* (non-Javadoc)
	 * @see org.dbunit.dataset.ITable#getTableMetaData()
	 */
	public ITableMetaData getTableMetaData() {
		return metaData;
	}

	/* (non-Javadoc)
	 * @see org.dbunit.dataset.ITable#getValue(int, java.lang.String)
	 */
	public Object getValue(int row, String column) throws DataSetException {
		assertValidRowIndex(row);
		Object obj = initTable.getDatas().get(row).get(column);
		if (obj != null && (obj instanceof Date)) {
			return ZestDateUtils.getDateInDB((Date)obj, testCaseData);
		}
		return obj;
	}

}
