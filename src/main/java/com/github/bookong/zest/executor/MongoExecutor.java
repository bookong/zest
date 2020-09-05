package com.github.bookong.zest.executor;

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
 * @author Jiang Xu
 */
public class MongoExecutor extends AbstractExecutor {

    @Override
    public Class<?> supportedOperatorClass() {
        return MongoOperations.class;
    }

    @Override
    public AbstractTable createTable() {
        return new Collection();
    }

    @Override
    public void clear(ZestWorker worker, ZestData zestData, Source source) {
        for (AbstractTable<?> table : findAllTables(source)) {
            Collection collection = (Collection) table;
            removeAll(worker, source, collection);
        }
    }

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

    @Override
    protected void verify(ZestWorker worker, ZestData zestData, Source source, AbstractTable sourceTable) {
        Collection collection = (Collection) sourceTable;
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);

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

    /** 由于 Zest 编译时依赖 spring-data-mongodb 版本可能与实际使用版本不一致，如果具体的 MongoOperations 类变更需要覆盖此文件 */
    protected void removeAll(ZestWorker worker, Source source, Collection collection) {
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
        operator.remove(new Query(), collection.getEntityClass());
    }

    /** 由于 Zest 编译时依赖 spring-data-mongodb 版本可能与实际使用版本不一致，如果具体的 MongoOperations 类变更需要覆盖此文件 */
    protected void insertDataList(ZestWorker worker, Source source, Class<?> entityClass, List<Object> initDataList) {
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
        operator.insert(initDataList, entityClass);
    }

    /** 由于 Zest 编译时依赖 spring-data-mongodb 版本可能与实际使用版本不一致，如果具体的 MongoOperations 类变更需要覆盖此文件 */
    protected List<?> findData(ZestWorker worker, Source source, Collection collection) {
        Query query = new Query();
        if (collection.getSort() != null) {
            query.with(collection.getSort());
        }
        MongoOperations operator = worker.getOperator(source.getId(), MongoOperations.class);
        return operator.find(query, collection.getEntityClass());
    }

    /**
     * 根据测试用例中数据构建 Document 对象，子类可以覆盖
     */
    public Object createDocumentData(ZestData zestData, Class<?> entityClass, String collectionName, String xmlContent, boolean isVerifyElement) {
        throw new UnsupportedOperationException();
    }

    /**
     * 自定义验证 Document 对象，子类可以覆盖
     */
    public void verifyDocument(MongoOperations operator, ZestData zestData, Source source, Collection collection, int rowIdx, Document expectedDocument, Object actualData) {
        throw new UnsupportedOperationException();
    }

}
