package com.github.bookong.zest.testcase;

/**
 * @author jiangxu
 */
public abstract class AbstractDataConverter {

    /** 是否要处理关系型数据库 SqlType */
    public abstract boolean applySqlType(Integer colSqlType);

    /**
     * 将测试用例（xml文件）中某个值转换为关系型数据库插入的对象
     * 
     * @param colSqlType 数据库的对应字段的 SqlType
     * @param xmlValue 测试用例（xml 文件）中的值
     */
    public abstract Object sqlDataConvert(Integer colSqlType, String xmlValue);
}
