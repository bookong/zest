package com.github.bookong.zest.testcase.mongo;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.rule.AbstractRule;
import com.github.bookong.zest.support.xml.data.Document;
import com.github.bookong.zest.support.xml.data.MongoCollection;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestJsonUtil;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.w3c.dom.Node;

import java.sql.Connection;
import java.util.*;

/**
 * @author Jiang Xu
 */
public class Collection extends AbstractTable {

    private Sort                      mongoSort;

    private Class<?>                  entityClass;

    private Map<String, AbstractRule> ruleMap   = Collections.synchronizedMap(new LinkedHashMap<>());

    private List<Object>              documents = Collections.synchronizedList(new ArrayList<>());

    public Collection(ZestWorker worker, String sourceId, String nodeName, Node node, MongoOperations mongoOperations, boolean isTargetData) {
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);
        init(nodeName, attrMap);
        // TODO
    }

    public Collection(ZestWorker worker, String sourceId, MongoCollection xmlCollection, MongoOperations mongoOperations, boolean isTargetData){
//        super(xmlCollection);

        if (xmlCollection.getSorts() != null && !xmlCollection.getSorts().getSort().isEmpty()) {
            if (!isTargetData) {
                throw new ZestException(Messages.parseDataTableSort());
            }

            mongoSort = getSort(xmlCollection.getSorts().getSort().get(0));
            for (int i = 1; i < xmlCollection.getSorts().getSort().size(); i++) {
                mongoSort.and(getSort(xmlCollection.getSorts().getSort().get(i)));
            }
        }

        // TODO rules

        try {
            entityClass = Class.forName(xmlCollection.getEntityClass());
        } catch (Exception e) {
            throw new ZestException(e);
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

    public List<Object> getDocuments() {
        return documents;
    }
}
