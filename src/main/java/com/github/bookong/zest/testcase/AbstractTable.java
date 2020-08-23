package com.github.bookong.zest.testcase;

import com.github.bookong.zest.util.ZestXmlUtil;

import java.util.Map;

/**
 * 抽象的广义数据源广义的“表”
 * 
 * @author Jiang Xu
 */
public abstract class AbstractTable {

    /** 广义的表名 */
    private String  name;

    /** 是否不验证目标数据源的表，这个标识只在 Target 下的 Table 中才有效 */
    private boolean ignoreCheckTarget;

    protected void init(String nodeName, Map<String, String> attrMap) {
        this.name = ZestXmlUtil.removeAttr(nodeName, attrMap, "Name");
        this.ignoreCheckTarget = ZestXmlUtil.removeBooleanAttr(nodeName, attrMap, "Ignore", false);
        // TODO
    }

    public String getName() {
        return name;
    }

    public boolean isIgnoreCheckTarget() {
        return ignoreCheckTarget;
    }

}
