package com.github.bookong.zest.support.rule;

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

        String path = ZestXmlUtil.removeAttr("Rule", attrMap, "Path");
        if (StringUtils.isBlank(path)) {
            throw new ZestException(Messages.parseCommonAttrEmpty("Path"));
        }
        boolean nullable = ZestXmlUtil.removeBooleanAttr("Rule", attrMap, "Nullable", true);
        ZestXmlUtil.attrMapMustEmpty("Rule", attrMap);

        Node childNode = elements.get(0);
        String childName = childNode.getNodeName();
        switch (childName) {
            case "RegExp":
                return new RegExpRule(childName, childNode, path, nullable);
            case "CurrentTime":
                return new CurrentTimeRule(childName, childNode, path, nullable);
            case "FromCurrentTime":
                return new FromCurrentTimeRule(childName, childNode, path, nullable);
            default:
                throw new ZestException(Messages.parseRuleChoice());
        }
    }
}
