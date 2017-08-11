package com.github.bookong.zest.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.bookong.zest.core.executer.AbstractJdbcExcuter;
import com.github.bookong.zest.core.executer.DbUnitExcuter;

/**
 * 用注解标记一个 javax.sql.DataSource 对象，用这个对象操作数据库
 * 
 * @author jiangxu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface ZestDataSource {
	/** json 中定义的数据库名称 */
	String value() default "";
	/** 这个数据库对应的执行器 */
	Class<? extends AbstractJdbcExcuter> executerClazz() default DbUnitExcuter.class;
}