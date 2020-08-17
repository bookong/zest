package com.github.bookong.zest.annotation;

import org.springframework.data.redis.core.RedisOperations;

import java.lang.annotation.*;

/**
 * 指定某个参数是操作 MongoDB 的 {@link RedisOperations} 对象
 *
 * @author Jiang Xu
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZestRedis {
    /**
     * 数据源 ID
     */
    String value() default "";
}
