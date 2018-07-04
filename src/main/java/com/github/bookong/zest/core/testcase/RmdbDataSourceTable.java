package com.github.bookong.zest.core.testcase;

import java.util.ArrayList;
import java.util.List;

/**
 * 关系型数据库的表
 * 
 * @author jiangxu
 */
public class RmdbDataSourceTable extends AbstractDataSourceTable<RmdbDataSourceRow> {

    /** 关系型数据库相关数据 */
    private List<RmdbDataSourceRow> rowDatas = new ArrayList<>();

    public RmdbDataSourceTable(TestCaseData testCaseData, TestCaseDataSource testCaseDataSource, com.github.bookong.zest.core.xml.data.Table xmlTable, boolean isTargetData){
        super(xmlTable);

        for (com.github.bookong.zest.core.xml.data.Row xmlRow : xmlTable.getRow()) {
            rowDatas.add(new RmdbDataSourceRow(testCaseData, testCaseDataSource, getName(), xmlRow, isTargetData));
        }
    }

    @Override
    public List<RmdbDataSourceRow> getRowDatas() {
        return rowDatas;
    }
}
