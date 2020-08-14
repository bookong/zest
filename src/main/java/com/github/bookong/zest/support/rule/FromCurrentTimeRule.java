package com.github.bookong.zest.support.rule;

import com.github.bookong.zest.testcase.sql.SqlDataSourceTable;
import com.github.bookong.zest.testcase.TestCaseData;
import com.github.bookong.zest.testcase.TestCaseDataSource;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;

import java.util.Calendar;

/**
 * @author jiangxu
 */
public class FromCurrentTimeRule extends AbstractRule {

    private int min;
    private int max;
    private int unit;
    private int offset;

    public FromCurrentTimeRule(String dataSourceId, String tableName, int rowIdx, String fieldName, Field xmlField){
        super(xmlField);
        min = xmlField.getFromCurrentTime().getMin();
        max = xmlField.getFromCurrentTime().getMax();
        offset = xmlField.getFromCurrentTime().getOffset();

        switch (xmlField.getFromCurrentTime().getUnit()) {
            case "day":
                unit = Calendar.DAY_OF_YEAR;
                break;
            case "hour":
                unit = Calendar.HOUR_OF_DAY;
                break;
            case "minute":
                unit = Calendar.MINUTE;
                break;
            case "second":
                unit = Calendar.SECOND;
                break;
            default:
                Assert.fail(Messages.checkTableColDateFromUnit(dataSourceId, tableName, rowIdx, fieldName,
                                                               xmlField.getFromCurrentTime().getUnit()));
        }
    }

    @Override
    public void assertIt(TestCaseData testCaseData, TestCaseDataSource dataSource, SqlDataSourceTable table, int rowIdx,
                         String columnName, Object value) {
        assertNullable(dataSource, table, rowIdx, columnName, value);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(testCaseData.getStartTime());
        cal.add(unit, min);
        cal.add(Calendar.MILLISECOND, -offset);
        long expectedMin = cal.getTimeInMillis();

        cal.setTimeInMillis(testCaseData.getEndTime());
        cal.add(unit, max);
        cal.add(Calendar.MILLISECOND, offset);
        long expectedMax = cal.getTimeInMillis();

        long tmp = getActualDataTime(dataSource, table, rowIdx, columnName, value);
        Assert.assertTrue(Messages.checkTableColDateFrom(dataSource.getId(), table.getName(), rowIdx, columnName),
                          (tmp >= expectedMin && tmp <= expectedMax));
    }
}
