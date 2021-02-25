/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.testcase.sql;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.rule.AbstractRule;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database table.
 * 
 * @author Jiang Xu
 */
public class Table extends AbstractTable<Row> {

    private String               sort;
    private Map<String, Integer> sqlTypes = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init(ZestWorker worker, String sourceId, XmlNode xmlNode, Map<String, String> tableEntityClassMap) {
        xmlNode.checkSupportedAttrs(Xml.NAME, Xml.IGNORE);

        SqlExecutor executor = worker.getExecutor(sourceId, SqlExecutor.class);
        DataSource dataSource = worker.getOperator(sourceId, DataSource.class);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            executor.loadSqlTypes(conn, getName(), getSqlTypes());
        } catch (UnsupportedOperationException e) {
            loadSqlTypes(conn);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSorts(List<Sort> sortList) {
        if (sortList.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(" order by");
        for (int i = 0; i < sortList.size(); i++) {
            Sort item = sortList.get(i);
            if (!getSqlTypes().containsKey(item.getField())) {
                throw new ZestException(Messages.parseTableSortExist(item.getField()));
            }
            if (i > 0) {
                sb.append(",");
            }
            sb.append(" ").append(item.getField()).append(" ").append(item.getDirection());
        }
        this.sort = sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkRule(AbstractRule rule) {
        if (!getSqlTypes().containsKey(rule.getField())) {
            throw new ZestException(Messages.parseTableRule(rule.getField()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadData(ZestWorker worker, ZestData zestData, String sourceId, String xmlContent,
                            boolean isVerifyElement) {
        SqlExecutor sqlExecutor = worker.getExecutor(sourceId, SqlExecutor.class);
        getDataList().add(new Row(zestData, sqlExecutor, getSqlTypes(), getName(), xmlContent));
    }

    private void loadSqlTypes(Connection conn) {
        try {
            ZestSqlHelper.loadSqlTypes(conn, getName(), getSqlTypes());
        } catch (Exception e) {
            throw new ZestException(Messages.parseTableMeta(), e);
        }
    }

    /**
     * @return {@link Types} map.
     */
    public Map<String, Integer> getSqlTypes() {
        return sqlTypes;
    }

    /**
     * @return sort criteria for query.
     */
    public String getSort() {
        return sort;
    }

}
