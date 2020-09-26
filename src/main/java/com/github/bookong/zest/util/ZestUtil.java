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
package com.github.bookong.zest.util;

import com.github.bookong.zest.common.ZestGlobalConstant;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.ZestData;
import org.apache.commons.lang3.StringUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.io.File;

/**
 * <em>Zest</em> tools.
 *
 * @author Jiang Xu
 */
public class ZestUtil {

    /**
     * Load unit test data from absolute path (*.xml).
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     */
    public static void loadZestData(ZestWorker worker, ZestData zestData) {
        try {
            File file = new File(zestData.getFilePath());
            if (!file.exists()) {
                throw new ZestException(Messages.fileNotFound(zestData.getFilePath()));
            }

            zestData.setFileName(file.getName());
            zestData.setFilePath(file.getAbsolutePath());
            zestData.load(worker);

        } catch (Exception e) {
            throw new ZestException(Messages.parse(zestData.getFilePath()), e);
        }
    }

    /**
     * The path of the unit test data file.
     *
     * @param testObjClass
     *          Test class.
     * @param methodName
     *          Test method name.
     * @return the path.
     */
    public static String getDir(Class<?> testObjClass, String methodName) {
        return testObjClass.getResource(StringUtils.EMPTY).getPath() //
                           .concat(ZestGlobalConstant.FIX_SUB_DIR).concat(File.separator) //
                           .concat(testObjClass.getSimpleName()).concat(File.separator) //
                           .concat(methodName).concat(File.separator);
    }

    /**
     * The path of the unit test data file.
     *
     * @param testCase
     *          Test class.
     * @param frameworkMethod
     *          Test method.
     * @return the path.
     */
    public static String getDir(TestClass testCase, FrameworkMethod frameworkMethod) {
        return getDir(testCase.getJavaClass(), frameworkMethod.getName());
    }
}
