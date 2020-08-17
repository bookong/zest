package com.github.bookong.zest.testcase;

import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.Init;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Xu
 */
public class SourceInitData extends AbstractSourceData {

    /** 执行前，初始化数据源用的数据 */
    private List<AbstractTable> initDataList = new ArrayList<>();

    public SourceInitData(ZestWorker worker, String sourceId, Init xmlInit){
        initDataList.addAll(createTables(worker, sourceId, xmlInit, false));
    }

    public List<AbstractTable> getInitDataList() {
        return initDataList;
    }
}
