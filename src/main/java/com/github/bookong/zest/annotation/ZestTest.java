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
package com.github.bookong.zest.annotation;

import java.lang.annotation.*;
import com.github.bookong.zest.runner.junit4.ZestSpringJUnit4ClassRunner;
import com.github.bookong.zest.testcase.ZestParam;
import com.github.bookong.zest.runner.junit5.ZestJUnit5Worker;

/**
 * Use this annotation to mark a method, which means that the method uses <em>Zest</em> for testing.
 * <p>
 * In the case of combining <em>Spring MVC</em> and <em>JUnit 4</em> for testing, you first need to specify the runner
 * class as {@link ZestSpringJUnit4ClassRunner} through @RunWith. Then replace @Test with @ZestTest.
 * <p>
 * The method annotated by @ZestTest must have one and only one parameter, which must be a subclass of
 * {@link ZestParam}. for example:
 *
 * <pre class="code">
 * &#064;RunWith(ZestSpringJUnit4ClassRunner.class)
 * &#064;WebAppConfiguration
 * &#064;ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
 * public class ZestTest {
 *   &#064;ZestTest
 *   public void testXXX(Param param) {
 *     // Execute the code under test ...
 *   }
 *
 *   public static class Param extends ZestParam {
 *     ...
 *   }
 * }
 * </pre>
 *
 * In the case of combining <em>Spring Boot</em> and <em>JUnit 5</em> for testing, you first need to create a
 * {@link ZestJUnit5Worker} object. Then use @ZestTest to annotate the same method in @TestFactory, for example:
 *
 * <pre class="code">
 * &#064;ActiveProfiles("test")
 * &#064;SpringBootTest
 * public class ZestTest {
 *   protected ZestJUnit5Worker zestWorker = new ZestJUnit5Worker();
 *
 *   &#064;ZestTest
 *   &#064;TestFactory
 *   public Stream<DynamicTest> testXXX() {
 *     return zestWorker.test(this, Param.class, param -> {
 *       // Execute the code under test ...
 *     });
 *   }
 *
 *   public static class Param extends ZestParam {
 *     ...
 *   }
 * }
 * </pre>
 *
 * When the test case name is specified by {@link #value}, the test data file will be searched in the default path (use
 * {@link #extName} to specify the extension). If {@link #value} is not specified, all files matching the extension of
 * {@link #extName} under the default path will be used
 * <p>
 * The default path for searching test data files is defined as follows: If your test class is {@code com.abc.ZestTest}
 * and the test method is {@code testXXX()}, then the default path is {@code com/abc/data/ZestTest/testXXX}. A word
 * <em>data</em> is added between the package name and the class name to avoid warnings (The type {@code ZestTest} collides
 * with a package).
 *
 * @author Jiang Xu
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZestTest {

    /**
     * If empty, it will search for available files in the default path
     *
     * @return Test case file name (without extension)
     */
    String value() default "";

    /**
     * @return Test case extension
     */
    String extName() default ".xml";
}
