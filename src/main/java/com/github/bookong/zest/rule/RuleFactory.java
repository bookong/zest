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
import com.github.bookong.zest.util.Messages;
import org.w3c.dom.Node;

/**
 * Factory class for creating validation rules.
 *
 * @author Jiang Xu
 */
public class RuleFactory {

    /**
     * Construct a new instance of automatic validation rules.
     *
     * @param xmlNode
     *          Related elements in the unit test data (*.xml).
     * @param field
     *          Field name corresponding to the validation rule.
     * @return a new instance.
     */
    public static AbstractRule create(XmlNode xmlNode, String field) {
        if (xmlNode.getChildren().size() != 1) {
            throw new ZestException(Messages.parseRuleChoice());
        }

        boolean nullable = xmlNode.getAttrBoolean(Xml.NULLABLE, false);

        Node childNode = xmlNode.getChildren().get(0);
        String childName = childNode.getNodeName();
        switch (childName) {
            case Xml.REG_EXP:
                return new RegExpRule(childNode, field, nullable);
            case Xml.CURRENT_TIME:
                return new CurrentTimeRule(childNode, field, nullable);
            case Xml.FROM_CURRENT_TIME:
                return new FromCurrentTimeRule(childNode, field, nullable);
            case Xml.RANGE:
                return new RangeRule(childNode, field, nullable);
            default:
                throw new ZestException(Messages.parseRuleChoice());
        }
    }
}
