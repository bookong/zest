package com.github.bookong.zest.util;

import com.github.bookong.zest.core.ZestGlobalConstant;

/**
 * @author jiangxu
 */
public class LoadTestCaseUtil {

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
