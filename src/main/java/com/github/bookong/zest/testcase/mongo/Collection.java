package com.github.bookong.zest.testcase.mongo;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.Document;
import com.github.bookong.zest.support.xml.data.MongoCollection;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestJsonUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jiang Xu
 */
public class Collection extends AbstractTable {

    private Sort         mongoSort;

    private Class<?>     entityClass;

    private List<Object> documents = Collections.synchronizedList(new ArrayList<>());

    public Collection(ZestWorker worker, String sourceId, MongoCollection xmlCollection,
                      MongoOperations mongoOperations, boolean isTargetData){
        super(xmlCollection);

        if (xmlCollection.getSorts() != null && !xmlCollection.getSorts().getSort().isEmpty()) {
            if (!isTargetData) {
                throw new ZestException(Messages.parseDataTableSort());
            }

            mongoSort = getSort(xmlCollection.getSorts().getSort().get(0));
            for (int i = 1; i < xmlCollection.getSorts().getSort().size(); i++) {
                mongoSort.and(getSort(xmlCollection.getSorts().getSort().get(i)));
            }

            try {
                entityClass = Class.forName(xmlCollection.getEntityClass());
            } catch (Exception e) {
                throw new ZestException(e);
            }
        }

        int rowIdx = 1;
        for (Document xmlDoc : xmlCollection.getDocument()) {
            try {
                documents.add(ZestJsonUtil.fromJson(xmlDoc.getValue(), entityClass));
                rowIdx++;
            } catch (Exception e) {
                throw new ZestException(Messages.parseDocObj(sourceId, getName(), rowIdx), e);
            }
        }
    }

    private Sort getSort(com.github.bookong.zest.support.xml.data.Sort item) {
        if ("asc".equalsIgnoreCase(item.getDirection())) {
            return Sort.by(Sort.Direction.ASC, item.getField());
        } else {
            return Sort.by(Sort.Direction.DESC, item.getField());
        }
    }

    public Sort getMongoSort() {
        return mongoSort;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}