package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.*;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.redis.core.RedisOperations;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Xu
 */
public abstract class AbstractSourceData {

    protected List<AbstractTable> createTables(ZestWorker worker, String sourceId, Object xml, boolean isTargetData) {
        Object operation = worker.getSourceOperation(sourceId);
        if (operation == null) {
            throw new ZestException(Messages.operationNull(sourceId));
        }

        List<SqlTable> sqlTableList;
        List<com.github.bookong.zest.support.xml.data.MongoCollection> mongoCollectionList;
        List<RedisData> redisDataList;

        String nodeName = "<Target>";
        if (xml instanceof Init) {
            nodeName = "<Init>";
            sqlTableList = ((Init) xml).getSqlTable();
            mongoCollectionList = ((Init) xml).getMongoCollection();
            redisDataList = ((Init) xml).getRedisData();
        } else {
            sqlTableList = ((Target) xml).getSqlTable();
            mongoCollectionList = ((Target) xml).getMongoCollection();
            redisDataList = ((Target) xml).getRedisData();
        }

        List<AbstractTable> list = new ArrayList<>();
        if (operation instanceof Connection) {
            if (CollectionUtils.isNotEmpty(mongoCollectionList) || CollectionUtils.isNotEmpty(redisDataList)) {
                throw new ZestException(Messages.operationMismatching(sourceId, nodeName));
            }

            for (SqlTable sqlTable : sqlTableList) {
                list.add(new Table(worker, sourceId, sqlTable, (Connection) operation, isTargetData));
            }

        } else if (operation instanceof MongoOperations) {
            if (CollectionUtils.isNotEmpty(sqlTableList) || CollectionUtils.isNotEmpty(redisDataList)) {
                throw new ZestException(Messages.operationMismatching(sourceId, nodeName));
            }

            for (MongoCollection mongoCollection : mongoCollectionList) {
                list.add(new Collection(worker, sourceId, mongoCollection, (MongoOperations) operation, isTargetData));
            }

        } else if (operation instanceof RedisOperations) {
            if (CollectionUtils.isNotEmpty(sqlTableList) || CollectionUtils.isNotEmpty(mongoCollectionList)) {
                throw new ZestException(Messages.operationMismatching(sourceId, nodeName));
            }

            for (RedisData redisData : redisDataList) {
                // TODO
            }

        } else {
            throw new ZestException(Messages.operationUnsupported(sourceId, operation.getClass().getName()));
        }

        return list;
    }
}
