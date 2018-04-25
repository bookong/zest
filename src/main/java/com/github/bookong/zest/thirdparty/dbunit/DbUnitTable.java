package com.github.bookong.zest.thirdparty.dbunit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.StringDataType;

import com.github.bookong.zest.core.testcase.RmdbDataSourceTable;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.util.ZestDateUtils;

/**
 * @author jiangxu
 */
public class DbUnitTable extends AbstractTable {

    private ITableMetaData      metaData;
    private TestCaseData        testCaseData;
    private RmdbDataSourceTable initDataSourceTable;

    public DbUnitTable(TestCaseData testCaseData, RmdbDataSourceTable initDataSourceTable){
        this.testCaseData = testCaseData;
        this.initDataSourceTable = initDataSourceTable;
        List<Column> columnList = new ArrayList<>();

        for (Entry<String, Integer> entry : testCaseData.getRmdbColSqlTypes().get(initDataSourceTable.getName()).entrySet()) {
            Column column = new Column(entry.getKey(), new StringDataType(entry.getKey(), entry.getValue()));
            columnList.add(column);
        }

        metaData = new DefaultTableMetaData(initDataSourceTable.getName(), columnList.toArray(new Column[0]));
    }

    @Override
    public int getRowCount() {
        return initDataSourceTable.getRowDatas().size();
    }

    @Override
    public ITableMetaData getTableMetaData() {
        return metaData;
    }

    @Override
    public Object getValue(int row, String column) throws DataSetException {
        assertValidRowIndex(row);
        Object obj = initDataSourceTable.getRowDatas().get(row).getFields().get(column);
        if (obj != null && (obj instanceof Date)) {
            return ZestDateUtils.getDateInDB((Date) obj, testCaseData);
        }
        return obj;
    }

}
