package com.github.bookong.zest.testcase.data;

import com.github.bookong.zest.executor.MongoExecutor;
import com.github.bookong.zest.executor.SqlExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.runner.junit5.ZestJUnit5Worker;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.mock.MockDataSource;
import com.github.bookong.zest.testcase.mock.MockMongoOperations;
import com.github.bookong.zest.testcase.param.Param;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public abstract class AbstractZestDataTest {

    protected Logger     logger = LoggerFactory.getLogger(getClass());

    protected ZestWorker worker = new ZestJUnit5Worker();

    protected void initZestData(String filename, ZestData zestData) {
        DataSource dataSource = new MockDataSource();
        MongoOperations mongoOperations = new MockMongoOperations();

        Map<String, Object> operatorMap = (Map<String, Object>) ZestReflectHelper.getValue(worker, "operatorMap");
        operatorMap.put("mysql", dataSource);
        operatorMap.put("mongo", mongoOperations);
        operatorMap.put("unknown", new Object());

        Map<String, Object> executorMap = (Map<String, Object>) ZestReflectHelper.getValue(worker, "executorMap");
        executorMap.put("mysql", new SqlExecutor());
        executorMap.put("mongo", new MongoExecutor());
        executorMap.put("unknown", new SqlExecutor());
    }

    protected void testLoadError(String filename, String... errorMessages) {
        try {
            load(filename);
            Assert.fail("Should raise an exception");
        } catch (Exception e) {
            logger.info(e.getMessage());
            Assert.assertEquals(getExpectMessage(filename, errorMessages), e.getMessage());
        }
    }

    protected ZestData load(String filename) {
        String filePath = getClass().getResource(filename).getPath();
        ZestData zestData = new ZestData(filePath);
        Param param = new Param();
        zestData.setParam(param);
        param.setZestData(zestData);
        initZestData(filename, zestData);
        zestData.load(worker);
        return zestData;
    }

    private String getExpectMessage(String filename, String... errorMessages) {
        String filePath = getClass().getResource(filename).getPath();
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.parse(filePath));
        for (String errorMessage : errorMessages) {
            sb.append("\n").append(errorMessage);
        }
        return sb.toString();
    }
}
