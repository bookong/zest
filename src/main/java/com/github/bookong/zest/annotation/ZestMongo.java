package com.github.bookong.zest.annotation;

import org.springframework.data.mongodb.core.MongoOperations;

import java.lang.annotation.*;

/**
 * 指定某个参数是操作 MongoDB 的 {@link MongoOperations} 对象
 *
 * @author Jiang Xu
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZestMongo {
    /**
     * 数据源 ID
     */
    String value() default "";
}
