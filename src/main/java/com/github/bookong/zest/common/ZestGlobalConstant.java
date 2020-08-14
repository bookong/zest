package com.github.bookong.zest.common;

/**
 * @author jiangxu
 */
public interface ZestGlobalConstant {

    /** XML 数据文件解析类所在的包 */
    String DATA_XML_CODE_PACKAGE = "com.github.bookong.zest.support.xml.data"; //$NON-NLS-1$

    /** 数据源类型 */
    interface DataSourceType {

        String MySQL     = "MySQL";     // $NON-NLS-1$
        String Oracle    = "Oracle";    // $NON-NLS-1$
        String SQLServer = "SQLServer"; // $NON-NLS-1$
        String MongoDB   = "MongoDB";   // $NON-NLS-1$
    }

    interface Logger {

        String SQL = "zest_sql"; // $NON-NLS-1$
    }
}
