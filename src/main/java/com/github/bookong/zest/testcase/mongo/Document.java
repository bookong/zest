package com.github.bookong.zest.testcase.mongo;

import com.github.bookong.zest.executor.MongoExecutor;
import com.github.bookong.zest.testcase.AbstractRowData;
import com.github.bookong.zest.util.ZestJsonUtil;

/**
 * @author Jiang Xu
 */
public class Document extends AbstractRowData {

    private Object data;

    public Document(MongoExecutor mongoExecutor, Class<?> entityClass, String collectionName, String xmlContent){
        try {
            this.data = mongoExecutor.createDocument(entityClass, collectionName, xmlContent);
        } catch (UnsupportedOperationException e) {
            this.data = ZestJsonUtil.fromJson(xmlContent, entityClass);
        }

    }

    public Object getData() {
        return data;
    }

}
