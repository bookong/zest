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
package com.github.bookong.zest.runner.junit5;

import com.github.bookong.zest.annotation.ZestTest;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.ZestParam;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import com.github.bookong.zest.util.ZestUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicTest;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.stream;

/**
 * Customized class runner for JUnit 5.
 *
 * @author Jiang Xu
 */
public class ZestJUnit5Worker extends ZestWorker {

    /**
     * Construct dynamic test code supporting <em>Zest</em>.
     *
     * @param testObj
     *          The test class to be run.
     * @param zestParamClass
     *          Parameters required by <em>Zest</em>, which are automatically filled in from the test case data file.
     * @param fun
     *          The main logic of the dynamic test method.
     * @param <T>
     *           <em>Zest's</em> test parameter class.
     * @return dynamic test.
     */
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

            String dir = ZestUtil.getDir(testObj.getClass(), testMethodName);

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

        } catch (Exception e) {
            throw new ZestException(Messages.runFail(), e);
        }
    }

    private <T extends ZestParam> T before(ZestData zestData, Class<T> zestParamClass) {
        try {
            T param = zestParamClass.newInstance();
            zestData.setParam(param);
            param.setZestData(zestData);
            ZestUtil.loadZestData(this, zestData);

            logger.info(Messages.run(zestData.getDescription()));
            logger.info(zestData.getFilePath());
            initSource(zestData);
            zestData.setStartTime(System.currentTimeMillis());
            return param;

        } catch (Exception e) {
            throw new ZestException(Messages.runFail(), e);
        }
    }

    private void after(ZestData zestData) {
        zestData.setEndTime(System.currentTimeMillis());
        verifySource(zestData);
    }
}
