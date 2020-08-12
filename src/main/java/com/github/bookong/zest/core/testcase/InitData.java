package com.github.bookong.zest.core.testcase;

import com.github.bookong.zest.core.testcase.sql.SqlDataSourceTable;
import com.github.bookong.zest.support.xml.data.Init;
import com.github.bookong.zest.support.xml.data.Table;
import com.github.bookong.zest.util.ZestTestCaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangxu
 */
public class InitData {

    /** 执行前，初始化数据源用的数据 */
    private List<AbstractDataSourceTable<?>> initDataList = new ArrayList<>();

    public InitData(TestCaseData testCaseData, String dataSourceId, String dataSourceType, Init xmlInit,
                    List<AbstractDataConverter> dataConverterList){
        if (ZestTestCaseUtil.isRmdb(dataSourceType)) {
            for (Table xmlTable : xmlInit.getTable()) {
                initDataList.add(new SqlDataSourceTable(testCaseData, dataSourceId, xmlTable, dataConverterList,
                                                        false));
            }
        }
    }

    public List<AbstractDataSourceTable<?>> getInitDataList() {
        return initDataList;
    }
}
