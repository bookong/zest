package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.Sort;
import com.github.bookong.zest.support.xml.data.SqlTable;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestSqlHelper;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

/**
 * 关系型数据库的表
 * 
 * @author Jiang Xu
 */
public class Table extends AbstractTable<Object> {

    /** 排序的依据 */
    private String               sort;

    /** 关系型数据库相关数据 */
    private List<Row>            rowDataList = Collections.synchronizedList(new ArrayList<>());

    /** 关系型数据库的 SqlType */
    private Map<String, Integer> sqlTypes    = Collections.synchronizedMap(new HashMap<>());

    public Table(ZestWorker worker, String sourceId, String nodeName, Node node, Connection conn, boolean isTargetData){
        List<Node> elements = ZestXmlUtil.getElements(node.getChildNodes());
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);
        init(nodeName, elements, attrMap, isTargetData);
        loadSqlTypes(conn);
        // TODO
    }

    @Deprecated
    public Table(ZestWorker worker, String sourceId, SqlTable xmlTable, Connection conn, boolean isTargetData){
        // super(xmlTable);
        loadSqlTypes(conn);

        if (xmlTable.getSorts() != null && !xmlTable.getSorts().getSort().isEmpty()) {
            if (!isTargetData) {
                throw new ZestException(Messages.parseDataTableSort());
            }

            StringBuilder sb = new StringBuilder();
            sb.append(" order by");
            for (Sort item : xmlTable.getSorts().getSort()) {
                sb.append(" ").append(item.getField()).append(" ").append(item.getDirection());
            }
            this.sort = sb.toString();
        }

        int rowIdx = 1;
        for (com.github.bookong.zest.support.xml.data.Row xmlRow : xmlTable.getRow()) {
            rowDataList.add(new Row(worker, sourceId, getName(), rowIdx++, xmlRow, sqlTypes, isTargetData));
        }
    }

    public List<Row> getRowDataList() {
        return rowDataList;
    }

    public String getSort() {
        return sort;
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
            throw new ZestException(Messages.parseTableMeta(), e);
        } finally {
            ZestSqlHelper.close(rs);
        }
    }

    public Map<String, Integer> getSqlTypes() {
        return sqlTypes;
    }
}
