package com.github.bookong.zest.util;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.ZestData;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Jiang Xu
 */
public class ZestUtil {

    private static Logger logger = LoggerFactory.getLogger(ZestUtil.class);

    /**
     * 从绝对路径加载 xml 文件
     */
    public static void loadFromAbsolutePath(ZestWorker worker, ZestData zestData) {
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

    public static String getDir(Class<?> testObjClass, String methodName) {
        return testObjClass.getResource("").getPath() //
                           .concat("data").concat(File.separator) //
                           .concat(testObjClass.getSimpleName()).concat(File.separator) //
                           .concat(methodName).concat(File.separator);
    }

    public static String getDir(TestClass testCase, FrameworkMethod frameworkMethod) {
        return getDir(testCase.getJavaClass(), frameworkMethod.getName());
    }
}
