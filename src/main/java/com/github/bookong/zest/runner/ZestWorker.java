package com.github.bookong.zest.runner;

import com.github.bookong.zest.annotation.ZestConnection;
import com.github.bookong.zest.annotation.ZestDataSource;
import com.github.bookong.zest.core.executer.AbstractExcuter;
import com.github.bookong.zest.core.executer.SqlExcuter;
import com.github.bookong.zest.core.testcase.AbstractDataConverter;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.core.testcase.ZestTestParam;
import com.github.bookong.zest.exception.ZestException;
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
import java.util.*;

/**
 * @author jiangxu
 */
public abstract class ZestWorker {

    protected static Logger                            logger           = LoggerFactory.getLogger(ZestWorker.class);

    protected TestCaseData                             testCaseData;

    protected Map<String, Connection>                  connectionMap    = new HashMap<>();

    protected Map<String, AbstractExcuter>             executerMap      = new HashMap<>();

    protected Map<String, List<AbstractDataConverter>> dataConverterMap = new HashMap<>();

    protected abstract Connection getConnection(DataSource dataSource);

    protected void loadTestObjectAnnotation(Object test) throws Exception {
        Class<?> clazz = test.getClass();
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                ZestDataSource zestDataSource = f.getAnnotation(ZestDataSource.class);
                if (zestDataSource != null) {
                    Object obj = ZestReflectHelper.getValue(test, f.getName());
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

    protected void setConnection(String dataSourceId, Connection conn) {
        if (connectionMap.containsKey(dataSourceId)) {
            throw new RuntimeException(Messages.duplicateDs(dataSourceId));
        }

        connectionMap.put(dataSourceId, conn);
    }

    protected void setExecuter(String dataSourceId, Class<? extends AbstractExcuter> executerClass) {
        if (executerMap.containsKey(dataSourceId)) {
            return;
        }

        try {
            executerMap.put(dataSourceId, executerClass.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(Messages.initExecuter(executerClass.getName()), e);
        }
    }

    protected void setDataConverter(String dataSourceId,
                                    Class<? extends AbstractDataConverter>[] dataConverterClasses) {
        if (dataConverterMap.containsKey(dataSourceId)) {
            return;
        }

        for (Class<? extends AbstractDataConverter> dataConverterClass : dataConverterClasses) {
            try {
                List<AbstractDataConverter> list = dataConverterMap.computeIfAbsent(dataSourceId,
                                                                                    o -> new ArrayList<>());
                list.add(dataConverterClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(Messages.initDc(dataConverterClass.getName()), e);
            }
        }
    }

    public TestCaseData getTestCaseData() {
        return testCaseData;
    }

    public Connection getJdbcConn(String dataSourceId) {
        return connectionMap.get(dataSourceId);
    }

    public List<AbstractDataConverter> getDataConverter(String dataSourceId) {
        return dataConverterMap.computeIfAbsent(dataSourceId, o -> Collections.emptyList());
    }

    protected void loadTestParamAnnotation(ZestTestParam param) throws Exception {
        for (Field f : param.getClass().getDeclaredFields()) {
            ZestConnection ann = f.getAnnotation(ZestConnection.class);
            if (ann != null && !Connection.class.getName().equals(f.getType().getName())) {
                throw new ZestException(Messages.annotationConnection());
            }

            ZestReflectHelper.setValue(param, f.getName(), connectionMap.get(ann.value()));
        }
    }
}
