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
import org.junit.Assert;
import org.w3c.dom.Node;

/**
 * Can be verified by this rule. The actual data must within a range of values.
 *
 * @author Jiang Xu
 */
public class RangeRule extends AbstractRule {
    
    private Double  from;
    private boolean includeFrom;
    private Double  to;
    private boolean includeTo;

    /**
     * Construct a new instance of manual validation rules.
     * 
     * @param field
     *          Field name corresponding to the validation rule.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param from
     *          The start value of the numerical range.
     * @param includeFrom
     *          Whether to include the start value.
     * @param to
     *          The end value of the numerical range.
     * @param includeTo
     *          Whether to include the end value.
     */
    public RangeRule(String field, boolean nullable, Double from, boolean includeFrom, Double to, boolean includeTo){
        super(field, nullable, true);

        this.from = from;
        this.includeFrom = includeFrom;
        this.to = to;
        this.includeTo = includeTo;
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
    public RangeRule(Node node, String field, boolean nullable){
        super(field, nullable, false);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs(Xml.FROM, Xml.INCLUDE_FROM, Xml.TO, Xml.INCLUDE_TO);

        this.from = xmlNode.getAttrDoubleObj(Xml.FROM);
        this.includeFrom = xmlNode.getAttrBoolean(Xml.INCLUDE_FROM, true);
        this.to = xmlNode.getAttrDoubleObj(Xml.TO);
        this.includeTo = xmlNode.getAttrBoolean(Xml.INCLUDE_TO, true);
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
            Assert.assertTrue(Messages.verifyRuleMustNumber(getField(), String.valueOf(actual)),
                              actual instanceof Number);

            double value = ((Number) actual).doubleValue();
            if (getFrom() != null) {
                if (isIncludeFrom()) {
                    Assert.assertTrue(Messages.verifyRuleGte(getField(), String.valueOf(from), String.valueOf(value)),
                                      getFrom() >= value);
                } else {
                    Assert.assertTrue(Messages.verifyRuleGt(getField(), String.valueOf(from), String.valueOf(value)),
                                      getFrom() > value);
                }
            }

            if (getTo() != null) {
                if (isIncludeTo()) {
                    Assert.assertTrue(Messages.verifyRuleLte(getField(), String.valueOf(from), String.valueOf(value)),
                                      getTo() <= value);
                } else {
                    Assert.assertTrue(Messages.verifyRuleLt(getField(), String.valueOf(from), String.valueOf(value)),
                                      getTo() < value);
                }
            }
        }
    }

    /**
     * @return the start value of the numerical range.
     */
    public Double getFrom() {
        return from;
    }

    /**
     * @return whether to include the start value.
     */
    public boolean isIncludeFrom() {
        return includeFrom;
    }

    /**
     * @return the end value of the numerical range.
     */
    public Double getTo() {
        return to;
    }

    /**
     * @return whether to include the end value.
     */
    public boolean isIncludeTo() {
        return includeTo;
    }
}
