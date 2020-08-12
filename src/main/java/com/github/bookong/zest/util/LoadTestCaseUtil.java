package com.github.bookong.zest.util;

import com.github.bookong.zest.core.ZestGlobalConstant;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.runner.ZestLauncher;
import com.github.bookong.zest.support.xml.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author jiangxu
 */
public class LoadTestCaseUtil {

    private static Logger logger = LoggerFactory.getLogger(LoadTestCaseUtil.class);

    /**
     * 从绝对路径加载 xml 文件
     *
     * @param filePath 数据文件绝对路径
     * @param zestData
     */
    public static void loadFromAbsolutePath(ZestLauncher launcher, String filePath, TestCaseData zestData) {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException(Messages.fileNotFound(filePath));
            }

            zestData.setFileName(file.getName());
            zestData.setFilePath(file.getAbsolutePath());
            JAXBContext cxt = JAXBContext.newInstance(ZestGlobalConstant.DATA_XML_CODE_PACKAGE);
            Unmarshaller unm = cxt.createUnmarshaller();
            fis = new FileInputStream(file);
            Data data = (Data) unm.unmarshal(fis);
            zestData.load(launcher, data);

        } catch (Exception e) {
            throw new RuntimeException(Messages.parseFile(filePath), e);
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

    /** 是不是关系型数据库 */
    public static boolean isRmdb(String type) {
        switch (type) {
            case ZestGlobalConstant.DataSourceType.MySQL:
            case ZestGlobalConstant.DataSourceType.Oracle:
            case ZestGlobalConstant.DataSourceType.SQLServer:
                return true;
            default:
                return false;
        }
    }
}
