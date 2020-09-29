package com.github.bookong.zest.mock;

import com.mongodb.ClientSessionOptions;
import com.mongodb.ReadPreference;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.util.CloseableIterator;

import java.util.*;

/**
 * @author Jiang Xu
 */
public class FakeMongoOperations implements MongoOperations {

    private Map<String, List<Object>> datas = new LinkedHashMap<>();

    @Override
    public DeleteResult remove(Query query, Class<?> entityClass) {
        String collectionName = getCollectionName(entityClass);
        datas.remove(collectionName);
        return null;
    }

    @Override
    public <T> Collection<T> insert(Collection<? extends T> batchToSave, Class<?> entityClass) {
        String collectionName = getCollectionName(entityClass);
        List<Object> list = datas.computeIfAbsent(collectionName, o -> new ArrayList<>());
        batchToSave.forEach(item -> list.add(item));
        return null;
    }

    @Override
    public String getCollectionName(Class<?> entityClass) {
        org.springframework.data.mongodb.core.mapping.Document ann = entityClass.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);
        return ann.collection();
    }

    @Override
    public <T> List<T> find(Query query, Class<T> entityClass) {
        for (Map.Entry<String, Object> entry : query.getSortObject().entrySet()) {
            System.out.println("sort " + entry.getKey() + " (" + entry.getValue().getClass().getName() + ") = " + String.valueOf(entry.getValue()));
        }

        String collectionName = getCollectionName(entityClass);
        return (List<T>)datas.computeIfAbsent(collectionName, o -> new ArrayList<>());
    }

    @Override
    public Document executeCommand(String jsonCommand) {
        return null;
    }

    @Override
    public Document executeCommand(Document command) {
        return null;
    }

    @Override
    public Document executeCommand(Document command, ReadPreference readPreference) {
        return null;
    }

    @Override
    public void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch) {

    }

    @Override
    public <T> T execute(DbCallback<T> action) {
        return null;
    }

    @Override
    public <T> T execute(Class<?> entityClass, CollectionCallback<T> action) {
        return null;
    }

    @Override
    public <T> T execute(String collectionName, CollectionCallback<T> action) {
        return null;
    }

    @Override
    public SessionScoped withSession(ClientSessionOptions sessionOptions) {
        return null;
    }

    @Override
    public MongoOperations withSession(ClientSession session) {
        return null;
    }

    @Override
    public <T> CloseableIterator<T> stream(Query query, Class<T> entityType) {
        return null;
    }

    @Override
    public <T> CloseableIterator<T> stream(Query query, Class<T> entityType, String collectionName) {
        return null;
    }

    @Override
    public <T> MongoCollection<Document> createCollection(Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> MongoCollection<Document> createCollection(Class<T> entityClass, CollectionOptions collectionOptions) {
        return null;
    }

    @Override
    public MongoCollection<Document> createCollection(String collectionName) {
        return null;
    }

    @Override
    public MongoCollection<Document> createCollection(String collectionName, CollectionOptions collectionOptions) {
        return null;
    }

    @Override
    public Set<String> getCollectionNames() {
        return null;
    }

    @Override
    public MongoCollection<Document> getCollection(String collectionName) {
        return null;
    }

    @Override
    public <T> boolean collectionExists(Class<T> entityClass) {
        return false;
    }

    @Override
    public boolean collectionExists(String collectionName) {
        return false;
    }

    @Override
    public <T> void dropCollection(Class<T> entityClass) {

    }

    @Override
    public void dropCollection(String collectionName) {

    }

    @Override
    public IndexOperations indexOps(String collectionName) {
        return null;
    }

    @Override
    public IndexOperations indexOps(Class<?> entityClass) {
        return null;
    }

    @Override
    public ScriptOperations scriptOps() {
        return null;
    }

    @Override
    public BulkOperations bulkOps(BulkOperations.BulkMode mode, String collectionName) {
        return null;
    }

    @Override
    public BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType) {
        return null;
    }

    @Override
    public BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType, String collectionName) {
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, String collectionName) {
        return null;
    }

    @Override
    public <T> GroupByResults<T> group(String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> GroupByResults<T> group(Criteria criteria, String inputCollectionName, GroupBy groupBy,
                                       Class<T> entityClass) {
        return null;
    }

    @Override
    public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String collectionName,
                                               Class<O> outputType) {
        return null;
    }

    @Override
    public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType) {
        return null;
    }

    @Override
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
        return null;
    }

    @Override
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType) {
        return null;
    }

    @Override
    public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, String collectionName,
                                                    Class<O> outputType) {
        return null;
    }

    @Override
    public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, Class<O> outputType) {
        return null;
    }

    @Override
    public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
        return null;
    }

    @Override
    public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, String collectionName,
                                                    Class<O> outputType) {
        return null;
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction,
                                             Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction,
                                             MapReduceOptions mapReduceOptions, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction,
                                             String reduceFunction, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction,
                                             String reduceFunction, MapReduceOptions mapReduceOptions,
                                             Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass, String collectionName) {
        return null;
    }

    @Override
    public <T> T findOne(Query query, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
        return null;
    }

    @Override
    public boolean exists(Query query, String collectionName) {
        return false;
    }

    @Override
    public boolean exists(Query query, Class<?> entityClass) {
        return false;
    }

    @Override
    public boolean exists(Query query, Class<?> entityClass, String collectionName) {
        return false;
    }

    @Override
    public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        return null;
    }

    @Override
    public <T> T findById(Object id, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
        return null;
    }

    @Override
    public <T> List<T> findDistinct(Query query, String field, Class<?> entityClass, Class<T> resultClass) {
        return null;
    }

    @Override
    public <T> List<T> findDistinct(Query query, String field, String collectionName, Class<?> entityClass,
                                    Class<T> resultClass) {
        return null;
    }

    @Override
    public <T> T findAndModify(Query query, UpdateDefinition update, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> T findAndModify(Query query, UpdateDefinition update, Class<T> entityClass, String collectionName) {
        return null;
    }

    @Override
    public <T> T findAndModify(Query query, UpdateDefinition update, FindAndModifyOptions options,
                               Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> T findAndModify(Query query, UpdateDefinition update, FindAndModifyOptions options, Class<T> entityClass,
                               String collectionName) {
        return null;
    }

    @Override
    public <S, T> T findAndReplace(Query query, S replacement, FindAndReplaceOptions options, Class<S> entityType,
                                   String collectionName, Class<T> resultType) {
        return null;
    }

    @Override
    public <T> T findAndRemove(Query query, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
        return null;
    }

    @Override
    public long count(Query query, Class<?> entityClass) {
        return 0;
    }

    @Override
    public long count(Query query, String collectionName) {
        return 0;
    }

    @Override
    public long count(Query query, Class<?> entityClass, String collectionName) {
        return 0;
    }

    @Override
    public <T> T insert(T objectToSave) {
        return null;
    }

    @Override
    public <T> T insert(T objectToSave, String collectionName) {
        return null;
    }

    @Override
    public <T> Collection<T> insert(Collection<? extends T> batchToSave, String collectionName) {
        return null;
    }

    @Override
    public <T> Collection<T> insertAll(Collection<? extends T> objectsToSave) {
        return null;
    }

    @Override
    public <T> T save(T objectToSave) {
        return null;
    }

    @Override
    public <T> T save(T objectToSave, String collectionName) {
        return null;
    }

    @Override
    public UpdateResult upsert(Query query, UpdateDefinition update, Class<?> entityClass) {
        return null;
    }

    @Override
    public UpdateResult upsert(Query query, UpdateDefinition update, String collectionName) {
        return null;
    }

    @Override
    public UpdateResult upsert(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
        return null;
    }

    @Override
    public UpdateResult updateFirst(Query query, UpdateDefinition update, Class<?> entityClass) {
        return null;
    }

    @Override
    public UpdateResult updateFirst(Query query, UpdateDefinition update, String collectionName) {
        return null;
    }

    @Override
    public UpdateResult updateFirst(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
        return null;
    }

    @Override
    public UpdateResult updateMulti(Query query, UpdateDefinition update, Class<?> entityClass) {
        return null;
    }

    @Override
    public UpdateResult updateMulti(Query query, UpdateDefinition update, String collectionName) {
        return null;
    }

    @Override
    public UpdateResult updateMulti(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
        return null;
    }

    @Override
    public DeleteResult remove(Object object) {
        return null;
    }

    @Override
    public DeleteResult remove(Object object, String collectionName) {
        return null;
    }

    @Override
    public DeleteResult remove(Query query, Class<?> entityClass, String collectionName) {
        return null;
    }

    @Override
    public DeleteResult remove(Query query, String collectionName) {
        return null;
    }

    @Override
    public <T> List<T> findAllAndRemove(Query query, String collectionName) {
        return null;
    }

    @Override
    public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass, String collectionName) {
        return null;
    }

    @Override
    public MongoConverter getConverter() {
        return null;
    }

    @Override
    public <T> ExecutableAggregation<T> aggregateAndReturn(Class<T> domainType) {
        return null;
    }

    @Override
    public <T> ExecutableFind<T> query(Class<T> domainType) {
        return null;
    }

    @Override
    public <T> ExecutableInsert<T> insert(Class<T> domainType) {
        return null;
    }

    @Override
    public <T> MapReduceWithMapFunction<T> mapReduce(Class<T> domainType) {
        return null;
    }

    @Override
    public <T> ExecutableRemove<T> remove(Class<T> domainType) {
        return null;
    }

    @Override
    public <T> ExecutableUpdate<T> update(Class<T> domainType) {
        return null;
    }
}
