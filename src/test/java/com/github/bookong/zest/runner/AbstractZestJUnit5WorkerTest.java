package com.github.bookong.zest.runner;

import com.github.bookong.zest.runner.junit5.ZestJUnit5Worker;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.ZestParam;

import java.util.function.Consumer;

/**
 * @author Jiang Xu
 */
public abstract class AbstractZestJUnit5WorkerTest extends ZestJUnit5Worker {

    protected abstract void before();

    protected <T extends ZestParam> ZestData run(String filename, Class<T> zestParamClass, Consumer<T> fun) {
        before();
        loadAnnotation(this);
        String filePath = getClass().getResource(filename).getPath();
        ZestData zestData = new ZestData(filePath);

        T param = before(zestData, zestParamClass);
        fun.accept(param);
        after(zestData);
        return zestData;
    }
}
