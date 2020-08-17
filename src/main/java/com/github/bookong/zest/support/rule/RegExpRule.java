package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.regex.Pattern;

/**
 * @author Jiang Xu
 */
public class RegExpRule extends AbstractRule {

    private String regExp;

    public RegExpRule(Field xmlField){
        super(xmlField);
        regExp = StringUtils.trimToEmpty(xmlField.getRegExp());
    }

    @Override
    public void assertIt(ZestData testCaseData, Source dataSource, Table table, int rowIdx,
                         String columnName, Object value) {
        assertNullable(dataSource, table, rowIdx, columnName, value);

        Assert.assertTrue(Messages.checkTableColRegexp(dataSource.getId(), table.getName(), rowIdx, columnName, regExp),
                          Pattern.matches(regExp, String.valueOf(value)));
    }
}
