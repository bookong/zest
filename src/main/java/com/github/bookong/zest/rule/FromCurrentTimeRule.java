package com.github.bookong.zest.rule;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Jiang Xu
 */
public class FromCurrentTimeRule extends AbstractRule {

    private int min;
    private int max;
    private int unit;
    private int offset;

    public FromCurrentTimeRule(String field, boolean nullable, int min, int max, int unit, int offset){
        super(field, nullable, true);
        this.min = min;
        this.max = max;

        switch (unit) {
            case Calendar.DAY_OF_YEAR:
            case Calendar.DAY_OF_MONTH:
            case Calendar.DAY_OF_WEEK:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
                this.unit = Calendar.DAY_OF_YEAR;
                break;
            case Calendar.HOUR_OF_DAY:
            case Calendar.HOUR:
                this.unit = Calendar.HOUR_OF_DAY;
                break;
            case Calendar.MINUTE:
                this.unit = Calendar.MINUTE;
                break;
            case Calendar.SECOND:
                this.unit = Calendar.SECOND;
                break;
            default:
                throw new ZestException(Messages.parseRuleManualFromUnitUnknown(unit));
        }

        this.offset = offset;
    }

    public FromCurrentTimeRule(Node node, String field, boolean nullable){
        super(field, nullable, false);
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

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(zestData.getStartTime());
            cal.add(getUnit(), getMin());
            cal.add(Calendar.MILLISECOND, -getOffset());
            Date startTime = cal.getTime();

            cal.setTimeInMillis(zestEndTime);
            cal.add(getUnit(), getMax());
            cal.add(Calendar.MILLISECOND, getOffset());
            Date endTime = cal.getTime();

            Assert.assertTrue(Messages.verifyRuleDateFrom(getField(), ZestDateUtil.formatDateNormal(startTime),
                                                          ZestDateUtil.formatDateNormal(endTime),
                                                          ZestDateUtil.formatDateNormal(actualDate)),
                              (actualDate.getTime() >= startTime.getTime()
                               && actualDate.getTime() <= endTime.getTime()));
        }
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
