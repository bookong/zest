package com.github.bookong.zest.core.testcase.sql;

import com.github.bookong.zest.core.testcase.AbstractDataConverter;
import com.github.bookong.zest.core.testcase.AbstractDataSourceTable;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.support.xml.data.Row;
import com.github.bookong.zest.support.xml.data.Table;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 关系型数据库的表
 * 
 * @author jiangxu
 */
public class SqlDataSourceTable extends AbstractDataSourceTable<SqlDataSourceRow> {

    /** 排序的依据 */
    private String                 query;

    /** 关系型数据库相关数据 */
    private List<SqlDataSourceRow> rowDataList = new ArrayList<>();

    public SqlDataSourceTable(TestCaseData testCaseData, String dataSourceId, Table xmlTable,
                              List<AbstractDataConverter> dataConverterList, boolean isTargetData){
        super(xmlTable);

        if (StringUtils.isNotBlank(xmlTable.getQuery()) && !isTargetData) {
            throw new RuntimeException(Messages.parseDataTableQuery());
        }
        this.query = xmlTable.getQuery();

        int rowIdx = 1;
        for (Row xmlRow : xmlTable.getRow()) {
            rowDataList.add(new SqlDataSourceRow(testCaseData, dataSourceId, getName(), rowIdx++, xmlRow,
                                                 dataConverterList, isTargetData));
        }
    }

    @Override
    public List<SqlDataSourceRow> getRowDataList() {
        return rowDataList;
    }

    public String getQuery() {
        return query;
    }
}
