package com.github.bookong.zest.testcase.data.rule;

import com.github.bookong.zest.rule.CurrentTimeRule;
import com.github.bookong.zest.rule.FromCurrentTimeRule;
import com.github.bookong.zest.rule.RegExpRule;
import com.github.bookong.zest.testcase.SourceVerifyData;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    @Test
    public void testLoad01() {
        logger.info("Normal data");
        ZestData zestData = load("01.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());
        Assert.assertTrue(obj.getTableMap().get("tab") instanceof Table);
        Table table = (Table) obj.getTableMap().get("tab");
        Assert.assertEquals(3, table.getRuleMap().size());

        RegExpRule regExpRule = (RegExpRule) table.getRuleMap().get("f_varchar");
        Assert.assertEquals("f_varchar", regExpRule.getPath());
        Assert.assertEquals("^[0-9]*$", regExpRule.getRegExp());

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleMap().get("f_time");
        Assert.assertEquals("f_time", currentTimeRule.getPath());
        Assert.assertEquals(1000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleMap().get("f_date");
        Assert.assertEquals("f_date", fromCurrentTimeRule.getPath());
        Assert.assertEquals(1000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.DAY_OF_YEAR, fromCurrentTimeRule.getUnit());
    }

    @Test
    public void testLoad02() {
        logger.info("Normal data");
        ZestData zestData = load("02.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());
        Collection table = (Collection) obj.getTableMap().get("tab");
        Assert.assertEquals(3, table.getRuleMap().size());

        RegExpRule regExpRule = (RegExpRule) table.getRuleMap().get("obj.str");
        Assert.assertEquals("obj.str", regExpRule.getPath());
        Assert.assertEquals("^[0-9]*$", regExpRule.getRegExp());

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleMap().get("date1");
        Assert.assertEquals("date1", currentTimeRule.getPath());
        Assert.assertEquals(1000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleMap().get("date2");
        Assert.assertEquals("date2", fromCurrentTimeRule.getPath());
        Assert.assertEquals(1000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.DAY_OF_YEAR, fromCurrentTimeRule.getUnit());
    }

    @Test
    public void testLoad03() {
        logger.info("Normal data");
        ZestData zestData = load("03.xml");
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Table table = (Table) obj.getTableMap().get("tab");

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleMap().get("f_time");
        Assert.assertEquals("f_time", currentTimeRule.getPath());
        Assert.assertEquals(2000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleMap().get("f_date");
        Assert.assertEquals("f_date", fromCurrentTimeRule.getPath());
        Assert.assertEquals(2000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.HOUR_OF_DAY, fromCurrentTimeRule.getUnit());
    }

    @Test
    public void testLoad04() {
        testLoadError("04.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_char"), //
                      Messages.parseRuleChoice());
    }

    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError(""), //
                      Messages.parseCommonAttrEmpty("Path"));
    }

    @Test
    public void testLoad06() {
        testLoadError("06.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseRuleChoice());
    }

    @Test
    public void testLoad07() {
        testLoadError("07.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonAttr("CurrentTime", "Offset"), //
                      "For input string: \"abc\"");
    }

    @Test
    public void testLoad08() {
        testLoadError("08.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonAttrUnknown("CurrentTime", "U"));
    }

    @Test
    public void testLoad09() {
        testLoadError("09.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonChildren("CurrentTime"));
    }

    @Test
    public void testLoad10() {
        testLoadError("10.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonAttr("FromCurrentTime", "Max"), //
                      "For input string: \"abc\"");
    }

    @Test
    public void testLoad11() {
        testLoadError("11.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseRuleFromUnitUnknown("none"));
    }

    @Test
    public void testLoad12() {
        testLoadError("12.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonAttrUnknown("FromCurrentTime", "U"));
    }

    @Test
    public void testLoad13() {
        testLoadError("13.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonChildren("FromCurrentTime"));
    }

    @Test
    public void testLoad14() {
        testLoadError("14.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonAttrUnknown("RegExp", "U"));
    }

    @Test
    public void testLoad15() {
        testLoadError("15.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonChildren("RegExp"));
    }
}
