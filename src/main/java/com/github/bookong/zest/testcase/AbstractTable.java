package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.rule.AbstractRule;
import com.github.bookong.zest.rule.RuleFactory;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.util.*;

/**
 * 抽象的广义数据源广义的“表”
 * 
 * @author Jiang Xu
 */
public abstract class AbstractTable<T> {

    private List<T>                   dataList;

    private Map<String, AbstractRule> ruleMap;

    /** 广义的表名 */
    private String                    name;

    /** 是否不验证目标数据源的表，这个标识只在 Target 下的 Table 中才有效 */
    private boolean                   ignoreVerify;

    protected abstract void loadSorts(List<Sort> sortList);

    protected abstract void checkRule(AbstractRule rule);

    protected abstract void loadData(ZestWorker worker, String sourceId, String content, boolean isVerifyElement);

    protected void init(ZestWorker worker, String sourceId, XmlNode xmlNode, boolean isVerifyElement) {
        this.ignoreVerify = xmlNode.getAttrBoolean(Xml.IGNORE, false);
        this.dataList = new ArrayList<>(xmlNode.getChildren().size() + 1);

        boolean parseSorts = false;
        boolean parseRules = false;
        boolean parseData = false;
        int dataIdx = 1;
        for (Node child : xmlNode.getChildren()) {
            if (Xml.SORTS.equals(child.getNodeName())) {
                if (!isVerifyElement) {
                    throw new ZestException(Messages.parseSortsPosition());
                }

                if (parseSorts || parseRules || parseData) {
                    throw new ZestException(Messages.parseSortsOrder());
                }
                parseSorts(child);
                parseSorts = true;

            } else if (Xml.RULES.equals(child.getNodeName())) {
                if (parseRules || parseData) {
                    throw new ZestException(Messages.parseRulesPosition());
                }
                parseRules(child);
                parseRules = true;

            } else if (Xml.DATA.equals(child.getNodeName())) {
                parseData(worker, sourceId, child, dataIdx++, isVerifyElement);
                parseData = true;

            } else {
                throw new ZestException(Messages.parseCommonChildrenUnknown(xmlNode.getNodeName(),
                                                                            child.getNodeName()));
            }
        }
    }

    private void parseSorts(Node node) {
        try {
            XmlNode xmlNode = new XmlNode(node);
            xmlNode.checkSupportedAttrs();
            List<Node> children = xmlNode.getFixedNodeList(Messages.parseCommonChildrenList(xmlNode.getNodeName(),
                                                                                            Xml.SORT),
                                                           Xml.SORT);
            List<Sort> list = new ArrayList<>(children.size() + 1);
            Set<String> fieldNames = new HashSet<>(children.size() + 1);
            for (Node child : children) {
                list.add(parseSort(child, fieldNames));
            }

            loadSorts(list);
        } catch (Exception e) {
            throw new ZestException(Messages.parseSortsError(), e);
        }
    }

    private void parseRules(Node node) {
        try {
            XmlNode xmlNode = new XmlNode(node);
            xmlNode.checkSupportedAttrs();
            List<Node> children = xmlNode.getFixedNodeList(Messages.parseCommonChildrenList(Xml.RULES, Xml.RULE),
                                                           Xml.RULE);
            this.ruleMap = new HashMap<>(children.size() + 1);
            Set<String> rulePaths = new HashSet<>(children.size() + 1);
            for (Node child : children) {
                parseRule(child, rulePaths);
            }
        } catch (Exception e) {
            throw new ZestException(Messages.parseRulesError(), e);
        }
    }

    private void parseData(ZestWorker worker, String sourceId, Node node, int dataIdx, boolean isVerifyElement) {
        try {
            XmlNode xmlNode = new XmlNode(node);
            loadData(worker, sourceId, xmlNode.getNodeValue(), isVerifyElement);
        } catch (Exception e) {
            throw new ZestException(Messages.parseDataError(dataIdx), e);
        }
    }

    private Sort parseSort(Node node, Set<String> fieldNames) {
        String fieldName = null;
        try {
            XmlNode xmlNode = new XmlNode(node);
            fieldName = xmlNode.getAttr(Xml.FIELD);
            if (StringUtils.isBlank(fieldName)) {
                throw new ZestException(Messages.parseCommonAttrEmpty(Xml.FIELD));
            }

            xmlNode.checkSupportedAttrs(Xml.FIELD, Xml.DIRECTION);
            String direction = xmlNode.getAttr(Xml.DIRECTION, Xml.ASC);
            if (!Xml.ASC.equals(direction) && !Xml.DESC.equals(direction)) {
                throw new ZestException(Messages.parseSortDirection());
            }

            xmlNode.mustNoChildren();
            XmlNode.duplicateCheck(Xml.FIELD, fieldNames, fieldName);

            return new Sort(fieldName, direction);
        } catch (Exception e) {
            throw new ZestException(Messages.parseSortError(fieldName), e);
        }
    }

    private void parseRule(Node node, Set<String> rulePaths) {
        String path = null;
        try {
            XmlNode xmlNode = new XmlNode(node);
            path = xmlNode.getAttr(Xml.PATH);
            if (StringUtils.isBlank(path)) {
                throw new ZestException(Messages.parseCommonAttrEmpty(Xml.PATH));
            }

            xmlNode.checkSupportedAttrs(Xml.PATH, Xml.NULLABLE);
            AbstractRule rule = RuleFactory.create(xmlNode, path);
            XmlNode.duplicateCheck(Xml.PATH, rulePaths, rule.getPath());
            checkRule(rule);
            ruleMap.put(rule.getPath(), rule);
        } catch (Exception e) {
            throw new ZestException(Messages.parseRuleError(path), e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (StringUtils.isBlank(name)) {
            throw new ZestException(Messages.parseCommonAttrEmpty(name));
        }
    }

    public boolean isIgnoreVerify() {
        return ignoreVerify;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public Map<String, AbstractRule> getRuleMap() {
        return ruleMap;
    }

    protected class Sort {

        private String field;
        private String direction;

        public Sort(String field, String direction){
            this.field = field;
            this.direction = direction;
        }

        public String getField() {
            return field;
        }

        public String getDirection() {
            return direction;
        }

    }
}
