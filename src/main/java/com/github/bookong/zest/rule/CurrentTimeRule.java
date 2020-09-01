package com.github.bookong.zest.rule;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.w3c.dom.Node;

/**
 * @author Jiang Xu
 */
public class CurrentTimeRule extends AbstractRule {

    private int offset;

    CurrentTimeRule(Node node, String field, boolean nullable){
        super(field, nullable);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs(Xml.OFFSET);
        xmlNode.mustNoChildren();

        this.offset = xmlNode.getAttrInt(Xml.OFFSET, 1000);
    }

    @Override
    public void verify(ZestData zestData, String field, Object actual) {
        assertNullable(field, actual);

        long tmp = getActualDataTime(field, actual);
        Assert.assertTrue(Messages.verifyRuleDateCurrent(field),
                          (tmp >= zestData.getStartTime() && tmp <= zestData.getEndTime() + getOffset()));

    }

    public int getOffset() {
        return offset;
    }
}
