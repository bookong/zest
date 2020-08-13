package com.github.bookong.zest.runner.junit5;

import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.ZestTestParam;
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

public class ZestJUnit5Worker extends ZestWorker {

    @Override
    protected Connection getConnection(DataSource dataSource) {
        return DataSourceUtils.getConnection(dataSource);
    }

    public <T extends ZestTestParam> Stream<DynamicTest> test(Object testObj, Class<T> zestTestParamClass,
                                                              Consumer<T> fun) {
        return stream(iterator(testObj), ZestInfo::getName, //
                      info -> {
                          T param = before(info, zestTestParamClass);
                          fun.accept(param);
                          after();
                      });
    }

    private Iterator<ZestInfo> iterator(Object testObj) {
        try {
            String testMethodName = null;
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                if (testObj.getClass().getName().equals(ste.getClassName())) {
                    testMethodName = ste.getMethodName();
                    break;
                }
            }

            loadTestObjectAnnotation(testObj);
            Method testMethodFiled = ZestReflectHelper.getMethod(testObj, testMethodName);
            ZestTest zestTest = testMethodFiled.getAnnotation(ZestTest.class);
            if (zestTest == null) {
                throw new ZestException(Messages.noAnnotationZest());
            }

            String dir = ZestTestCaseUtil.getDir(testObj.getClass(), testMethodName);

            List<ZestInfo> list = new ArrayList<>();
            if (StringUtils.isBlank(zestTest.value())) {
                File searchDir = new File(dir);
                File[] searchFiles = searchDir.listFiles();
                if (searchFiles != null) {
                    for (File searchFile : searchFiles) {
                        if (searchFile.isFile() && searchFile.getName().endsWith(zestTest.extName())) {
                            list.add(new ZestInfo(searchFile.getAbsolutePath()));
                        }
                    }
                }
            } else {
                list.add(new ZestInfo(dir.concat(zestTest.value()).concat(zestTest.extName())));
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

    private <T extends ZestTestParam> T before(ZestInfo info, Class<T> zestTestParamClass) {
        try {
            testCaseData = new TestCaseData();
            T param = zestTestParamClass.newInstance();
            loadTestParamAnnotation(param);
            ZestTestCaseUtil.loadFromAbsolutePath(this, info.getTestCaseFilePath(), testCaseData);

            logger.info(Messages.statementRun(testCaseData.getDescription()));
            logger.info(info.getTestCaseFilePath());
            initDataSource();
            getTestCaseData().setStartTime(System.currentTimeMillis());

            return param;

        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.failRun(), e);
        }
    }

    private void after() {
        getTestCaseData().setEndTime(System.currentTimeMillis());
        checkTargetDataSource();
    }

    private static class ZestInfo {

        private String testCaseFileName;
        private String testCaseFilePath;

        public ZestInfo(String testCaseFilePath){
            this.testCaseFilePath = testCaseFilePath;
            this.testCaseFileName = testCaseFilePath.substring(testCaseFilePath.lastIndexOf(File.separator) + 1);
        }

        public String getName() {
            return String.format("[%s]", testCaseFileName);
        }

        public String getTestCaseFilePath() {
            return testCaseFilePath;
        }
    }
}
