package com.github.bookong.zest.rule;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.w3c.dom.Node;

/**
 * @author Jiang Xu
 */
public class RuleFactory {

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
            default:
                throw new ZestException(Messages.parseRuleChoice());
        }
    }
}
