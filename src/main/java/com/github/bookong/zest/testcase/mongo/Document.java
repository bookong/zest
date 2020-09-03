package com.github.bookong.zest.testcase.mongo;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.MongoExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractRow;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestJsonUtil;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.springframework.data.mongodb.core.MongoOperations;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jiang Xu
 */
public class Document extends AbstractRow<Object> {

    private Object      data;

    private Set<String> expectedFields = new LinkedHashSet<>();

    public Document(ZestData zestData, MongoExecutor mongoExecutor, Class<?> entityClass, String collectionName,
                    String xmlContent, boolean isVerifyElement){
        try {
            this.data = mongoExecutor.createDocumentData(zestData, entityClass, collectionName, xmlContent,
                                                         isVerifyElement);
        } catch (UnsupportedOperationException e) {
            this.data = ZestJsonUtil.fromJson(xmlContent, entityClass);
            for (Field f : getData().getClass().getDeclaredFields()) {
                Object value = ZestReflectHelper.getValue(getData(), f);
                if (value == null) {
                    continue;
                }

                if (value instanceof Date) {
                    Date valueInZest = ZestDateUtil.getDateInZest(zestData, (Date) value);
                    ZestReflectHelper.setValue(getData(), f.getName(), valueInZest);
                }
            }
        }

        if (isVerifyElement) {
            Map map = ZestJsonUtil.fromJson(xmlContent, Map.class);
            for (Object key : map.keySet()) {
                expectedFields.add(String.valueOf(key));
            }
        }
    }

    @Override
    public void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable<?> sourceTable, int rowIdx,
                       Object actualData) {
        Collection collection = (Collection) sourceTable;
        try {
            try {
                MongoExecutor executor = worker.getExecutor(source.getId(), MongoExecutor.class);
                MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
                executor.verifyDocument(operator, zestData, source, collection, rowIdx, this, actualData);
            } catch (UnsupportedOperationException e) {
                verify(zestData, collection, rowIdx, actualData);
            }
        } catch (Exception e) {
            throw new ZestException(Messages.verifyDocumentError(source.getId(), collection.getName(), rowIdx), e);
        }
    }

    private void verify(ZestData zestData, Collection collection, int rowIdx, Object actualData) throws Exception {
        if (!collection.getEntityClass().isAssignableFrom(actualData.getClass())) {
            throw new ZestException(Messages.verifyDocumentType(collection.getEntityClass().getName(),
                                                                actualData.getClass().getName()));
        }

        for (Field f : collection.getEntityClass().getDeclaredFields()) {
            String fieldName = f.getName();
            Object actual = ZestReflectHelper.getValue(actualData, f);
            Object expected = ZestReflectHelper.getValue(getData(), f);

            verify(zestData, collection, rowIdx, fieldName, expected, actual);
        }
    }

    public Object getData() {
        return data;
    }

    @Override
    protected Set<String> getExpectedFields() {
        return expectedFields;
    }
}
