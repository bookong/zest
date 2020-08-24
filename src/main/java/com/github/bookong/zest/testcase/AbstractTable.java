package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
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

    private List<T> dataList;

    /** 广义的表名 */
    private String  name;

    /** 是否不验证目标数据源的表，这个标识只在 Target 下的 Table 中才有效 */
    private boolean ignoreVerify;

    protected void init(String nodeName, List<Node> elements, Map<String, String> attrMap, boolean isTargetData) {
        this.name = ZestXmlUtil.removeAttr(nodeName, attrMap, "Name");
        this.ignoreVerify = ZestXmlUtil.removeBooleanAttr(nodeName, attrMap, "Ignore", false);

        dataList = new ArrayList<>(elements.size() + 1);
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
        if (dataList == null) {
            return Collections.emptyList();
        }
        return dataList;
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
