package com.github.bookong.zest.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定某个参数是 JDBC 的连接对象
 * 
 * @author jiangxu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ZestJdbcConn {
	/** 这个 JDBC 连接对象对应的库名称 */
	String value() default "";
}
