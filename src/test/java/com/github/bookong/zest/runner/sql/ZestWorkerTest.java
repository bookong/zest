package com.github.bookong.zest.runner.sql;

import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.runner.junit5.ZestJUnit5Worker;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.param.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiang Xu
 */
public class ZestWorkerTest {

    protected Logger     logger = LoggerFactory.getLogger(getClass());

    protected ZestWorker worker = new ZestJUnit5Worker();

    protected ZestData load(String filename) {
        String filePath = getClass().getResource(filename).getPath();
        ZestData zestData = new ZestData(filePath);
        Param param = new Param();
        zestData.setParam(param);
        param.setZestData(zestData);
        zestData.load(worker);
        return zestData;
    }
}
