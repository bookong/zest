/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * Can be verified by this rule. The actual content is a time range related to the current time.
 *
 * @author Jiang Xu
 */
public class FromCurrentTimeRule extends AbstractRule {

    private int min;
    private int max;
    private int unit;
    private int offset;

    /**
     * Construct a new instance of manual validation rules.
     *
     * @param field
     *          Field name corresponding to the validation rule.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param min
     *          The minimum value from the current time, which can be a negative number.
     * @param max
     *          The maximum value from the current time, which can be a negative number.
     * @param unit
     *          Time unit, the value can be:
     *          <ul>
     *              <li>{@code Calendar.DAY_OF_YEAR}</li>
     *              <li>{@code Calendar.DAY_OF_MONTH}</li>
     *              <li>{@code Calendar.DAY_OF_WEEK}</li>
     *              <li>{@code Calendar.DAY_OF_WEEK_IN_MONTH}</li>
     *              <li>{@code Calendar.HOUR_OF_DAY}</li>
     *              <li>{@code Calendar.HOUR}</li>
     *              <li>{@code Calendar.MINUTE}</li>
     *              <li>{@code Calendar.SECOND}</li>
     *          </ul>
     * @param offset
     *          The precision deviation of time, in milliseconds.
     */
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

    /**
     * Construct a new instance of automatic validation rules.
     *
     * @param node
     *          Related elements in the unit test data (*.xml).
     * @param field
     *          Field name corresponding to the validation rule.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     */
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

    /**
     * {@inheritDoc}
     */
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

            Date actualDate = getActualDataTime(actual);

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

    /**
     * @return the minimum value from the current time.
     */
    public int getMin() {
        return min;
    }

    /**
     * @return the maximum value from the current time, which can be a negative number.
     */
    public int getMax() {
        return max;
    }

    /**
     * @return time unit.
     */
    public int getUnit() {
        return unit;
    }

    /**
     * @return the precision deviation of time, in milliseconds.
     */
    public int getOffset() {
        return offset;
    }
}
