package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Table;
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
    public void assertIt(ZestData zestData, Source source, Table table, int rowIdx, String columnName, Object value) {
        assertNullable(columnName, value);

        Assert.assertTrue(Messages.checkTableColRegexp(source.getId(), table.getName(), rowIdx, columnName,
                                                       getRegExp()),
                          Pattern.matches(getRegExp(), String.valueOf(value)));
    }

    public String getRegExp() {
        return regExp;
    }
}
