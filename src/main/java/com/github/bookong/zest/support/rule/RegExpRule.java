package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.core.testcase.SqlDataSourceTable;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.regex.Pattern;

/**
 * @author jiangxu
 */
public class RegExpRule extends AbstractRule {

    private String regExp;

    public RegExpRule(Field xmlField){
        super(xmlField);
        regExp = StringUtils.trimToEmpty(xmlField.getRegExp());
    }

    @Override
    public void assertIt(TestCaseData testCaseData, TestCaseDataSource dataSource, SqlDataSourceTable table, int rowIdx,
                         String columnName, Object value) {
        assertNullable(dataSource, table, rowIdx, columnName, value);

        Assert.assertTrue(Messages.checkTableColRegexp(dataSource.getId(), table.getName(), rowIdx, columnName, regExp),
                          Pattern.matches(regExp, String.valueOf(value)));
    }
}
