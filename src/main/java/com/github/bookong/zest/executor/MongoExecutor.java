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
package com.github.bookong.zest.executor;

import com.github.bookong.zest.annotation.ZestSource;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.mongo.Document;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Operator for <em>MongoDB</em>.
 * 
 * @author Jiang Xu
 */
public class MongoExecutor extends AbstractExecutor {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> supportedOperatorClass() {
        return MongoOperations.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractTable createTable() {
        return new Collection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(ZestWorker worker, ZestData zestData, Source source) {
        for (AbstractTable<?> table : findAllTables(source)) {
            Collection collection = (Collection) table;
            removeAll(worker, source, collection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable sourceTable) {
        Collection collection = (Collection) sourceTable;

        List<Object> initDataList = new ArrayList<>(collection.getDataList().size() + 1);
        for (Document doc : collection.getDataList()) {
            initDataList.add(doc.getData());
        }

        if (!initDataList.isEmpty()) {
            insertDataList(worker, source, collection.getEntityClass(), initDataList);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable sourceTable) {
        Collection collection = (Collection) sourceTable;

        Query query = new Query();
        if (collection.getSort() != null) {
            query.with(collection.getSort());
        }

        List<?> actualList = findData(worker, source, collection);

        Assert.assertEquals(Messages.verifyTableSize(source.getId(), collection.getName()), collection.getDataList().size(), actualList.size());

        for (int i = 0; i < collection.getDataList().size(); i++) {
            Document expected = collection.getDataList().get(i);
            Object actual = actualList.get(i);
            expected.verify(worker, zestData, source, collection, i + 1, actual);
        }
    }

    /**
     * Remove all data in the <em>MongoDB</em> specified <em>Collection</em>.
     * <p>
     * Since the version of <em>spring-data-mongodb</em> that <em>Zest</em> relies on during compilation may be
     * inconsistent with the actual version used, this file needs to be overwritten if the specific
     * {@code MongoOperations} class changes
     *
     * @param worker
     *          An object that controls the entire Zest logic.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     * @param collection
     *          An object corresponding to <em>Collection</em> data in <em>MongoDB</em>.
     */
    protected void removeAll(ZestWorker worker, Source source, Collection collection) {
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
        operator.remove(new Query(), collection.getEntityClass());
    }

    /**
     * Insert data into the <em>Collection</em> of the target <em>MongoDB</em>.
     * <p>
     * Since the version of <em>spring-data-mongodb</em> that <em>Zest</em> relies on during compilation may be
     * inconsistent with the actual version used, this file needs to be overwritten if the specific
     * {@code MongoOperations} class changes
     *
     * @param worker
     *          An object that controls the entire Zest logic.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     * @param entityClass
     *          The entity class corresponding to <em>MongoDB's Collection</em>.
     * @param initDataList
     *          Data used for initialization.
     */
    protected void insertDataList(ZestWorker worker, Source source, Class<?> entityClass, List<Object> initDataList) {
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
        operator.insert(initDataList, entityClass);
    }

    /**
     * In the <em>verify phase</em>, read the data in the specified <em>Collection</em> from the target <em>MongoDB</em> for automatic verification.
     * <p>
     * Since the version of <em>spring-data-mongodb</em> that <em>Zest</em> relies on during compilation may be
     * inconsistent with the actual version used, this file needs to be overwritten if the specific
     * {@code MongoOperations} class changes
     *
     * @param worker
     *          An object that controls the entire Zest logic.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     * @param collection
     *          <em>MongoDB Collection</em> that needs to be verified.
     * @return the actual data of the specified <em>Collection</em> in <em>MongoDB</em>
     */
    protected List<?> findData(ZestWorker worker, Source source, Collection collection) {
        Query query = new Query();
        if (collection.getSort() != null) {
            query.with(collection.getSort());
        }
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
        return operator.find(query, collection.getEntityClass());
    }

    /**
     * According to the data in the test case, the data object of the {@link Document} is constructed.
     * <p>
     * If the subclass requires customized logic, this position can be covered.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param entityClass
     *          The entity class corresponding to <em>MongoDB's Collection</em>.
     * @param collectionName
     *          The name of the <em>Collection</em> in <em>MongoDB</em>.
     * @param xmlContent
     *          Values in the unit test case file (*.xml).
     * @param isVerifyElement
     *          Whether the value under the &lt;Verify&gt; element in the unit test case file (*.xml)
     * @return the data object of the {@link Document}.
     * @throws UnsupportedOperationException
     *          This method is not implemented by default.
     */
    public Object createDocumentData(ZestData zestData, Class<?> entityClass, String collectionName, String xmlContent,
                                     boolean isVerifyElement) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Customize the validation process of the Document object.
     * <p>
     * If the subclass requires customized logic, this position can be covered.
     *
     * @param operator
     *          <em>MongoDB</em> operator.
     * @param zestData
     *          An object containing unit test case data.
     * @param source
     *          An object containing information about the current {@link ZestSource}.
     * @param collection
     *          <em>MongoDB Collection</em> that needs to be verified.
     * @param rowIdx
     *          The number of the data.
     * @param expectedDocument
     *          Expected data.
     * @param actualData
     *          Actual Data.
     * @throws UnsupportedOperationException
     *          This method is not implemented by default.
     */
    public void verifyDocument(MongoOperations operator, ZestData zestData, Source source, Collection collection,
                               int rowIdx, Document expectedDocument,
                               Object actualData) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
