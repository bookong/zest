package com.github.bookong.zest.runner.junit4.annotation;

import com.github.bookong.zest.core.executer.AbstractExcuter;
import com.github.bookong.zest.core.executer.SqlExcuter;
import com.github.bookong.zest.core.testcase.AbstractDataConverter;

import java.lang.annotation.*;

/**
 * 用注解标记一个 javax.sql.DataSource 对象，用这个对象操作数据库
 * 
 * @author jiangxu
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZestDataSource {

    /** 数据源 ID */
    String value() default "";

    /** 这个数据源对应的执行器 */
    Class<? extends AbstractExcuter> executerClass() default SqlExcuter.class;

    /** 数据转换器，如果设置会优先使用 */
    Class<? extends AbstractDataConverter>[] dataConverterClasses() default {};
}
