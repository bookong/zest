package com.github.bookong.zest.util;

import com.github.bookong.zest.common.ZestGlobalConstant;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.Data;
import com.github.bookong.zest.testcase.ZestParam;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Jiang Xu
 */
public class ZestUtil {

    private static Logger logger = LoggerFactory.getLogger(ZestUtil.class);

    /**
     * 从绝对路径加载 xml 文件
     */
    public static void loadFromAbsolutePath(ZestWorker worker, ZestData zestData) {
        FileInputStream fis = null;
        try {
            File file = new File(zestData.getFilePath());
            if (!file.exists()) {
                throw new ZestException(Messages.fileNotFound(zestData.getFilePath()));
            }

            zestData.setFileName(file.getName());
            zestData.setFilePath(file.getAbsolutePath());
            JAXBContext cxt = JAXBContext.newInstance(ZestGlobalConstant.DATA_XML_CODE_PACKAGE);
            Unmarshaller unm = cxt.createUnmarshaller();
            fis = new FileInputStream(file);
            Data data = (Data) unm.unmarshal(fis);
//            zestData.load(worker, data); TODO

        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.parse(zestData.getFilePath()), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.warn("", e);
                }
            }
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
