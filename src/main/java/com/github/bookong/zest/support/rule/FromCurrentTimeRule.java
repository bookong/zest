package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.Calendar;

/**
 * @author Jiang Xu
 */
public class FromCurrentTimeRule extends AbstractRule {

    private int min;
    private int max;
    private int unit;
    private int offset;

    FromCurrentTimeRule(Node node, String path, boolean nullable){
        super(path, nullable);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs(Xml.MIN, Xml.MAX, Xml.UNIT, Xml.OFFSET);
        xmlNode.mustNoChildren();

        this.min = xmlNode.getAttrInt(Xml.MIN);
        this.max = xmlNode.getAttrInt(Xml.MAX);
        this.offset = xmlNode.getAttrInt(Xml.OFFSET, 1000);

        String str = xmlNode.getAttrNotEmpty(Xml.UNIT);
        switch (str) {
            case Xml.DAY:
                unit = Calendar.DAY_OF_YEAR;
                break;
            case Xml.HOUR:
                unit = Calendar.HOUR_OF_DAY;
                break;
            case Xml.MINUTE:
                unit = Calendar.MINUTE;
                break;
            case Xml.SECOND:
                unit = Calendar.SECOND;
                break;
            default:
                throw new ZestException(Messages.parseRuleFromUnitUnknown(str));
        }
    }

    @Override
    public void assertIt(ZestData zestData, Source source, Table table, int rowIdx, String columnName, Object value) {
        assertNullable(columnName, value);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(zestData.getStartTime());
        cal.add(getUnit(), getMin());
        cal.add(Calendar.MILLISECOND, -getOffset());
        long expectedMin = cal.getTimeInMillis();

        cal.setTimeInMillis(zestData.getEndTime());
        cal.add(getUnit(), getMax());
        cal.add(Calendar.MILLISECOND, getOffset());
        long expectedMax = cal.getTimeInMillis();

        long tmp = getActualDataTime(source, table, rowIdx, columnName, value);
        Assert.assertTrue(Messages.checkTableColDateFrom(source.getId(), table.getName(), rowIdx, columnName),
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
