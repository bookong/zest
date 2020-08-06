package com.github.bookong.zest.core.testcase;

import com.github.bookong.zest.support.xml.data.DataSource;

import java.util.List;

/**
 * @author jiangxu
 */
public class TestCaseDataSource {

    private String     id;

    private String     type;

    private InitData   initData;

    private TargetData targetData;

    public TestCaseDataSource(TestCaseData testCaseData, DataSource xmlDataSource,
                              List<AbstractDataConverter> dataConverterList){
        this.id = xmlDataSource.getId();
        this.type = xmlDataSource.getType();

        this.initData = new InitData(testCaseData, getId(), getType(), xmlDataSource.getInit(), dataConverterList);
        this.targetData = new TargetData(testCaseData, getId(), getType(), xmlDataSource.getInit(),
                                         xmlDataSource.getTarget(), dataConverterList);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public InitData getInitData() {
        return initData;
    }

    public TargetData getTargetData() {
        return targetData;
    }
}
