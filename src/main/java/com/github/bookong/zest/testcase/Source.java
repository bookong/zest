package com.github.bookong.zest.testcase;

import com.github.bookong.zest.runner.ZestWorker;

/**
 * @author jiangxu
 */
public class Source {

    private String         id;

    private SourceInitData initData;

    private SourceTargetData targetData;

    public Source(ZestWorker worker, com.github.bookong.zest.support.xml.data.Source xmlDataSource){
        this.id = xmlDataSource.getId();

        this.initData = new SourceInitData(worker, getId(), xmlDataSource.getInit());
        this.targetData = new SourceTargetData(worker, getId(), xmlDataSource.getInit(), xmlDataSource.getTarget());
    }

    public String getId() {
        return id;
    }

    public SourceInitData getInitData() {
        return initData;
    }

    public SourceTargetData getTargetData() {
        return targetData;
    }
}
