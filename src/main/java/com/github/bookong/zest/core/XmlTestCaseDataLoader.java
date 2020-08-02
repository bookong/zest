package com.github.bookong.zest.core;

import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.exception.LoadTestCaseFileException;
import com.github.bookong.zest.support.xml.data.Data;
import com.github.bookong.zest.util.Messages;
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
public class XmlTestCaseDataLoader {

    private static Logger logger = LoggerFactory.getLogger(XmlTestCaseDataLoader.class);

    /**
     * 从绝对路径加载 xml 文件
     * 
     * @param filePath 数据文件绝对路径
     * @param zestData
     */
    public void loadFromAbsolutePath(Launcher launcher, String filePath,
                                     TestCaseData zestData) throws LoadTestCaseFileException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new LoadTestCaseFileException(Messages.fileNotFound(filePath));
            }

            zestData.setFileName(file.getName());
            zestData.setFilePath(file.getAbsolutePath());
            JAXBContext cxt = JAXBContext.newInstance(ZestGlobalConstant.DATA_XML_CODE_PACKAGE);
            Unmarshaller unm = cxt.createUnmarshaller();
            fis = new FileInputStream(file);
            Data data = (Data) unm.unmarshal(fis);
            zestData.load(launcher, data);

        } catch (LoadTestCaseFileException e) {
            throw e;
        } catch (Exception e) {
            throw new LoadTestCaseFileException(Messages.failParseFile(filePath), e);
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
}
