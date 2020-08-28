package com.github.bookong.zest.support.rule;

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

    CurrentTimeRule(Node node, String path, boolean nullable){
        super(path, nullable);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs(Xml.OFFSET);
        xmlNode.mustNoChildren();

        this.offset = xmlNode.getAttrInt(Xml.OFFSET, 1000);
    }

    @Override
    public void verify(ZestData zestData, String path, Object actual) {
        assertNullable(path, actual);

        long tmp = getActualDataTime(path, actual);
        Assert.assertTrue(Messages.verifyRuleDateCurrent(path),
                          (tmp >= zestData.getStartTime() && tmp <= zestData.getEndTime() + getOffset()));

    }

    public int getOffset() {
        return offset;
    }
}
