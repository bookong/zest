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

    public DbUnitDataSet(TestCaseData testCaseData, TestCaseDataSource dataSource) throws AmbiguousTableNameException{
        tables = super.createTableNameMap();

        for (AbstractDataSourceTable<?> obj : dataSource.getInitData().getInitDataList()) {
            SqlDataSourceTable table = (SqlDataSourceTable) obj;
            tables.add(table.getName(), new DbUnitTable(testCaseData, dataSource, table));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ITableIterator createIterator(boolean reversed) {
        return new DefaultTableIterator((ITable[]) tables.orderedValues().toArray(new ITable[0]), reversed);
    }
}
