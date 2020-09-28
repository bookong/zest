package com.github.bookong.zest.testcase.data.rule;

import com.github.bookong.zest.rule.CurrentTimeRule;
import com.github.bookong.zest.rule.FromCurrentTimeRule;
import com.github.bookong.zest.rule.RangeRule;
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
 * 测试元素 Zest/Sources/Source/Verify/Rules/Rule
 * 
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    /** mysql 类型，正常例子，默认属性 */
    @Test
    public void testLoad01() {
        ZestData zestData = load("01.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());
        Assert.assertTrue(obj.getTableMap().get("tab") instanceof Table);
        Table table = (Table) obj.getTableMap().get("tab");
        Assert.assertEquals(4, table.getRuleMap().size());

        RegExpRule regExpRule = (RegExpRule) table.getRuleMap().get("f_varchar");
        Assert.assertEquals("f_varchar", regExpRule.getField());
        Assert.assertEquals("^[0-9]*$", regExpRule.getRegExp());

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleMap().get("f_time");
        Assert.assertEquals("f_time", currentTimeRule.getField());
        Assert.assertEquals(1000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleMap().get("f_date");
        Assert.assertEquals("f_date", fromCurrentTimeRule.getField());
        Assert.assertEquals(1000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.DAY_OF_YEAR, fromCurrentTimeRule.getUnit());

        RangeRule rangeRule = (RangeRule) table.getRuleMap().get("f_integer");
        Assert.assertEquals("f_integer", rangeRule.getField());
        Assert.assertEquals(1.0F, rangeRule.getFrom(), 0.01F);
        Assert.assertTrue(rangeRule.isIncludeFrom());
        Assert.assertEquals(12.5F, rangeRule.getTo(), 0.01F);
        Assert.assertTrue(rangeRule.isIncludeTo());
    }

    /** mongo 类型，正常例子，默认属性 */
    @Test
    public void testLoad02() {
        ZestData zestData = load("02.xml");
        Assert.assertEquals(1, zestData.getSourceList().size());
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Assert.assertEquals(1, obj.getTableMap().size());
        Collection table = (Collection) obj.getTableMap().get("tab");
        Assert.assertEquals(4, table.getRuleMap().size());

        RegExpRule regExpRule = (RegExpRule) table.getRuleMap().get("strValue");
        Assert.assertEquals("strValue", regExpRule.getField());
        Assert.assertEquals("^[0-9]*$", regExpRule.getRegExp());

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleMap().get("date1");
        Assert.assertEquals("date1", currentTimeRule.getField());
        Assert.assertEquals(1000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleMap().get("date2");
        Assert.assertEquals("date2", fromCurrentTimeRule.getField());
        Assert.assertEquals(1000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.DAY_OF_YEAR, fromCurrentTimeRule.getUnit());

        RangeRule rangeRule = (RangeRule) table.getRuleMap().get("longValue");
        Assert.assertEquals("longValue", rangeRule.getField());
        Assert.assertEquals(1.0F, rangeRule.getFrom(), 0.01F);
        Assert.assertTrue(rangeRule.isIncludeFrom());
        Assert.assertEquals(12.5F, rangeRule.getTo(), 0.01F);
        Assert.assertTrue(rangeRule.isIncludeTo());
    }

    /** mysql 类型，正常例子，指定属性 */
    @Test
    public void testLoad03() {
        ZestData zestData = load("03.xml");
        SourceVerifyData obj = zestData.getSourceList().get(0).getVerifyData();
        Table table = (Table) obj.getTableMap().get("tab");

        CurrentTimeRule currentTimeRule = (CurrentTimeRule) table.getRuleMap().get("f_time");
        Assert.assertEquals("f_time", currentTimeRule.getField());
        Assert.assertEquals(2000, currentTimeRule.getOffset());

        FromCurrentTimeRule fromCurrentTimeRule = (FromCurrentTimeRule) table.getRuleMap().get("f_date");
        Assert.assertEquals("f_date", fromCurrentTimeRule.getField());
        Assert.assertEquals(2000, fromCurrentTimeRule.getOffset());
        Assert.assertEquals(1, fromCurrentTimeRule.getMin());
        Assert.assertEquals(2, fromCurrentTimeRule.getMax());
        Assert.assertEquals(Calendar.HOUR_OF_DAY, fromCurrentTimeRule.getUnit());

        RangeRule rangeRule = (RangeRule) table.getRuleMap().get("f_integer");
        Assert.assertEquals("f_integer", rangeRule.getField());
        Assert.assertEquals(1.0F, rangeRule.getFrom(), 0.01F);
        Assert.assertFalse(rangeRule.isIncludeFrom());
        Assert.assertEquals(12.5F, rangeRule.getTo(), 0.01F);
        Assert.assertFalse(rangeRule.isIncludeTo());
    }

    /** 至少指定一个规则 */
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

    /** 属性 Field 的值不能为空 */
    @Test
    public void testLoad05() {
        testLoadError("05.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError(""), //
                      Messages.parseCommonAttrEmpty("Field"));
    }

    /** 不支持的子元素 */
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

    /** CurrentTime 的 Offset 格式不正确 */
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

    /** CurrentTime 不支持的属性 */
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

    /** CurrentTime 不支持的子元素 */
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

    /** FromCurrentTime 的 Max 格式不正确 */
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

    /** FromCurrentTime 的 Unit 格式不正确 */
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

    /** FromCurrentTime 不支持的属性 */
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

    /** FromCurrentTime 不支持的子元素 */
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

    /** RegExp 不支持的属性 */
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

    /** RegExp 不支持的子元素 */
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

    /** Range 不支持的属性 */
    @Test
    public void testLoad16() {
        testLoadError("16.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonAttrUnknown("Range", "U"));
    }

    /** Range 不支持的子元素 */
    @Test
    public void testLoad17() {
        testLoadError("17.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseCommonChildren("Range"));
    }

    /** Range 中的 From 和 To 至少要有一个 */
    @Test
    public void testLoad18() {
        testLoadError("18.xml", Messages.parseSourcesError(), //
                      Messages.parseSourceError("mysql"), //
                      Messages.parseSourceVerifyError(), //
                      Messages.parseTableError("tab"), //
                      Messages.parseRulesError(), //
                      Messages.parseRuleError("f_time"), //
                      Messages.parseRuleRangeChoice());
    }
}
