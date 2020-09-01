package com.github.bookong.zest.common;

/**
 * @author Jiang Xu
 */
public interface ZestGlobalConstant {

    String FIX_SUB_DIR    = "data";
    String PATH_SEPARATOR = ".";

    interface Logger {

        String SQL = "zest_sql";
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
        String NULLABLE          = "Nullable";
        String REG_EXP           = "RegExp";
        String FROM_CURRENT_TIME = "FromCurrentTime";
        String MIN               = "Min";
        String MAX               = "Max";
        String UNIT              = "Unit";
        String OFFSET            = "Offset";
        String ASC               = "asc";
        String DESC              = "desc";
        String DAY               = "day";
        String HOUR              = "hour";
        String MINUTE            = "minute";
        String SECOND            = "second";

    }
}
