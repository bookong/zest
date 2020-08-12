package com.github.bookong.zest.runner.junit4.annotation;

import java.lang.annotation.*;

/**
 * 注释在被测试的方法方，标识这个方法是一个 Zest 支持的测试方法。<br>
 * 自动匹配和测试类相似的目录结构下查找参数 value 指定的文件。 例如：测试类为 zest.TicketSreviceTest 测试方法为 testApplyTicket 那么自动匹配路径为: <br>
 * $SOURCE/target/test-classes/zest/datas/TicketSreviceTest/testApplyTicket<br>
 * 以包名和类名作为目录名，中间加了一层 datas 目录目的是避免警告(The type XXXX collides with a package)<br>
 * 从找到的这些文件中（xml 格式）解析测试用例需要的数据，循环执行被测试方法。<br>
 * 
 * @author jiangxu
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZestTest {

    /** 测试用例文件名（不含扩展名)，如果不设置，则默认认为在指定路径下所有文件都是有效的 xml 文件 */
    String value() default "";

    /** 测试用例扩展名 */
    String extName() default "xml";
}
