package com.github.bookong.zest.core.annotation;

import java.lang.annotation.*;

/**
 * 指定某个参数是 JDBC 的连接对象
 * 
 * @author jiangxu
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZestJdbcConn {

    /** 这个 JDBC 连接对象对应的库名称 */
    String value() default "";
}
