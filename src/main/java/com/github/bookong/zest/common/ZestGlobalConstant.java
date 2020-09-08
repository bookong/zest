/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.common;

/**
 * Constant string
 * 
 * @author Jiang Xu
 */
public interface ZestGlobalConstant {

    /**
     * The subdir where the test case file is located
     */
    String FIX_SUB_DIR = "data";

    /**
     * Log related constants
     */
    interface Logger {

        /**
         * The log name of the SQL details output by Zest automatically
         */
        String SQL = "zest_sql";
    }

    /**
     * Parse XML related constants
     */
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
