package com.github.bookong.zest.annotation;

import com.github.bookong.zest.executor.AbstractExecutor;
import com.github.bookong.zest.executor.SqlExecutor;
import javax.sql.DataSource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.redis.core.RedisOperations;
import java.lang.annotation.*;

/**
 * 用注解标记一个 {@link DataSource} , {@link MongoOperations} 或 {@link RedisOperations} 对象，用这个对象实现自动操作
 * 
 * @author Jiang Xu
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZestSource {

    /** 数据源 ID */
    String value() default "";

    /** 这个数据源对应的执行器 */
    Class<? extends AbstractExecutor> executorClass() default SqlExecutor.class;

}
