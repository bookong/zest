package com.github.bookong.zest.runner.junit5;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.ZestParam;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.annotation.ZestTest;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import com.github.bookong.zest.util.ZestTestCaseUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicTest;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.stream;

/**
 * @author Jiang Xu
 */
public class ZestJUnit5Worker extends ZestWorker {

    public <T extends ZestParam> Stream<DynamicTest> test(Object testObj, Class<T> zestParamClass, Consumer<T> fun) {
        return stream(iterator(testObj), zestData -> String.format("[%s]", zestData.getFileName()), //
                      zestData -> {
                          T param = before(zestData, zestParamClass);
                          fun.accept(param);
                          after(zestData);
                      });
    }

    private Iterator<ZestData> iterator(Object testObj) {
        try {
            loadAnnotation(testObj);

            String testMethodName = null;
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                if (testObj.getClass().getName().equals(ste.getClassName())) {
                    testMethodName = ste.getMethodName();
                    break;
                }
            }

            Method testMethodFiled = ZestReflectHelper.getMethod(testObj, testMethodName);
            ZestTest zestTest = testMethodFiled.getAnnotation(ZestTest.class);
            if (zestTest == null) {
                throw new ZestException(Messages.noAnnotationZest());
            }

            String dir = ZestTestCaseUtil.getDir(testObj.getClass(), testMethodName);

            List<ZestData> list = new ArrayList<>();
            if (StringUtils.isBlank(zestTest.value())) {
                File searchDir = new File(dir);
                File[] searchFiles = searchDir.listFiles();
                if (searchFiles != null) {
                    for (File searchFile : searchFiles) {
                        if (searchFile.isFile() && searchFile.getName().endsWith(zestTest.extName())) {
                            list.add(new ZestData(searchFile.getAbsolutePath()));
                        }
                    }
                }
            } else {
                list.add(new ZestData(dir.concat(zestTest.value()).concat(zestTest.extName())));
            }

            if (list.isEmpty()) {
                throw new ZestException(Messages.noData());
            }
            return list.iterator();

        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.failRun(), e);
        }
    }

    private <T extends ZestParam> T before(ZestData zestData, Class<T> zestParamClass) {
        try {
            T param = zestParamClass.newInstance();
            zestData.setTestParam(param);
            ZestTestCaseUtil.loadFromAbsolutePath(this, zestData);
            prepare(zestData);

            logger.info(Messages.statementRun(zestData.getDescription()));
            logger.info(zestData.getFilePath());
            initDataSource(zestData);
            zestData.setStartTime(System.currentTimeMillis());

            return param;

        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.failRun(), e);
        }
    }

    private void after(ZestData zestData) {
        zestData.setEndTime(System.currentTimeMillis());
        checkTargetDataSource(zestData);
    }

    @Override
    protected Connection getConnection(DataSource dataSource) {
        return DataSourceUtils.getConnection(dataSource);
    }
}
