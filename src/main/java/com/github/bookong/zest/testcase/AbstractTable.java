package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.support.rule.AbstractRule;
import com.github.bookong.zest.support.rule.RuleFactory;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.util.*;

/**
 * 抽象的广义数据源广义的“表”
 * 
 * @author Jiang Xu
 */
public abstract class AbstractTable<T extends AbstractRowData> {

    private List<T>            dataList;

    private List<AbstractRule> ruleList;

    /** 广义的表名 */
    private String             name;

    /** 是否不验证目标数据源的表，这个标识只在 Target 下的 Table 中才有效 */
    private boolean            ignoreVerify;

    protected void init(String nodeName, List<Node> elements, Map<String, String> attrMap) {
        this.name = ZestXmlUtil.removeAttr(nodeName, attrMap, "Name");
        this.ignoreVerify = ZestXmlUtil.removeBooleanAttr(nodeName, attrMap, "Ignore", false);

        dataList = new ArrayList<>(elements.size() + 1);
    }

    protected void parseData(List<Node> elements, boolean isVerifyElement) {
        try {
            for (Node node : elements) {
                if (!"Data".equals(node.getNodeName())) {
                    continue;
                }

                ZestXmlUtil.attrMapMustEmpty("Data", ZestXmlUtil.getAllAttrs(node));
                List<Node> subElements = ZestXmlUtil.getElements(node.getChildNodes());
                if (subElements.size() == 1) {
                    if (!"Value".equals(subElements.get(0).getNodeName())) {
                        throw new ZestException(Messages.parseDataValueExist());
                    }
                    parseValue(subElements.get(0));

                } else if (subElements.size() == 2) {
                    if (!isVerifyElement) {
                        throw new ZestException(Messages.parseDataRulesPosition());
                    }

                    if (!"Rules".equals(subElements.get(0).getNodeName())) {
                        throw new ZestException(Messages.parseDataInclude());
                    }

                    if (!"Value".equals(subElements.get(1).getNodeName())) {
                        throw new ZestException(Messages.parseDataInclude());
                    }

                    parseRules(subElements.get(0));
                    parseValue(subElements.get(1));

                } else {
                    throw new ZestException(Messages.parseDataInclude());
                }
            }
        } catch (Exception e) {
            throw new ZestException(Messages.parseDataError(), e);
        }
    }

    protected abstract void checkRule(AbstractRule rule);

    private void parseRules(Node rulesNode) {
        try {
            ZestXmlUtil.attrMapMustEmpty("Rules", ZestXmlUtil.getAllAttrs(rulesNode));
            List<Node> elements = ZestXmlUtil.getElements(rulesNode.getChildNodes());
            this.ruleList = new ArrayList<>(elements.size() + 1);
            Set<String> rulePaths = new HashSet<>(elements.size() + 1);
            for (Node element : elements) {
                if (!"Rule".equals(element.getNodeName())) {
                    throw new ZestException(Messages.parseRuleType());
                }

                AbstractRule rule = RuleFactory.create(element);
                if (rulePaths.contains(rule.getPath())) {
                    throw new ZestException(Messages.parseRulePathDuplicate(rule.getPath()));
                }
                rulePaths.add(rule.getPath());
                checkRule(rule);
                this.ruleList.add(rule);
            }
        } catch (Exception e) {
            throw new ZestException(Messages.parseRulesError(), e);
        }
    }

    private void parseValue(Node valueNode) {
        // TODO
    }

    protected List<Sort> parseSort(Node elementSorts) {
        List<Node> elements = ZestXmlUtil.getElements(elementSorts.getChildNodes());
        List<Sort> list = new ArrayList<>(elements.size() + 1);

        ZestXmlUtil.attrMapMustEmpty("Sorts", ZestXmlUtil.getAllAttrs(elementSorts));

        Set<String> fieldNames = new HashSet<>(elements.size() + 1);
        for (Node sortElement : elements) {
            if (!"Sort".equals(sortElement.getNodeName())) {
                throw new ZestException(Messages.parseSortType());
            }

            List<Node> sortSubElements = ZestXmlUtil.getElements(sortElement.getChildNodes());
            Map<String, String> sortAttrMap = ZestXmlUtil.getAllAttrs(sortElement);

            String fieldName = ZestXmlUtil.removeAttr(sortElement.getNodeName(), sortAttrMap, "Field");
            if (StringUtils.isBlank(fieldName)) {
                throw new ZestException(Messages.parseSortField());
            }

            String direction = ZestXmlUtil.removeAttr(sortElement.getNodeName(), sortAttrMap, "Direction");
            direction = StringUtils.isBlank(direction) ? "asc" : direction;
            if (!"asc".equals(direction) && !"desc".equals(direction)) {
                throw new ZestException(Messages.parseSortDirection(fieldName));
            }

            if (!sortSubElements.isEmpty()) {
                throw new ZestException(Messages.parseSortChildren(fieldName));
            }

            if (fieldNames.contains(fieldName)) {
                throw new ZestException(Messages.parseSortFieldDuplicate(fieldName));
            }
            fieldNames.add(fieldName);

            list.add(new Sort(fieldName, direction));
        }

        return list;
    }

    public String getName() {
        return name;
    }

    public boolean isIgnoreVerify() {
        return ignoreVerify;
    }

    public List<T> getDataList() {
        return dataList == null ? Collections.emptyList() : dataList;
    }

    public List<AbstractRule> getRuleList() {
        return ruleList == null ? Collections.emptyList() : ruleList;
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
