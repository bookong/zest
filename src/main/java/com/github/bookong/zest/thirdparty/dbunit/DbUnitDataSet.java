package com.github.bookong.zest.thirdparty.dbunit;

import com.github.bookong.zest.core.testcase.AbstractDataSourceTable;
import com.github.bookong.zest.core.testcase.sql.SqlDataSourceTable;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.*;

/**
 * @author jiangxu
 */
public class DbUnitDataSet extends AbstractDataSet {

    private final OrderedTableNameMap tables;

    public DbUnitDataSet(TestCaseData testCaseData,
                         TestCaseDataSource testCaseDataSource) throws AmbiguousTableNameException{
        tables = super.createTableNameMap();

        for (AbstractDataSourceTable<?> table : testCaseDataSource.getInitData().getInitDataList()) {
            SqlDataSourceTable initDataSourceTable = (SqlDataSourceTable) table;
            tables.add(initDataSourceTable.getName(),
                       new DbUnitTable(testCaseData, testCaseDataSource, initDataSourceTable));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ITableIterator createIterator(boolean reversed) {
        return new DefaultTableIterator((ITable[]) tables.orderedValues().toArray(new ITable[0]), reversed);
    }
}
