package com.github.bookong.zest.core.testcase;

import com.github.bookong.zest.support.xml.data.Source;

import java.sql.Connection;
import java.util.List;

/**
 * @author jiangxu
 */
public class TestCaseDataSource {

    private String     id;

    private String     type;

    private InitData   initData;

    private TargetData targetData;

    public TestCaseDataSource(Source xmlDataSource, List<AbstractDataConverter> dataConverterList, Connection conn){
        this.id = xmlDataSource.getId();
        this.type = xmlDataSource.getType();

        this.initData = new InitData(getId(), getType(), xmlDataSource.getInit(), dataConverterList, conn);
        this.targetData = new TargetData(getId(), getType(), xmlDataSource.getInit(), xmlDataSource.getTarget(),
                                         dataConverterList, conn);
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
