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
package com.github.bookong.zest.rule;

import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import org.junit.Assert;

import java.util.Date;

/**
 * Abstract automatic validation rules.
 *
 * @author Jiang Xu
 */
public abstract class AbstractRule {

    private String  field;
    private boolean nullable;
    private boolean manual;

    /**
     * Constructs a new instance.
     *
     * @param field
     *          Field name corresponding to the validation rule.
     * @param nullable
     *          Whether the content can be {@code NULL}.
     * @param manual
     *          Whether to use this rule manually.
     */
    AbstractRule(String field, boolean nullable, boolean manual){
        this.field = field;
        this.nullable = nullable;
        this.manual = manual;
    }

    /**
     * Use this rule to verify content.
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param actual
     *          Actual data.
     */
    public abstract void verify(ZestData zestData, Object actual);

    /**
     * Try to convert actual data to {@code Date} object.
     *
     * @param actual
     *          Actual data.
     * @return A {@code Date} object
     */
    protected Date getActualDataTime(Object actual) {
        Date tmp = null;

        if (actual instanceof Date) {
            tmp = (Date) actual;

        } else {
            Assert.fail(Messages.verifyRuleDateType(getField()));
        }

        return tmp;
    }

    /**
     * @return Whether the content can be {@code NULL}.
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * @return field name corresponding to the validation rule.
     */
    public String getField() {
        return field;
    }

    /**
     * @return whether to use this rule manually.
     */
    protected boolean isManual() {
        return manual;
    }
}
