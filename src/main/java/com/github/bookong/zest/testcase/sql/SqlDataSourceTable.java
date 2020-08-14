package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.testcase.AbstractDataConverter;
import com.github.bookong.zest.testcase.AbstractDataSourceTable;
import com.github.bookong.zest.support.xml.data.Row;
import com.github.bookong.zest.support.xml.data.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** 关系型数据库的 SqlType */
    private Map<String, Integer>   sqlTypes    = new HashMap<>();

    public SqlDataSourceTable(String dataSourceId, Table xmlTable, List<AbstractDataConverter> dataConverterList,
                              Connection conn, boolean isTargetData){
        super(xmlTable);
        loadSqlTypes(conn);

        if (StringUtils.isNotBlank(xmlTable.getQuery()) && !isTargetData) {
            throw new RuntimeException(Messages.parseDataTableQuery());
        }
        this.query = xmlTable.getQuery();

        int rowIdx = 1;
        for (Row xmlRow : xmlTable.getRow()) {
            rowDataList.add(new SqlDataSourceRow(dataSourceId, getName(), rowIdx++, xmlRow, dataConverterList, sqlTypes,
                                                 isTargetData));
        }
    }

    @Override
    public List<SqlDataSourceRow> getRowDataList() {
        return rowDataList;
    }

    public String getQuery() {
        return query;
    }

    private void loadSqlTypes(Connection conn) {
        DatabaseMetaData dbMetaData;
        ResultSet rs = null;
        try {
            dbMetaData = conn.getMetaData();
            List<String> tableNames = new ArrayList<>();
            rs = dbMetaData.getTables(null, null, null, new String[] { "TABLE" });
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            ZestSqlHelper.close(rs);

            for (String tableName : tableNames) {
                rs = conn.getMetaData().getColumns(null, "%", tableName, "%");
                while (rs.next()) {
                    sqlTypes.put(StringUtils.lowerCase(rs.getString("column_name")), rs.getInt("data_type"));
                }
                ZestSqlHelper.close(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(Messages.parseDbMeta(), e);
        } finally {
            ZestSqlHelper.close(rs);
        }
    }

    public Map<String, Integer> getSqlTypes() {
        return sqlTypes;
    }
}
