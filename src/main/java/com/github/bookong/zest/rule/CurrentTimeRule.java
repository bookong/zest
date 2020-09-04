package com.github.bookong.zest.rule;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * @author Jiang Xu
 */
public class CurrentTimeRule extends AbstractRule {

    private int offset;

    public CurrentTimeRule(String field, boolean nullable, int offset){
        super(field, nullable, true);
        this.offset = offset;
    }

    public CurrentTimeRule(Node node, String field, boolean nullable){
        super(field, nullable, false);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs(Xml.OFFSET);
        xmlNode.mustNoChildren();

        this.offset = xmlNode.getAttrInt(Xml.OFFSET, 1000);
    }

    @Override
    public void verify(ZestData zestData, Object actual) {
        if (actual == null) {
            if (!isNullable()) {
                Assert.fail(Messages.verifyRuleNotNull(getField()));
            }
        } else {
            long zestEndTime = zestData.getEndTime();
            if (isManual()) {
                zestEndTime = System.currentTimeMillis();
            }

            Date actualDate = getActualDataTime(getField(), actual);
            Date startTime = new Date(zestData.getStartTime());
            Date endTime = new Date(zestEndTime + getOffset());

            Assert.assertTrue(Messages.verifyRuleDateCurrent(getField(), ZestDateUtil.formatDateNormal(startTime),
                                                             ZestDateUtil.formatDateNormal(endTime),
                                                             ZestDateUtil.formatDateNormal(actualDate)),
                              (actualDate.getTime() >= startTime.getTime()
                               && actualDate.getTime() <= endTime.getTime()));
        }
    }

    public int getOffset() {
        return offset;
    }
}
