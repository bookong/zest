package com.github.bookong.zest.core.testcase;

import com.github.bookong.zest.exception.LoadTestCaseFileException;
import com.github.bookong.zest.support.xml.data.Row;
import com.github.bookong.zest.support.xml.data.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * 关系型数据库的表
 * 
 * @author jiangxu
 */
public class SqlDataSourceTable extends AbstractDataSourceTable<SqlDataSourceRow> {

    /** 关系型数据库相关数据 */
    private List<SqlDataSourceRow> rowDataList = new ArrayList<>();

    public SqlDataSourceTable(TestCaseData testCaseData, String dataSourceId, Table xmlTable,
                              List<AbstractDataConverter> dataConverterList,
                              boolean isTargetData) throws LoadTestCaseFileException{
        super(xmlTable);

        for (Row xmlRow : xmlTable.getRow()) {
            rowDataList.add(new SqlDataSourceRow(testCaseData, dataSourceId, getName(), xmlRow, dataConverterList,
                                                 isTargetData));
        }
    }

    @Override
    public List<SqlDataSourceRow> getRowDataList() {
        return rowDataList;
    }
}
