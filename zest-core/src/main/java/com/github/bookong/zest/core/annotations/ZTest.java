package com.github.bookong.zest.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注释在被测试的方法方，标识这个方法是一个 Zest 支持的测试方法。
 * <ul>
 * 		<li>首先在 absoluteDir 参数指定的绝对路径上查找参数 filenames 指定的文件</li>
 * 		<li>如果 absoluteDir 参数没有设置，则在 relativePath 参数指定的相对路径上查找参数 filenames 指定的文件</li>
 * 		<li>如果 absoluteDir 参数与 relativePath 参数都没有设置，则自动匹配和测试类相似的目录结构下查找参数 filenames 指定的文件。
 * 			例如：测试类为 zest.servicetest.TicketSreviceTest 测试方法为 testApplyTicket，
 * 			那么自动匹配路径为 $SOURCE/target/test-classes/zest/servicetest/ticketservicetest/testapplyticket</li>
 * </ul>
 * 从找到的这些文件中（json 格式）解析测试用例需要的数据，循环执行被测试方法。<br>
 * 
 * @author jiangxu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ZTest {
	/** 绝对目录*/
	String absoluteDir() default "";
	/** 相对路径 */
	String relativePath() default "";
	/** 测试用例文件，如果不设置，则默认认为在指定路径下所有文件都是有效的 json 文件 */
	String[] filenames() default {};
}
