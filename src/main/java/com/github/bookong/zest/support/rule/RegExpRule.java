package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Jiang Xu
 */
public class RegExpRule extends AbstractRule {

    private String regExp;

    RegExpRule(String nodeName, Node node, String path, boolean nullable){
        super(path, nullable);
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);
        List<Node> children = ZestXmlUtil.getElements(node.getChildNodes());

        this.regExp = ZestXmlUtil.getValue(node);

        ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
        ZestXmlUtil.mustHaveNoChildrenElements(nodeName, children);
    }

    @Override
    public void assertIt(ZestData zestData, Source source, Table table, int rowIdx, String columnName, Object value) {
        assertNullable(columnName, value);

        Assert.assertTrue(Messages.checkTableColRegexp(source.getId(), table.getName(), rowIdx, columnName,
                                                       getRegExp()),
                          Pattern.matches(getRegExp(), String.valueOf(value)));
    }

    public String getRegExp() {
        return regExp;
    }
}
