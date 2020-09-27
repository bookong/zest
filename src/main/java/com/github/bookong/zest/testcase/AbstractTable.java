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
package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.rule.AbstractRule;
import com.github.bookong.zest.rule.RuleFactory;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Abstract table.
 * 
 * @author Jiang Xu
 */
public abstract class AbstractTable<T> {

    private List<T>                   dataList = Collections.emptyList();
    private Map<String, AbstractRule> ruleMap  = Collections.emptyMap();
    private String                    name;
    private boolean                   ignoreVerify;

    /**
     * Initialize with unit test data.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param sourceId
     *          Data source id.
     * @param xmlNode
     *          XML element.
     * @param tableEntityClassMap
     *          Contains the <em>EntityClass</em> attributes bound on the  &lt;Table&gt; element in XML.
     */
    protected abstract void init(ZestWorker worker, String sourceId, XmlNode xmlNode,
                                 Map<String, String> tableEntityClassMap);

    /**
     * Initialize with unit test data.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param sourceId
     *          Data source id.
     * @param node
     *          XML element.
     * @param isVerifyElement
     *          Is it under {@code SourceVerifyData}.
     * @param tableEntityClassMap
     *          Contains the <em>EntityClass</em> attributes bound on the  &lt;Table&gt; element in XML.
     */
    public void init(ZestWorker worker, ZestData zestData, String sourceId, Node node, boolean isVerifyElement,
                     Map<String, String> tableEntityClassMap) {
        try {
            XmlNode xmlNode = new XmlNode(node);
            setName(xmlNode.getAttr(Xml.NAME));

            init(worker, sourceId, xmlNode, tableEntityClassMap);
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
                    if (!isVerifyElement) {
                        throw new ZestException(Messages.parseRulesPosition());
                    }

                    if (parseRules || parseData) {
                        throw new ZestException(Messages.parseRulesOrder());
                    }
                    parseRules(child);
                    parseRules = true;

                } else if (Xml.DATA.equals(child.getNodeName())) {
                    parseData(worker, zestData, sourceId, child, dataIdx++, isVerifyElement);
                    parseData = true;

                } else {
                    throw new ZestException(Messages.parseCommonChildrenUnknown(xmlNode.getNodeName(),
                                                                                child.getNodeName()));
                }
            }

        } catch (Exception e) {
            throw new ZestException(Messages.parseTableError(getName()), e);
        }
    }

    /**
     * Load sorting rules from the unit test data.
     *
     * @param sortList
     *          Sorting rules.
     */
    protected abstract void loadSorts(List<Sort> sortList);

    /**
     * Check if the rules are correct.
     *
     * @param rule
     *          Validation rule.
     */
    protected abstract void checkRule(AbstractRule rule);

    /**
     * Load from unit test data.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param sourceId
     *          Data source id.
     * @param content
     *          Content in XML.
     * @param isVerifyElement
     *          Is it under {@code SourceVerifyData}.
     */
    protected abstract void loadData(ZestWorker worker, ZestData zestData, String sourceId, String content,
                                     boolean isVerifyElement);

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

    private void parseData(ZestWorker worker, ZestData zestData, String sourceId, Node node, int dataIdx,
                           boolean isVerifyElement) {
        try {
            XmlNode xmlNode = new XmlNode(node);
            loadData(worker, zestData, sourceId, xmlNode.getNodeValue(), isVerifyElement);
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
        String field = null;
        try {
            XmlNode xmlNode = new XmlNode(node);
            field = xmlNode.getAttr(Xml.FIELD);
            if (StringUtils.isBlank(field)) {
                throw new ZestException(Messages.parseCommonAttrEmpty(Xml.FIELD));
            }

            xmlNode.checkSupportedAttrs(Xml.FIELD, Xml.NULLABLE);
            AbstractRule rule = RuleFactory.create(xmlNode, field);
            XmlNode.duplicateCheck(Xml.FIELD, rulePaths, rule.getField());
            checkRule(rule);
            ruleMap.put(rule.getField(), rule);
        } catch (Exception e) {
            throw new ZestException(Messages.parseRuleError(field), e);
        }
    }

    /**
     * @return table name (if it is for <em>MongoDB/em>, this name is not bound to the <em>Collection</em> name).
     */
    public String getName() {
        return name;
    }

    /**
     * Set table name.
     *
     * @param name
     *          Table name.
     */
    public void setName(String name) {
        this.name = name;
        if (StringUtils.isBlank(name)) {
            throw new ZestException(Messages.parseCommonAttrEmpty(name));
        }
    }

    /**
     * @return whether to ignore the verification of this table.
     */
    public boolean isIgnoreVerify() {
        return ignoreVerify;
    }

    /**
     * @return data in the table.
     */
    public List<T> getDataList() {
        return dataList;
    }

    /**
     * @return rules for verification.
     */
    public Map<String, AbstractRule> getRuleMap() {
        return ruleMap;
    }

    /**
     * Describe the sorting rules.
     */
    protected class Sort {

        private String field;
        private String direction;

        /**
         * Construct a new instance.
         *
         * @param field
         *          Sorted field.
         * @param direction
         *          Sorting direction.
         */
        public Sort(String field, String direction){
            this.field = field;
            this.direction = direction;
        }

        /**
         * @return sorted field.
         */
        public String getField() {
            return field;
        }

        /**
         * @return sorting direction.
         */
        public String getDirection() {
            return direction;
        }

    }
}
