package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;

/**
 * @author jiangxu
 */
public class CurrentTimeRule extends AbstractRule {

    private int offset;

    public CurrentTimeRule(Field xmlField){
        super(xmlField);
        this.offset = xmlField.getCurrentTime().getOffset();
    }

    @Override
    public void assertIt(ZestData testCaseData, Source dataSource, Table table, int rowIdx,
                         String columnName, Object value) {
        assertNullable(dataSource, table, rowIdx, columnName, value);

        long tmp = getActualDataTime(dataSource, table, rowIdx, columnName, value);
        Assert.assertTrue(Messages.checkTableColdateCurrent(dataSource.getId(), table.getName(), rowIdx, columnName),
                          (tmp >= testCaseData.getStartTime() && tmp <= testCaseData.getEndTime() + offset));

    }
}
