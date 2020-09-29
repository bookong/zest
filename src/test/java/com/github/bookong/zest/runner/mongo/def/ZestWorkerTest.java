package com.github.bookong.zest.runner.mongo.def;

import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.executor.MongoExecutor;
import com.github.bookong.zest.mock.FakeMongoOperations;
import com.github.bookong.zest.runner.AbstractZestJUnit5WorkerTest;
import com.github.bookong.zest.testcase.ZestParam;
import org.junit.Test;

/**
 * @author Jiang Xu
 */
public class ZestWorkerTest extends AbstractZestJUnit5WorkerTest {

    @ZestSource(value = "mongo", executorClass = MongoExecutor.class)
    private FakeMongoOperations fakeMongoOperations = new FakeMongoOperations();

    @Override
    protected void before() {
    }

    @Test
    public void test01() {
        run("01.xml", Param.class, param -> {
        });
    }

    public static class Param extends ZestParam {

    }
}
