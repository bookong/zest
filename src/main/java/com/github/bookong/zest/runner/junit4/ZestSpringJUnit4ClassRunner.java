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

import com.github.bookong.zest.annotation.ZestTest;
import com.github.bookong.zest.runner.junit4.statement.ZestFilter;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Extend {@code SpringJUnit4ClassRunner} to support <em>Zest</em>.
 *
 * @author Jiang Xu
 */
public class ZestSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner implements ZestClassRunner {

    private ZestJUnit4Worker worker;

    /**
     * Construct a new instance.
     *
     * @param clazz
     *          The test class to be run.
     * @throws InitializationError
     *          If the test class is malformed.
     */
    public ZestSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError{
        super(clazz);
        worker = new ZestJUnit4Worker(getTestClass(), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return ZestJUnit4Worker.computeTestMethods(getTestClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        ZestTest zestTest = frameworkMethod.getAnnotation(ZestTest.class);
        if (zestTest == null) {
            super.runChild(frameworkMethod, notifier);
        } else {
            worker.runChild(frameworkMethod, notifier);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        super.filter(new ZestFilter(filter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object createTest() throws Exception {
        return super.createTest();
    }
}
