package com.github.bookong.zest.testcase;

import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.Init;
import com.github.bookong.zest.support.xml.data.Target;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jiangxu
 */
public class SourceTargetData extends AbstractSourceData {

    private boolean                    ignoreCheck;

    private boolean                    onlyCheckCoreData;

    /** 执行完，检测数据源的数据 */
    private Map<String, AbstractTable> targetDataMap = new LinkedHashMap<>();

    public SourceTargetData(ZestWorker worker, String sourceId, Init xmlInit, Target xmlTarget){
        this.ignoreCheck = xmlTarget.isIgnore();
        this.onlyCheckCoreData = xmlTarget.isOnlyCoreData();

        if (isIgnoreCheck()) {
            return;
        }

        for (AbstractTable table : createTables(worker, sourceId, xmlTarget, true)) {
            targetDataMap.put(table.getName(), table);
        }

        if (isOnlyCheckCoreData()) {
            return;
        }

        for (AbstractTable table : createTables(worker, sourceId, xmlInit, true)) {
            // 验证全部数据时，即使 Target 部分没有写，也要验证是否与 Init 部分一致
            targetDataMap.put(table.getName(), table);
        }

    }

    public boolean isIgnoreCheck() {
        return ignoreCheck;
    }

    public boolean isOnlyCheckCoreData() {
        return onlyCheckCoreData;
    }

    public Map<String, AbstractTable> getTargetDataMap() {
        return targetDataMap;
    }
}
