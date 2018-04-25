package com.github.bookong.zest.core.testcase;

import java.util.List;

/**
 * 抽象的广义数据源广义的“表”
 * 
 * @author jiangxu
 */
public abstract class AbstractDataSourceTable<T extends AbstractDataSourceRow> {

    /** 广义的表名 */
    private String  name;
    /** 是否不验证目标数据源的表，这个标识只在 Target 下的 Table 中才有效 */
    private boolean ignoreCheckTarget;
    /** 排序的依据 */
    private String  querySql;

    public AbstractDataSourceTable(com.github.bookong.zest.core.xml.data.Table xmlTable){
        name = xmlTable.getName();
        ignoreCheckTarget = xmlTable.isIgnore();
        querySql = xmlTable.getQuerySQL();
    }

    public String getName() {
        return name;
    }

    public abstract List<T> getRowDatas();

    public boolean isIgnoreCheckTarget() {
        return ignoreCheckTarget;
    }

    public String getQuerySql() {
        return querySql;
    }

}
