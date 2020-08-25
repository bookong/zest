package com.github.bookong.zest.common;

/**
 * @author Jiang Xu
 */
public interface ZestGlobalConstant {

    /** XML 数据文件解析类所在的包 */
    String DATA_XML_CODE_PACKAGE = "com.github.bookong.zest.support.xml.data"; //$NON-NLS-1$

    interface Logger {

        String SQL = "zest_sql"; // $NON-NLS-1$
    }

    interface Xml {

        String ZEST              = "Zest";
        String DESCRIPTION       = "Description";
        String VERSION           = "Version";
        String CURRENT_TIME      = "CurrentTime";
        String SOURCES           = "Sources";
        String SOURCE            = "Source";
        String PARAM             = "Param";
        String PARAM_FIELD       = "ParamField";
        String ID                = "Id";
        String NAME              = "Name";
        String INIT              = "Init";
        String VERIFY            = "Verify";
        String IGNORE            = "Ignore";
        String ONLY_CORE_DATA    = "OnlyCoreData";
        String TABLE             = "Table";
        String COLLECTION        = "Collection";
        String ENTITY_CLASS      = "EntityClass";
        String SORTS             = "Sorts";
        String SORT              = "Sort";
        String RULES             = "Rules";
        String RULE              = "Rule";
        String DATA              = "Data";
        String FIELD             = "Field";
        String DIRECTION         = "Direction";
        String PATH              = "Path";
        String NULLABLE          = "Nullable";
        String REG_EXP           = "RegExp";
        String FROM_CURRENT_TIME = "FromCurrentTime";
        String ASC               = "asc";
        String DESC              = "desc";

    }
}
