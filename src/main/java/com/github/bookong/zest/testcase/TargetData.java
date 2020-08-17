package com.github.bookong.zest.testcase;

import com.github.bookong.zest.testcase.sql.SqlDataSourceTable;
import com.github.bookong.zest.support.xml.data.Init;
import com.github.bookong.zest.support.xml.data.Table;
import com.github.bookong.zest.support.xml.data.Target;
import com.github.bookong.zest.util.ZestTestCaseUtil;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangxu
 */
public class TargetData {

    private boolean                                 ignoreCheck;

    private boolean                                 onlyCheckCoreData;

    /** 执行完，检测数据源的数据 */
    private Map<String, AbstractDataSourceTable<?>> targetDataMap = new LinkedHashMap<>();

    public TargetData(String dataSourceId, String dataSourceType, Init xmlInit, Target xmlTarget,
                      List<AbstractDataConverter> dataConverterList, Connection conn){
        this.ignoreCheck = xmlTarget.isIgnore();
        this.onlyCheckCoreData = xmlTarget.isOnlyCoreData();

        if (isIgnoreCheck()) {
            return;
        }

        if (ZestTestCaseUtil.isRmdb(dataSourceType)) {
            for (Table xmlTable : xmlTarget.getTable()) {
                SqlDataSourceTable table = new SqlDataSourceTable(dataSourceId, xmlTable, dataConverterList, conn,
                                                                  true);
                targetDataMap.put(table.getName(), table);
            }
        }

        if (isOnlyCheckCoreData()) {
            return;
        }

        for (Table xmlTable : xmlInit.getTable()) {
            // 验证全部数据时，即使 Target 部分没有写，也要验证是否与 Init 部分一致
            if (!targetDataMap.containsKey(xmlTable.getName())) {
                SqlDataSourceTable table = new SqlDataSourceTable(dataSourceId, xmlTable, dataConverterList, conn,
                                                                  true);
                targetDataMap.put(table.getName(), table);
            }
        }
    }

    public boolean isIgnoreCheck() {
        return ignoreCheck;
    }

    public boolean isOnlyCheckCoreData() {
        return onlyCheckCoreData;
    }

    public Map<String, AbstractDataSourceTable<?>> getTargetDataMap() {
        return targetDataMap;
    }
}