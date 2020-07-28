package com.github.bookong.zest.util;

import com.github.bookong.zest.core.ZestGlobalConstant;
import com.github.bookong.zest.exceptions.LoadTestCaseFileException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * @author jiangxu
 */
public class LoadTestCaseUtil {

    /**
     * 解析字符串到日期类型，支持格式
     * <ul>
     * <li>yyyy-MM-dd hh:mm:ss</li>
     * <li>yyyy-MM-dd hh:mm</li>
     * <li>yyyy-MM-dd hh</li>
     * <li>yyyy-MM-dd</li>
     * </ul>
     * 
     * @param value 待解析的字符串
     * @return 解析后的日期对象
     * @throws LoadTestCaseFileException
     */
    public static Date parseDate(String value) throws LoadTestCaseFileException {
        if (StringUtils.isBlank(value)) {
            throw new LoadTestCaseFileException(Messages.failParseDate(value));
        }
        value = value.trim();
        try {
            return DateUtils.parseDateStrictly(value, new String[] { "yyyy-MM-dd HH:mm:ss", //
                                                                     "yyyy-MM-dd HH:mm", //
                                                                     "yyyy-MM-dd HH", //
                                                                     "yyyy-MM-dd" });
        } catch (ParseException e) {
            throw new LoadTestCaseFileException(Messages.failParseDate(value), e);
        }
    }

    /** 是不是关系型数据库 */
    public static boolean isRmdb(String type) {
        switch (type) {
            case ZestGlobalConstant.DataSourceType.MySQL:
            case ZestGlobalConstant.DataSourceType.Oracle:
            case ZestGlobalConstant.DataSourceType.SQLServer:
                return true;
            default:
                return false;
        }
    }
}
