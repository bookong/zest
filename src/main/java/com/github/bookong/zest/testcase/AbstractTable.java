package com.github.bookong.zest.testcase;

import com.github.bookong.zest.util.ZestXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 抽象的广义数据源广义的“表”
 * 
 * @author Jiang Xu
 */
public abstract class AbstractTable<T> {

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
}
