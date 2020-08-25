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

/**
 * @author Jiang Xu
 */
public class CurrentTimeRule extends AbstractRule {

    private int offset;

    CurrentTimeRule(String nodeName, Node node, String path, boolean nullable){
        super(path, nullable);
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);
        List<Node> children = ZestXmlUtil.getElements(node.getChildNodes());

        this.offset = ZestXmlUtil.removeIntAttr(nodeName, attrMap, "Offset", 1000);

        ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
        ZestXmlUtil.mustHaveNoChildrenElements(nodeName, children);
    }

    @Override
    public void assertIt(ZestData testCaseData, Source dataSource, Table table, int rowIdx, String columnName,
                         Object value) {
        assertNullable(dataSource, table, rowIdx, columnName, value);

        long tmp = getActualDataTime(dataSource, table, rowIdx, columnName, value);
        Assert.assertTrue(Messages.checkTableColDateCurrent(dataSource.getId(), table.getName(), rowIdx, columnName),
                          (tmp >= testCaseData.getStartTime() && tmp <= testCaseData.getEndTime() + getOffset()));

    }

    public int getOffset() {
        return offset;
    }
}
