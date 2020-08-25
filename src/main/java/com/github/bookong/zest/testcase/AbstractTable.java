package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.rule.AbstractRule;
import com.github.bookong.zest.support.rule.RuleFactory;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
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

    protected abstract void loadSorts(List<Sort> sortList);

    protected abstract void checkRule(AbstractRule rule);

    protected abstract void loadData(ZestWorker worker, String sourceId, String content);

    protected void init(ZestWorker worker, String sourceId, String nodeName, List<Node> children,
                        Map<String, String> attrMap, boolean isVerifyElement) {
        this.name = ZestXmlUtil.removeNotEmptyAttr(nodeName, attrMap, Xml.NAME);
        this.ignoreVerify = ZestXmlUtil.removeBooleanAttr(nodeName, attrMap, Xml.IGNORE, false);
        this.dataList = new ArrayList<>(children.size() + 1);

        boolean parseSorts = false;
        boolean parseRules = false;
        boolean parseData = false;
        int dataIdx = 1;
        for (Node child : children) {
            if (Xml.SORTS.equals(child.getNodeName())) {
                if (!isVerifyElement) {
                    throw new ZestException(Messages.parseSortsPosition());
                }

                if (parseSorts || parseRules || parseData) {
                    throw new ZestException(Messages.parseSortsOrder());
                }
                parseSorts(child.getNodeName(), child);
                parseSorts = true;

            } else if (Xml.RULES.equals(child.getNodeName())) {
                if (parseRules || parseData) {
                    throw new ZestException(Messages.parseRulesPosition());
                }
                parseRules(child.getNodeName(), child);
                parseRules = true;

            } else if (Xml.DATA.equals(child.getNodeName())) {
                parseData(worker, sourceId, child, dataIdx++);
                parseData = true;

            } else {
                throw new ZestException(Messages.parseCommonChildrenUnknown(nodeName, child.getNodeName()));
            }
        }
    }

    private void parseSorts(String nodeName, Node node) {
        try {
            List<Node> children = ZestXmlUtil.getElements(node.getChildNodes());
            List<Sort> list = new ArrayList<>(children.size() + 1);

            ZestXmlUtil.attrMapMustEmpty(nodeName, ZestXmlUtil.getAllAttrs(node));

            Set<String> fieldNames = new HashSet<>(children.size() + 1);
            for (Node child : children) {
                if (!Xml.SORT.equals(child.getNodeName())) {
                    throw new ZestException(Messages.parseCommonChildrenList(nodeName, Xml.SORT));
                }
                list.add(parseSort(child.getNodeName(), child, fieldNames));
            }

            loadSorts(list);
        } catch (Exception e) {
            throw new ZestException(Messages.parseSortsError(), e);
        }
    }

    private void parseRules(String nodeName, Node node) {
        try {
            ZestXmlUtil.attrMapMustEmpty(nodeName, ZestXmlUtil.getAllAttrs(node));
            List<Node> children = ZestXmlUtil.getElements(node.getChildNodes());
            this.ruleList = new ArrayList<>(children.size() + 1);
            Set<String> rulePaths = new HashSet<>(children.size() + 1);
            for (Node child : children) {
                if (!Xml.RULE.equals(child.getNodeName())) {
                    throw new ZestException(Messages.parseCommonChildrenList(Xml.RULES, Xml.RULE));
                }

                this.ruleList.add(parseRule(child.getNodeName(), child, rulePaths));
            }
        } catch (Exception e) {
            throw new ZestException(Messages.parseRulesError(), e);
        }
    }

    private void parseData(ZestWorker worker, String sourceId, Node node, int dataIdx) {
        try {
            loadData(worker, sourceId, ZestXmlUtil.getValue(node));
        } catch (Exception e) {
            throw new ZestException(Messages.parseDataError(dataIdx), e);
        }
    }

    private Sort parseSort(String nodeName, Node node, Set<String> fieldNames) {
        String fieldName = null;
        try {
            List<Node> sortChildren = ZestXmlUtil.getElements(node.getChildNodes());
            Map<String, String> sortAttrMap = ZestXmlUtil.getAllAttrs(node);

            fieldName = ZestXmlUtil.removeNotEmptyAttr(nodeName, sortAttrMap, Xml.FIELD);
            String direction = ZestXmlUtil.removeAttr(nodeName, sortAttrMap, Xml.DIRECTION, Xml.ASC);
            if (!Xml.ASC.equals(direction) && !Xml.DESC.equals(direction)) {
                throw new ZestException(Messages.parseSortDirection());
            }

            ZestXmlUtil.mustHaveNoChildrenElements(Xml.SORT, sortChildren);
            ZestXmlUtil.duplicateCheck(Xml.FIELD, fieldNames, fieldName);

            return new Sort(fieldName, direction);
        } catch (Exception e) {
            throw new ZestException(Messages.parseSortError(fieldName), e);
        }
    }

    private AbstractRule parseRule(String nodeName, Node node, Set<String> rulePaths) {
        String path = null;
        try {
            Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);
            path = ZestXmlUtil.removeAttr(nodeName, attrMap, Xml.PATH);
            AbstractRule rule = RuleFactory.create(node);
            ZestXmlUtil.duplicateCheck(Xml.PATH, rulePaths, rule.getPath());
            checkRule(rule);
            return rule;
        } catch (Exception e) {
            throw new ZestException(Messages.parseRuleError(path), e);
        }
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
