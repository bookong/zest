package com.github.bookong.zest.thirdparty.dbunit;

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.OrderedTableNameMap;

import com.github.bookong.zest.core.testcase.AbstractDataSourceTable;
import com.github.bookong.zest.core.testcase.RmdbDataSourceTable;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;

/**
 * @author jiangxu
 */
public class DbUnitDataSet extends AbstractDataSet {

    private final OrderedTableNameMap tables;

    public DbUnitDataSet(TestCaseData testCaseData, TestCaseDataSource testCaseDataSource) throws AmbiguousTableNameException{
        tables = super.createTableNameMap();

        for (AbstractDataSourceTable<?> table : testCaseDataSource.getInitDatas()) {
            RmdbDataSourceTable initDataSourceTable = (RmdbDataSourceTable) table;
            tables.add(initDataSourceTable.getName(), new DbUnitTable(testCaseData, testCaseDataSource, initDataSourceTable));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        return new DefaultTableIterator((ITable[]) tables.orderedValues().toArray(new ITable[0]), reversed);
    }
}
