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

    public abstract void clear(ZestWorker worker, ZestData zestData, Source source);

    protected abstract void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable data);

    protected abstract void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable data);

    public void init(ZestWorker worker, ZestData zestData, Source source) {
        for (AbstractTable table : source.getInitData().getTableList()) {
            init(worker, zestData, source, table);
        }
    }

    public void verify(ZestWorker worker, ZestData zestData, Source source) {
        if (source.getVerifyData().isIgnoreVerify()) {
            logger.info(Messages.ignoreTargetData(source.getId()));
            return;
        }

        for (AbstractTable table : source.getVerifyData().getTableMap().values()) {
            if (table.isIgnoreVerify()) {
                logger.info(Messages.ignoreTargetTable(source.getId(), table.getName()));
                continue;
            }

            try {
                logger.info(Messages.verifyStart(source.getId(), table.getName()));
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
        source.getInitData().getTableList().forEach(table -> tableNames.add(table.getName()));
        source.getVerifyData().getTableMap().values().forEach(table -> tableNames.add(table.getName()));
        return tableNames;
    }

}
