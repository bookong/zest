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
package com.github.bookong.zest.runner.junit4.statement;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

/**
 * Customized <em>Zest</em> Filter.
 *
 * @author Jiang Xu
 */
public class ZestFilter extends Filter {

    private Filter filter;

    /**
     * Construct a new instance.
     *
     * @param filter
     *          Original filter.
     */
    public ZestFilter(Filter filter){
        this.filter = filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldRun(Description description) {
        String desiredDescription = filter.describe();
        if (description.isTest()) {
            String className = description.getClassName();
            String methodName = description.getMethodName();
            int pos = methodName.indexOf("[");
            if (pos > 0) {
                methodName = methodName.substring(0, pos).trim();
            }

            String expected = String.format(" %s(%s)", methodName, className);
            boolean b = desiredDescription.indexOf(expected) > 0;
            if (b) {
                return b;
            }
            return desiredDescription.startsWith(methodName.concat(" "));
        }

        // explicitly check if any children want to run
        for (Description each : description.getChildren()) {
            if (shouldRun(each)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String describe() {
        return filter.describe();
    }
}
