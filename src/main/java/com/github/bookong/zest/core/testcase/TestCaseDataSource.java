package com.github.bookong.zest.core.testcase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.bookong.zest.core.ZestGlobalConstant;
import com.github.bookong.zest.exceptions.LoadTestCaseFileException;

/**
 * @author jiangxu
 */
public class TestCaseDataSource {

    private String                                  id;
    /** 是否忽略对数据源的结果进行验证 */
    private boolean                                 ignoreTargetData = false;
    /** 执行前，初始化数据源用的数据 */
    private List<AbstractDataSourceTable<?>>        initDatas        = new ArrayList<>();
    /** 执行完，检测数据源的数据 */
    private Map<String, AbstractDataSourceTable<?>> targetDatas      = new LinkedHashMap<>();
    private String                                  type;

    public TestCaseDataSource(TestCaseData testCaseData, com.github.bookong.zest.core.xml.data.DataSource xmlDataSource) throws LoadTestCaseFileException{
        try {
            id = xmlDataSource.getId();
            type = xmlDataSource.getType();

            if (isRmdb(type)) {
                for (com.github.bookong.zest.core.xml.data.Table xmlTable : xmlDataSource.getInit().getTable()) {
                    initDatas.add(new RmdbDataSourceTable(testCaseData, this, xmlTable, false));
                }
            }

            ignoreTargetData = xmlDataSource.getTarget().isIgnore();
            if (!ignoreTargetData) {
                if (isRmdb(type)) {
                    for (com.github.bookong.zest.core.xml.data.Table xmlTable : xmlDataSource.getTarget().getTable()) {
                        targetDatas.put(xmlTable.getName(), new RmdbDataSourceTable(testCaseData, this, xmlTable, true));
                    }
                }

                for (AbstractDataSourceTable<? extends AbstractDataSourceRow> initTable : initDatas) {
                    if (!targetDatas.containsKey(initTable.getName())) {
                        targetDatas.put(initTable.getName(), initTable);
                    }
                }
            }

        } catch (Exception e) {
            throw new LoadTestCaseFileException(String.format("解析 DataSource (ID:%1$s) 内容失败", xmlDataSource.getId()), e);
        }
    }

    /** 数据源是否是关系型数据库（MySQL, Oracle） */
    private boolean isRmdb(String type) {
        return ZestGlobalConstant.DataSourceType.MySQL.equals(type) || ZestGlobalConstant.DataSourceType.Oracle.equals(type);
    }

    public boolean isIgnoreTargetData() {
        return ignoreTargetData;
    }

    public String getId() {
        return id;
    }

    public List<AbstractDataSourceTable<?>> getInitDatas() {
        return initDatas;
    }

    public Map<String, AbstractDataSourceTable<?>> getTargetDatas() {
        return targetDatas;
    }

    public String getType() {
        return type;
    }

}
