package com.github.bookong.zest.runner.junit5;

import com.github.bookong.zest.core.testcase.ZestTestParam;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.runner.junit4.annotation.ZestTest;
import com.github.bookong.zest.util.ZestReflectHelper;
import com.github.bookong.zest.util.ZestTestCaseUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicTest;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.stream;

public class ZestJUnit5Worker extends ZestWorker {

    @Override
    protected Connection getConnection(DataSource dataSource) {
        return DataSourceUtils.getConnection(dataSource);
    }

    public <T extends ZestTestParam> Stream<DynamicTest> test(Object testObj, Class<T> zestTestParamClass,
                                                              Consumer<ZestInfo<T>> fun) {
        return stream(iterator(testObj, zestTestParamClass), ZestInfo::getName, //
                      info -> {
                          T param = before(info, zestTestParamClass);
                          info.setTestParam(param);
                          fun.accept(info);
                          after(info);
                      });
    }

    protected <T extends ZestTestParam> Iterator<ZestInfo<T>> iterator(Object testObj, Class<T> zestTestParamClass) {
        try {
            String testMethodName = null;
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                if (testObj.getClass().getName().equals(ste.getClassName())) {
                    testMethodName = ste.getMethodName();
                    break;
                }
            }

            Method testMethodFiled = ZestReflectHelper.getMethod(testObj, testMethodName);
            ZestTest zestTest = testMethodFiled.getAnnotation(ZestTest.class);
            String dir = ZestTestCaseUtil.getDir(testObj.getClass(), testMethodName);
            String extName = ".".concat(zestTest.extName());

            List<ZestInfo<T>> list = new ArrayList<>();
            if (StringUtils.isBlank(zestTest.value())) {
                File searchDir = new File(dir);
                File[] searchFiles = searchDir.listFiles();
                if (searchFiles != null) {
                    for (File searchFile : searchFiles) {
                        if (searchFile.isFile() && searchFile.getName().endsWith(extName)) {
                            list.add(new ZestInfo<>(searchFile.getAbsolutePath()));
                        }
                    }
                }
            } else {
                list.add(new ZestInfo<>(dir.concat(zestTest.value()).concat(extName)));
            }

            return list.iterator();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T extends ZestTestParam> T before(ZestInfo<T> info, Class<T> zestTestParamClass) {
        return null;
    }

    protected <T extends ZestTestParam> void after(ZestInfo<T> info) {

    }

}
