package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.w3c.dom.Node;


/**
 * @author Jiang Xu
 */
public class CurrentTimeRule extends AbstractRule {

    private int offset;

    CurrentTimeRule(Node node, String path, boolean nullable){
        super(path, nullable);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs(Xml.OFFSET);
        xmlNode.mustNoChildren();

        this.offset = xmlNode.getAttrInt(Xml.OFFSET, 1000);
    }

    @Override
    public void assertIt(ZestData zestData, Source source, Table table, int rowIdx, String columnName, Object value) {
        assertNullable(columnName, value);

        long tmp = getActualDataTime(source, table, rowIdx, columnName, value);
        Assert.assertTrue(Messages.checkTableColDateCurrent(source.getId(), table.getName(), rowIdx, columnName),
                          (tmp >= zestData.getStartTime() && tmp <= zestData.getEndTime() + getOffset()));

    }

    public int getOffset() {
        return offset;
    }
}
