package com.github.bookong.zest.runner;

import com.github.bookong.zest.core.executer.AbstractExcuter;
import com.github.bookong.zest.core.executer.SqlExcuter;
import com.github.bookong.zest.core.testcase.AbstractDataConverter;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.runner.junit4.annotation.ZestDataSource;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangxu
 */
public abstract class ZestLauncher {

    protected static Logger                            logger           = LoggerFactory.getLogger(ZestLauncher.class);

    protected TestCaseData                             testCaseData;

    protected String                                   currTestCaseFilePath;

    protected Map<String, Connection>                  connectionMap    = new HashMap<>();

    protected Map<String, AbstractExcuter>             executerMap      = new HashMap<>();

    protected Map<String, List<AbstractDataConverter>> dataConverterMap = new HashMap<>();

    protected abstract Connection getConnection(DataSource dataSource);

    protected void loadAutowiredFieldFromTest(Object test) throws Exception {
        connectionMap.clear();
        executerMap.clear();

        Class<?> clazz = test.getClass();
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                ZestDataSource zestDataSource = f.getAnnotation(ZestDataSource.class);
                if (zestDataSource != null) {
                    Object obj = ZestReflectHelper.getValueByFieldName(test, f.getName());
                    if (obj instanceof DataSource) {
                        Connection conn = getConnection((DataSource) obj);
                        setConnection(zestDataSource.value(), conn);
                    } else {
                        throw new RuntimeException(Messages.parseDs());
                    }

                    setExecuter(zestDataSource.value(), zestDataSource.executerClass());
                    setDataConverter(zestDataSource.value(), zestDataSource.dataConverterClasses());
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public void initDataSource() {
        for (TestCaseDataSource dataSource : testCaseData.getDataSources()) {
            AbstractExcuter executer = executerMap.get(dataSource.getId());

            if (executer instanceof SqlExcuter) {
                Connection conn = connectionMap.get(dataSource.getId());
                SqlExcuter jdbcExcuter = (SqlExcuter) executer;

                jdbcExcuter.clearDatabase(conn, testCaseData, dataSource);
                jdbcExcuter.initDatabase(conn, testCaseData, dataSource);
            }
        }
    }

    public void checkTargetDataSource() {
        for (TestCaseDataSource dataSource : testCaseData.getDataSources()) {
            AbstractExcuter executer = executerMap.get(dataSource.getId());

            if (executer instanceof SqlExcuter) {
                Connection conn = connectionMap.get(dataSource.getId());
                SqlExcuter jdbcExcuter = (SqlExcuter) executer;

                if (dataSource.getTargetData().isIgnoreCheck()) {
                    logger.info(Messages.ignoreTargetData(dataSource.getId()));
                } else {
                    jdbcExcuter.checkTargetDatabase(conn, testCaseData, dataSource);
                }
            }
        }
    }

    private void setConnection(String dataSourceId, Connection conn) {
        if (connectionMap.containsKey(dataSourceId)) {
            throw new RuntimeException(Messages.duplicateDs(dataSourceId));
        }

        connectionMap.put(dataSourceId, conn);
        loadAllTableColSqlTypes(dataSourceId, conn);
    }

    private void setExecuter(String dataSourceId, Class<? extends AbstractExcuter> executerClass) {
        if (executerMap.containsKey(dataSourceId)) {
            return;
        }

        try {
            executerMap.put(dataSourceId, executerClass.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(Messages.initExecuter(executerClass.getName()), e);
        }
    }

    private void setDataConverter(String dataSourceId, Class<? extends AbstractDataConverter>[] dataConverterClasses) {
        for (Class<? extends AbstractDataConverter> dataConverterClass : dataConverterClasses) {
            try {
                dataConverterMap.computeIfAbsent(dataSourceId,
                                                 o -> new ArrayList<>()).add(dataConverterClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(Messages.initDc(dataConverterClass.getName()), e);
            }
        }
    }

    private void loadAllTableColSqlTypes(String dataSourceId, Connection conn) {
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
                    testCaseData.putRmdbTableColSqlTypes(dataSourceId, tableName, rs.getString("column_name"),
                                                         rs.getInt("data_type"));
                }
                ZestSqlHelper.close(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(Messages.parseDbMeta(), e);
        } finally {
            ZestSqlHelper.close(rs);
        }
    }

    public TestCaseData getTestCaseData() {
        return testCaseData;
    }

    public Connection getJdbcConn(String databaseName) {
        return connectionMap.get(databaseName);
    }

    public Map<String, List<AbstractDataConverter>> getDataConverterMap() {
        return dataConverterMap;
    }
}
