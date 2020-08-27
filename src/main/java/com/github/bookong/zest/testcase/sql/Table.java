package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.rule.AbstractRule;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestSqlHelper;
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
public class Table extends AbstractTable<Row> {

    /** 排序的依据 */
    private String               sort;

    /** 关系型数据库的 SqlType */
    private Map<String, Integer> sqlTypes = Collections.synchronizedMap(new HashMap<>());

    public Table(ZestWorker worker, String sourceId, Node node, Connection conn, boolean isVerifyElement){
        try {
            XmlNode xmlNode = new XmlNode(node);
            setName(xmlNode.getAttr(Xml.NAME));
            xmlNode.checkSupportedAttrs(Xml.NAME, Xml.IGNORE);

            SqlExecutor sqlExecutor = worker.getExecutor(sourceId, SqlExecutor.class);
            try {
                sqlExecutor.loadSqlTypes(conn, getSqlTypes());
            } catch (UnsupportedOperationException e) {
                loadSqlTypes(conn);
            }

            init(worker, sourceId, xmlNode, isVerifyElement);

        } catch (Exception e) {
            throw new ZestException(Messages.parseTableError(getName()), e);
        }
    }

    @Override
    protected void loadSorts(List<Sort> sortList) {
        if (sortList.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(" order by");
        for (int i = 0; i < sortList.size(); i++) {
            Sort item = sortList.get(i);
            if (!sqlTypes.containsKey(item.getField())) {
                throw new ZestException(Messages.parseTableSortExist(item.getField()));
            }
            if (i > 0) {
                sb.append(",");
            }
            sb.append(" ").append(item.getField()).append(" ").append(item.getDirection());
        }
        this.sort = sb.toString();
    }

    @Override
    protected void checkRule(AbstractRule rule) {
        if (!sqlTypes.containsKey(rule.getPath())) {
            throw new ZestException(Messages.parseTableRule(rule.getPath()));
        }
    }

    @Override
    protected void loadData(ZestWorker worker, String sourceId, String xmlContent) {
        SqlExecutor sqlExecutor = worker.getExecutor(sourceId, SqlExecutor.class);
        getDataList().add(new Row(sqlExecutor, getSqlTypes(), getName(), xmlContent));
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

    public String getSort() {
        return sort;
    }

}
