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
package com.github.bookong.zest.testcase;

/**
 * For test parameters, users need to create a subclass of it and carry the data they need on it. <em>Zest</em> will
 * automatically fill in the above values using the test case data file.
 * 
 * @author Jiang Xu
 */
public abstract class ZestParam {

    private ZestData zestData;

    /**
     * @return an object containing unit test case data.
     */
    public ZestData getZestData() {
        return zestData;
    }

    /**
     * Set an object containing unit test case data.
     * @param zestData
     *          An object containing unit test case data.
     */
    public void setZestData(ZestData zestData) {
        this.zestData = zestData;
    }
}
