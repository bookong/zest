package com.github.bookong.zest.testcase;

import com.github.bookong.zest.testcase.sql.SqlDataSourceTable;
import com.github.bookong.zest.support.xml.data.Init;
import com.github.bookong.zest.support.xml.data.Table;
import com.github.bookong.zest.util.ZestTestCaseUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangxu
 */
public class InitData {

    /** 执行前，初始化数据源用的数据 */
    private List<AbstractDataSourceTable<?>> initDataList = new ArrayList<>();

    public InitData(String dataSourceId, String dataSourceType, Init xmlInit,
                    List<AbstractDataConverter> dataConverterList, Connection conn){
        if (ZestTestCaseUtil.isRmdb(dataSourceType)) {
            for (Table xmlTable : xmlInit.getTable()) {
                initDataList.add(new SqlDataSourceTable(dataSourceId, xmlTable, dataConverterList, conn, false));
            }
        }
    }

    public List<AbstractDataSourceTable<?>> getInitDataList() {
        return initDataList;
    }
}
