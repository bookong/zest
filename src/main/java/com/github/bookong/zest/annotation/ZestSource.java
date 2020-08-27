/**
 * Copyright 2010-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.annotation;

import com.github.bookong.zest.executor.AbstractExecutor;
import com.github.bookong.zest.executor.SqlExecutor;
import org.springframework.data.mongodb.core.MongoOperations;

import javax.sql.DataSource;
import java.lang.annotation.*;

/**
 * <p>
 * Use this annotation to register a database operator to <em>Zest</em>. This operator must implement {@link DataSource}
 * or {@link MongoOperations} interface and be autowired by <em>Spring Framework</em>.
 * </p>
 * <p>
 * The registered database operator will be bound to the &lt;Source&gt; element data in the unit test data. (The
 * {@link #value} attribute corresponds to the <em>Id</em> attribute). <em>Zest</em> will then use it to complete the
 * initialization and automatic verification of test data.
 * </p>
 * <p>
 * Configuration example:
 * </p>
 * 
 * <pre class="code">
 * &#064;ActiveProfiles("test")
 * &#064;SpringBootTest
 * public class ZestTest {
 *   &#064;Autowired
 *   &#064;ZestSource("mysql")
 *   private DataSource         dataSource;
 *
 *   &#064;Autowired
 *   &#064;ZestSource(value = "mongo", executorClass = MongoExecutor.class)
 *   protected MongoTemplate    mongoTemplate;
 *
 *   ...
 * }
 * </pre>
 * <p>
 * The above code is bound to the unit test data as shown below
 * </p>
 * 
 * <pre class="code">
 * {@code
 *   <Zest xmlns="https://www.bookong.net/schema/zest/data" >
 *     ...
 *     <Sources>
 *       <Source Id="mysql">
 *         ...
 *       </Source>
 *       <Source Id="mongo">
 *         ...
 *       </Source>
 *     </Sources>
 *   </Zest>
 * }
 * </pre>
 *
 * @author Jiang Xu
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZestSource {

    /**
     * @return Associated to the &lt;Source&gt; element in the test data as an <em>Id</em> attribute
     */
    String value() default "";

    /**
     * Executors of different database types can be replaced by custom subclasses
     * 
     * @return The core logic of initial data and automatic comparison
     */
    Class<? extends AbstractExecutor> executorClass() default SqlExecutor.class;
}
