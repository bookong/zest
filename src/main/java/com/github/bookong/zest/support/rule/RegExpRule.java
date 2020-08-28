package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.regex.Pattern;

/**
 * @author Jiang Xu
 */
public class RegExpRule extends AbstractRule {

    private String regExp;

    RegExpRule(Node node, String path, boolean nullable){
        super(path, nullable);
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs();
        xmlNode.mustNoChildren();

        this.regExp = xmlNode.getNodeValue();
    }

    @Override
    public void verify(ZestData zestData, String path, Object actual) {
        assertNullable(path, actual);

        Assert.assertTrue(Messages.verifyRuleRegExp(path, getRegExp()),
                          Pattern.matches(getRegExp(), String.valueOf(actual)));
    }

    public String getRegExp() {
        return regExp;
    }
}
