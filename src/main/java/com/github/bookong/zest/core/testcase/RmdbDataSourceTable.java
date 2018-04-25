package com.github.bookong.zest.core.testcase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 关系型数据库的表
 * 
 * @author jiangxu
 */
public class RmdbDataSourceTable extends AbstractDataSourceTable<RmdbDataSourceRow> {

    /** 关系型数据库相关数据 */
    private List<RmdbDataSourceRow> rowDatas = new ArrayList<>();

    public RmdbDataSourceTable(TestCaseData testCaseData, com.github.bookong.zest.core.xml.data.Table xmlTable, boolean isTargetData){
        super(xmlTable);
        Map<String, Integer> colSqlTypes = testCaseData.getRmdbColSqlTypes().get(getName());
        if (colSqlTypes == null) {
            throw new RuntimeException(String.format("Table (%1$s) not found RMDB columns SqlType.", getName()));
        }

        for (com.github.bookong.zest.core.xml.data.Row xmlRow : xmlTable.getRow()) {
            rowDatas.add(new RmdbDataSourceRow(getName(), colSqlTypes, xmlRow, isTargetData));
        }
    }

    @Override
    public List<RmdbDataSourceRow> getRowDatas() {
        return rowDatas;
    }
}
