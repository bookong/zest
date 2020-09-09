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

import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.regex.Pattern;

/**
 * Can be verified by this rule. The actual data must match the specified regular expression.
 *
 * @author Jiang Xu
 */
public class RegExpRule extends AbstractRule {

    private String regExp;

    /**
     * Construct a new instance of manual validation rules.
     *
     * @param field
     *          Field name corresponding to the validation rule.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param regExp
     *          Regular expression.
     */
    public RegExpRule(String field, boolean nullable, String regExp){
        super(field, nullable, true);
        this.regExp = regExp;
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
    public RegExpRule(Node node, String field, boolean nullable){
        super(field, nullable, false);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs();
        xmlNode.mustNoChildren();

        this.regExp = xmlNode.getNodeValue();
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
            String actualValue = String.valueOf(actual);
            Assert.assertTrue(Messages.verifyRuleRegExp(getField(), getRegExp(), actualValue),
                              Pattern.matches(getRegExp(), String.valueOf(actual)));
        }
    }

    /**
     * @return regular expression.
     */
    public String getRegExp() {
        return regExp;
    }
}
