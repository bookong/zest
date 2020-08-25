package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class FromCurrentTimeRule extends AbstractRule {

    private int min;
    private int max;
    private int unit;
    private int offset;

    FromCurrentTimeRule(String nodeName, Node node, String path, boolean nullable){
        super(path, nullable);
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);
        List<Node> children = ZestXmlUtil.getElements(node.getChildNodes());

        this.min = ZestXmlUtil.removeNotNullIntAttr(nodeName, attrMap, "Min");
        this.max = ZestXmlUtil.removeNotNullIntAttr(nodeName, attrMap, "Max");
        String str = ZestXmlUtil.removeNotEmptyAttr(nodeName, attrMap, "Unit");
        this.offset = ZestXmlUtil.removeIntAttr(nodeName, attrMap, "Offset", 1000);

        switch (str) {
            case "day":
                unit = Calendar.DAY_OF_YEAR;
                break;
            case "hour":
                unit = Calendar.HOUR_OF_DAY;
                break;
            case "minute":
                unit = Calendar.MINUTE;
                break;
            case "second":
                unit = Calendar.SECOND;
                break;
            default:
                throw new ZestException(Messages.parseRuleFromUnitUnknown(str));
        }

        ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
        ZestXmlUtil.mustHaveNoChildrenElements(nodeName, children);
    }

    @Override
    public void assertIt(ZestData testCaseData, Source dataSource, Table table, int rowIdx, String columnName,
                         Object value) {
        assertNullable(dataSource, table, rowIdx, columnName, value);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(testCaseData.getStartTime());
        cal.add(getUnit(), getMin());
        cal.add(Calendar.MILLISECOND, -getOffset());
        long expectedMin = cal.getTimeInMillis();

        cal.setTimeInMillis(testCaseData.getEndTime());
        cal.add(getUnit(), getMax());
        cal.add(Calendar.MILLISECOND, getOffset());
        long expectedMax = cal.getTimeInMillis();

        long tmp = getActualDataTime(dataSource, table, rowIdx, columnName, value);
        Assert.assertTrue(Messages.checkTableColDateFrom(dataSource.getId(), table.getName(), rowIdx, columnName),
                          (tmp >= expectedMin && tmp <= expectedMax));
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getUnit() {
        return unit;
    }

    public int getOffset() {
        return offset;
    }
}
