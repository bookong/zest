package com.github.bookong.zest.testcase.data.data;

import com.github.bookong.zest.testcase.AbstractTable;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.testcase.data.AbstractZestDataTest;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.mongo.Document;
import com.github.bookong.zest.testcase.param.Param;
import com.github.bookong.zest.testcase.sql.Row;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.ZestDateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * 测试元素 Zest/Sources/Source/Init|Verify/Table/Data
 *
 * @author Jiang Xu
 */
public class ZestDataTest extends AbstractZestDataTest {

    /** mysql 类型 Init 下，正常数据 */
    @Test
    public void testLoad01() {
        ZestData zestData = load("01.xml");
        AbstractTable<?> abstractTable = zestData.getSourceList().get(0).getInitData().getTableList().get(0);
        Assert.assertTrue(abstractTable instanceof Table);
        Table table = (Table) abstractTable;
        Assert.assertEquals(1, table.getDataList().size());

        Row row = table.getDataList().get(0);
        Assert.assertEquals(5, row.getDataMap().size());

        Assert.assertEquals(12, ((Integer) row.getDataMap().get("f_integer")).intValue());
        Assert.assertEquals("str", String.valueOf(row.getDataMap().get("f_varchar")));
        Assert.assertEquals("2020-09-28T11:12:13.000+08",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_date"))));
        Assert.assertEquals("2020-09-29T11:12:13.000+08",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_time"))));
        Assert.assertEquals("2020-09-30T11:12:13.000+08",
                            ZestDateUtil.formatDateNormal(((Date) row.getDataMap().get("f_timestamp"))));
    }

    /** mongo 类型 Init 下，正常数据 */
    @Test
    public void testLoad02() {
        ZestData zestData = load("02.xml");
        AbstractTable<?> abstractTable = zestData.getSourceList().get(0).getInitData().getTableList().get(0);
        Assert.assertTrue(abstractTable instanceof Collection);
        Collection collection = (Collection) abstractTable;
        Assert.assertEquals(1, collection.getDataList().size());

        Document document = collection.getDataList().get(0);
        Assert.assertTrue(document.getData() instanceof Param);
        Param data = (Param) document.getData();

        Assert.assertEquals(12, data.getIntValue());
        Assert.assertEquals("str", data.getStrValue());
        Assert.assertEquals("2020-09-28T11:12:13.000+08", ZestDateUtil.formatDateNormal(data.getDate1()));
    }
}
