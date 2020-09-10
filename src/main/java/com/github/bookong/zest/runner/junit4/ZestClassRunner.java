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
package com.github.bookong.zest.runner.junit4;

import com.github.bookong.zest.runner.ZestWorker;

/**
 * In order to bridge {@link ZestWorker} code with {@link ZestSpringJUnit4ClassRunner} code.
 *
 * @author Jiang Xu
 */
public interface ZestClassRunner {

    /**
     * Delegate to the parent implementation for creating the test instance.
     *
     * @return a test instance.
     * @throws Exception
     *          When creating the instance any exception occurs.
     */
    Object createTest() throws Exception;
}
