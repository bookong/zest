package com.github.bookong.zest.executor;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 抽象的执行器
 * 
 * @author Jiang Xu
 */
public abstract class AbstractExecutor {

    protected static Logger logger = LoggerFactory.getLogger(AbstractExecutor.class);

    public abstract Class<?> supportedOperatorClass();

    public abstract AbstractTable createTable(ZestWorker worker, String sourceId, Node node, boolean isVerifyElement);

    public abstract void clear(ZestWorker worker, ZestData zestData, Source source);

    protected abstract void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable data);

    protected abstract void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable data);

    protected abstract String getIgnoreTableInfo(Source source, AbstractTable data);

    public void init(ZestWorker worker, ZestData zestData, Source source) {
        for (AbstractTable table : source.getInitData().getTableList()) {
            init(worker, zestData, source, table);
        }
    }

    public void checkSupportedOperatorClass(ZestWorker worker, String sourceId) {
        Class<?> operatorClass = worker.getOperator(sourceId).getClass();
        if (!supportedOperatorClass().isAssignableFrom(operatorClass)) {
            throw new ZestException(Messages.parseSourceOperationUnknown(operatorClass.getName()));
        }
    }

    public void verify(ZestWorker worker, ZestData zestData, Source source) {
        if (source.getVerifyData().isIgnoreVerify()) {
            logger.info(Messages.verifyIgnore(source.getId()));
            return;
        }

        for (AbstractTable table : source.getVerifyData().getTableMap().values()) {
            if (table.isIgnoreVerify()) {
                logger.info(getIgnoreTableInfo(source, table));
                continue;
            }

            verify(worker, zestData, source, table);
        }
    }

    protected Set<String> findAllTableNames(Source source) {
        Set<String> tableNames = new LinkedHashSet<>();
        source.getInitData().getTableList().forEach(table -> tableNames.add(table.getName()));
        source.getVerifyData().getTableMap().values().forEach(table -> tableNames.add(table.getName()));
        return tableNames;
    }

}
