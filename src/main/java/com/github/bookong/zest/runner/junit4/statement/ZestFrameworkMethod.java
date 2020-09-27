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

import java.io.File;
import java.util.Objects;

import org.junit.runners.model.FrameworkMethod;

/**
 * Customized <em>Zest</em> FrameworkMethod.
 *
 * @author Jiang Xu
 */
public class ZestFrameworkMethod extends FrameworkMethod {

    private String filePath;
    private String fileName;

    /**
     * Construct a new instance.
     *
     * @param method
     *          Original method.
     * @param filePath
     *          File path of test case data (*.xml).
     */
    public ZestFrameworkMethod(FrameworkMethod method, String filePath){
        super(method.getMethod());
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return String.format("%s [%s] ", super.getName(), fileName);
    }

    /**
     * @return file path of test case data (*.xml).
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ZestFrameworkMethod that = (ZestFrameworkMethod) o;
        return Objects.equals(filePath, that.filePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), filePath);
    }
}
