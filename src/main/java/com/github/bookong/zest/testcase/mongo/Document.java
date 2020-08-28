package com.github.bookong.zest.testcase.mongo;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.MongoExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractRowData;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jiang Xu
 */
public class Document extends AbstractRowData {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Object   data;

    public Document(MongoExecutor mongoExecutor, Class<?> entityClass, String collectionName, String xmlContent){
        try {
            this.data = mongoExecutor.createDocumentData(entityClass, collectionName, xmlContent);
        } catch (UnsupportedOperationException e) {
            this.data = ZestJsonUtil.fromJson(xmlContent, entityClass);
        }

    }

    public Object getData() {
        return data;
    }

    public void verify(MongoExecutor executor, MongoOperations operator, ZestData zestData, Source source,
                       Collection collection, int rowIdx, Object actualDocument) {
        try {
            try {
                executor.verifyDocument(operator, zestData, source, collection, rowIdx, actualDocument);
            } catch (UnsupportedOperationException e) {
                verify(zestData, source, collection, rowIdx, actualDocument);
            }
        } catch (Exception e) {
            throw new ZestException(Messages.verifyDocumentError(source.getId(), collection.getName(), rowIdx), e);
        }
    }

    public void verify(ZestData zestData, Source source, Collection collection, int rowIdx, Object actualDocument) {
        Set<String> verified = new HashSet<>();
        // TODO
    }
}
