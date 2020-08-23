package com.github.bookong.zest.executor;

import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 抽象的执行器
 * 
 * @author Jiang Xu
 */
public abstract class AbstractExecutor {

    protected static Logger logger = LoggerFactory.getLogger(AbstractExecutor.class);

    protected abstract void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable data);

    protected abstract void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable data);

    /**
     * 清除数据
     * 
     * @param worker
     * @param zestData
     * @param source
     */
    public abstract void clear(ZestWorker worker, ZestData zestData, Source source);

    /**
     * 初始化数据
     * 
     * @param worker
     * @param zestData
     * @param source
     */
    public void init(ZestWorker worker, ZestData zestData, Source source) {
        for (AbstractTable table : source.getInitData().getInitDataList()) {
            init(worker, zestData, source, table);
        }
    }

    /**
     * 验证数据
     * 
     * @param worker
     * @param zestData
     * @param source
     */
    public void verify(ZestWorker worker, ZestData zestData, Source source) {
        if (source.getVerifyData().isIgnoreCheck()) {
            logger.info(Messages.ignoreTargetData(source.getId()));
            return;
        }

        for (AbstractTable table : source.getVerifyData().getTargetDataMap().values()) {
            if (table.isIgnoreCheckTarget()) {
                logger.info(Messages.ignoreTargetTable(source.getId(), table.getName()));
                continue;
            }

            try {
                logger.info(Messages.startCheckTable(source.getId(), table.getName()));
                verify(worker, zestData, source, table);
            } catch (AssertionError e) {
                throw e;
            } catch (Exception e) {
                throw new AssertionError(Messages.checkDs(source.getId()), e);
            }
        }
    }

    protected Set<String> findAllTableNames(Source source) {
        Set<String> tableNames = new LinkedHashSet<>();
        source.getInitData().getInitDataList().forEach(table -> tableNames.add(table.getName()));
        source.getVerifyData().getTargetDataMap().values().forEach(table -> tableNames.add(table.getName()));
        return tableNames;
    }

}
