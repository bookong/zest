package com.github.bookong.zest.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.xml.data.Data;
import com.github.bookong.zest.exceptions.ParseTestCaseException;
import com.github.bookong.zest.util.Messages;

/**
 * @author jiangxu
 */
public class XmlTestCaseDataLoader {

    private static Logger logger = LoggerFactory.getLogger(XmlTestCaseDataLoader.class);

    /**
     * 从绝对路径加载 xml 文件
     * 
     * @param filePath 数据文件绝对路径
     * @param zestData
     * @param zestLauncher
     */
    public void loadFromAbsolutePath(String filePath, TestCaseData zestData, Launcher zestLauncher) throws ParseTestCaseException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new ParseTestCaseException(Messages.getString("xmlTestCaseDataLoader.fileNotFound", filePath));
            }

            zestData.setFileName(file.getName());
            JAXBContext cxt = JAXBContext.newInstance(ZestGlobalConstant.DATA_XML_CODE_PACKAGE);
            Unmarshaller unm = cxt.createUnmarshaller();
            fis = new FileInputStream(file);
            zestData.load(Data.class.cast(unm.unmarshal(fis)));
        } catch (ParseTestCaseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseTestCaseException(Messages.getString("xmlTestCaseDataLoader.failToParseFile", filePath), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }
}
