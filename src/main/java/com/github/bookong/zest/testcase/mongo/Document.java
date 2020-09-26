/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * <em>Document</em> data corresponding to <em>MongoDB</em>.
 *
 * @author Jiang Xu
 */
public class Document extends AbstractRow<Object> {

    private Object      data;
    private Set<String> expectedFields = new LinkedHashSet<>();

    /**
     * Create a new instance.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param mongoExecutor
     *          <em>MongoDB</em> executor.
     * @param entityClass
     *          <em>MongoDB Document</em> entity class.
     * @param collectionName
     *          <em>MongoDB Collection</em> name
     * @param xmlContent
     *          XML content.
     * @param isVerifyElement
     *          Is it under {@code SourceVerifyData}.
     */
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

    /**
     * {@inheritDoc}
     */
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
        } catch (AssertionError e) {
            logger.error(Messages.verifyDocumentError(source.getId(), collection.getName(), rowIdx));
            throw e;
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

    /**
     * @return data actually inserted into <em>MongoDB</em>
     */
    public Object getData() {
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getExpectedFields() {
        return expectedFields;
    }
}
