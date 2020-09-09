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
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Can be verified by this rule. The actual content is the current time.
 *
 * @author Jiang Xu
 */
public class CurrentTimeRule extends AbstractRule {

    private int offset;

    /**
     * Construct a new instance of manual validation rules.
     *
     * @param field
     *          Field name corresponding to the validation rule.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param offset
     *          The precision deviation of time, in milliseconds.
     */
    public CurrentTimeRule(String field, boolean nullable, int offset){
        super(field, nullable, true);
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
    public CurrentTimeRule(Node node, String field, boolean nullable){
        super(field, nullable, false);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs(Xml.OFFSET);
        xmlNode.mustNoChildren();

        this.offset = xmlNode.getAttrInt(Xml.OFFSET, 1000);
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
            Date startTime = new Date(zestData.getStartTime());
            Date endTime = new Date(zestEndTime + getOffset());

            Assert.assertTrue(Messages.verifyRuleDateCurrent(getField(), ZestDateUtil.formatDateNormal(startTime),
                                                             ZestDateUtil.formatDateNormal(endTime),
                                                             ZestDateUtil.formatDateNormal(actualDate)),
                              (actualDate.getTime() >= startTime.getTime()
                               && actualDate.getTime() <= endTime.getTime()));
        }
    }

    /**
     * @return the precision deviation of time, in milliseconds.
     */
    public int getOffset() {
        return offset;
    }
}
