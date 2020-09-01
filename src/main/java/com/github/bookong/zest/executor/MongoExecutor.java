package com.github.bookong.zest.executor;

import com.github.bookong.zest.exception.ZestException;
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
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Xu
 */
public class MongoExecutor extends AbstractExecutor {

    @Override
    public Class<?> supportedOperatorClass() {
        return MongoOperations.class;
    }

    @Override
    public AbstractTable createTable(ZestWorker worker, String sourceId, Node node, boolean isVerifyElement) {
        return new Collection(worker, sourceId, node, isVerifyElement);
    }

    @Override
    public void clear(ZestWorker worker, ZestData zestData, Source source) {
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
        for (String tableName : findAllTableNames(source)) {
            operator.dropCollection(tableName);
        }
    }

    @Override
    protected void init(ZestWorker worker, ZestData zestData, Source source, AbstractTable data) {
        if (!(data instanceof Collection)) {
            throw new ZestException(Messages.verifyCollectionExecutor(getClass().getName()));
        }

        Collection collection = (Collection) data;
        List<Object> initDataList = new ArrayList<>(collection.getDataList().size());
        for (Document doc : collection.getDataList()) {
            initDataList.add(doc.getData());
        }
        if (initDataList.isEmpty()) {
            return;
        }

        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
        operator.insert(initDataList, collection.getEntityClass());
    }

    @Override
    protected void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable data) {
        logger.info(Messages.verifyCollectionStart(source.getId(), data.getName()));
        if (!(data instanceof Collection)) {
            throw new ZestException(Messages.verifyCollectionExecutor(getClass().getName()));
        }

        Collection collection = (Collection) data;
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);

        Query query = new Query();
        if (collection.getSort() != null) {
            query.with(collection.getSort());
        }

        List<?> actualList = operator.find(query, collection.getEntityClass());

        Assert.assertEquals(Messages.verifyCollectionSize(source.getId(), collection.getName()),
                            collection.getDataList().size(), actualList.size());

        for (int i = 0; i < collection.getDataList().size(); i++) {
            Document expected = collection.getDataList().get(i);
            Object actual = actualList.get(i);
            expected.verify(this, operator, zestData, source, collection, i + 1, actual);
        }
    }

    @Override
    protected String getIgnoreTableInfo(Source source, AbstractTable data) {
        return Messages.verifyCollectionIgnore(source.getId(), data.getName());
    }

    /**
     * 根据测试用例中数据构建 Document 对象，子类可以覆盖
     */
    public Object createDocumentData(Class<?> entityClass, String collectionName, String xmlContent,
                                     boolean isVerifyElement) {
        throw new UnsupportedOperationException();
    }

    /**
     * 自定义验证 Document 对象，子类可以覆盖
     */
    public void verifyDocument(MongoOperations operator, ZestData zestData, Source source, Collection collection,
                               int rowIdx, Document expectedDocument, Object actualData) {
        throw new UnsupportedOperationException();
    }
}
