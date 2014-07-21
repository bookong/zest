package com.github.bookong.zest.thirdparty.dbunit;

import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.OrderedTableNameMap;

import com.github.bookong.zest.core.testcase.data.DataBase;
import com.github.bookong.zest.core.testcase.data.InitTable;

/**
 * @author jiangxu
 *
 */
public class ZestDataSet extends AbstractDataSet {
	protected static final Log logger = LogFactory.getLog(ZestDataSet.class);
	private final OrderedTableNameMap _tables;

	public ZestDataSet(DataBase database, long currDbTimeDiff) throws AmbiguousTableNameException {
		_tables = super.createTableNameMap();
		for (Entry<String, InitTable> entry : database.getInitTables().entrySet()) {
			_tables.add(entry.getKey(), new ZestTable(entry.getKey(), entry.getValue(), currDbTimeDiff));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ITableIterator createIterator(boolean reversed) throws DataSetException {
		ITable[] tables = (ITable[]) _tables.orderedValues().toArray(new ITable[0]);
        return new DefaultTableIterator(tables, reversed);
	}
}
