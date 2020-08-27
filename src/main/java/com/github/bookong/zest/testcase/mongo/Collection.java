package com.github.bookong.zest.testcase.mongo;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.MongoExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.rule.AbstractRule;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Jiang Xu
 */
public class Collection extends AbstractTable<Document> {

    private org.springframework.data.domain.Sort sort;

    private Class<?>                             entityClass;

    private List<Object>                         documents      = new ArrayList<>();

    private List<Map<String, Object>>            verifyDataList = new ArrayList<>();

    public Collection(ZestWorker worker, String sourceId, Node node, MongoOperations mongoOperations, boolean isVerifyElement){
        try {
            XmlNode xmlNode = new XmlNode(node);
            setName(xmlNode.getAttr(Xml.NAME));
            xmlNode.checkSupportedAttrs(Xml.NAME, Xml.IGNORE, Xml.ENTITY_CLASS);

            String entityClassAttr = xmlNode.getAttrNotEmpty(Xml.ENTITY_CLASS);
            try {
                this.entityClass = Class.forName(entityClassAttr);
            } catch (ClassNotFoundException e) {
                throw new ZestException(Messages.parseCommonClassFound(entityClassAttr));
            }

            init(worker, sourceId, xmlNode, isVerifyElement);

        } catch (Exception e) {
            throw new ZestException(Messages.parseCollectionError(getName()), e);
        }
    }

    @Override
    protected void loadSorts(List<Sort> sortList) {
        if (sortList.isEmpty()) {
            return;
        }

        Set<String> fieldNames = new HashSet<>(entityClass.getDeclaredFields().length + 1);
        for (Field f : entityClass.getDeclaredFields()) {
            fieldNames.add(f.getName());
        }

        List<Order> orderList = new ArrayList<>(sortList.size() + 1);
        for (Sort item : sortList) {
            if (!fieldNames.contains(item.getField())) {
                throw new ZestException(Messages.parseCollectionSortExits(item.getField()));
            }
            orderList.add(getOrder(item));
        }

        this.sort = org.springframework.data.domain.Sort.by(orderList);
    }

    @Override
    protected void checkRule(AbstractRule rule) {
        Field field = ZestReflectHelper.getFieldByPath(this.entityClass, rule.getPath());
        if (field == null) {
            throw new ZestException(Messages.parseCollectionRule(rule.getPath()));
        }
    }

    @Override
    protected void loadData(ZestWorker worker, String sourceId, String content, boolean isVerifyElement) {
        MongoExecutor mongoExecutor = worker.getExecutor(sourceId, MongoExecutor.class);
        getDocuments().add(new Document(mongoExecutor, entityClass, getName(), content));
    }

    private Order getOrder(Sort item) {
        if (Xml.ASC.equalsIgnoreCase(item.getDirection())) {
            return new Order(Direction.ASC, item.getField());
        } else {
            return new Order(Direction.DESC, item.getField());
        }
    }

    public org.springframework.data.domain.Sort getSort() {
        return sort;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public List<Object> getDocuments() {
        return documents;
    }

    public List<Map<String, Object>> getVerifyDataList() {
        return verifyDataList;
    }
}
