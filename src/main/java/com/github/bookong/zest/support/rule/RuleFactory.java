package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class RuleFactory {

    public static AbstractRule create(Node ruleNode) {
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(ruleNode);
        List<Node> elements = ZestXmlUtil.getElements(ruleNode.getChildNodes());

        if (elements.size() != 1) {
            throw new ZestException(Messages.parseRuleChoice());
        }

        String path = ZestXmlUtil.removeNotEmptyAttr(Xml.RULE, attrMap, Xml.PATH);
        boolean nullable = ZestXmlUtil.removeBooleanAttr(Xml.RULE, attrMap, Xml.NULLABLE, true);
        ZestXmlUtil.attrMapMustEmpty(Xml.RULE, attrMap);

        Node childNode = elements.get(0);
        String childName = childNode.getNodeName();
        switch (childName) {
            case Xml.REG_EXP:
                return new RegExpRule(childName, childNode, path, nullable);
            case Xml.CURRENT_TIME:
                return new CurrentTimeRule(childName, childNode, path, nullable);
            case Xml.FROM_CURRENT_TIME:
                return new FromCurrentTimeRule(childName, childNode, path, nullable);
            default:
                throw new ZestException(Messages.parseRuleChoice());
        }
    }
}
