package com.github.bookong.zest.annotation;

import java.lang.annotation.*;

/**
 * 指定某个参数是 JDBC 的连接对象 {@link java.sql.Connection}
 *
 * @author Jiang Xu
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZestConnection {

    /** 数据源 ID */
    String value() default "";
}
