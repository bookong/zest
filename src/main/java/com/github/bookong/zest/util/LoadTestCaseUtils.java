package com.github.bookong.zest.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.github.bookong.zest.core.testcase.TestCaseData.TestParamInfo;

/**
 * @author jiangxu
 */
public class LoadTestCaseUtils {

    /**
     * 从 XML 文件加载的内容转换为合适的值
     * 
     * @param info 解析的测试对象信息
     * @param value XML 文件中的取值
     * @return 返回值必须是 Integer, Long, Boolean, Double, Float, String, Date
     */
    public static Object loadXmlFieldValue(TestParamInfo info, String value) {
        Class<?> fieldClass = info.getFieldClass();
        if (Integer.class.isAssignableFrom(fieldClass) || "int".equals(fieldClass.getName())) {
            return Integer.valueOf(value.trim());
        } else if (Long.class.isAssignableFrom(fieldClass) || "long".equals(fieldClass.getName())) {
            return Long.valueOf(value.trim());
        } else if (Boolean.class.isAssignableFrom(fieldClass) || "boolean".equals(fieldClass.getName())) {
            return Boolean.valueOf(value.trim());
        } else if (Double.class.isAssignableFrom(fieldClass) || "double".equals(fieldClass.getName())) {
            return Double.valueOf(value.trim());
        } else if (Float.class.isAssignableFrom(fieldClass) || "float".equals(fieldClass.getName())) {
            return Float.valueOf(value.trim());
        } else if (String.class.isAssignableFrom(fieldClass)) {
            return value;
        } else if (List.class.isAssignableFrom(fieldClass)) {
            Type fc = info.getField().getGenericType();
            if (fc != null && fc instanceof ParameterizedType) {
                return loadXmlFieldValueWithGenericClass(fc, value, 0);
            } else {
                throw new RuntimeException(Messages.getString("loadTestCase.mapAndListMustUseGenericType"));
            }
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            Type fc = info.getField().getGenericType();
            if (fc != null && fc instanceof ParameterizedType) {
                return loadXmlFieldValueWithGenericClass(fc, value, 1);
            } else {
                throw new RuntimeException(String.format(Messages.getString("loadTestCase.mapAndListMustUseGenericType"), info.getField().getName()));
            }
        } else if (Date.class.isAssignableFrom(fieldClass)) {
            return parseDate(value);
        } else {
            throw new RuntimeException(String.format(Messages.getString("loadTestCase.notSupportedTypes"), fieldClass.getName()));
        }
    }

    private static Object loadXmlFieldValueWithGenericClass(Type fc, String value, int ActualTypeArgumentIdx) {
        Class<?> genericClass = (Class<?>) ((ParameterizedType) fc).getActualTypeArguments()[ActualTypeArgumentIdx];
        if (Integer.class.isAssignableFrom(genericClass)) {
            return Integer.valueOf(value.trim());
        } else if (Long.class.isAssignableFrom(genericClass)) {
            return Long.valueOf(value.trim());
        } else if (Boolean.class.isAssignableFrom(genericClass)) {
            return Boolean.valueOf(value.trim());
        } else if (Double.class.isAssignableFrom(genericClass)) {
            return Double.valueOf(value.trim());
        } else if (Float.class.isAssignableFrom(genericClass)) {
            return Float.valueOf(value.trim());
        } else if (String.class.isAssignableFrom(genericClass)) {
            return value;
        } else if (Date.class.isAssignableFrom(genericClass)) {
            return parseDate(value);
        } else {
            throw new RuntimeException(Messages.getString("loadTestCase.mapAndListValueNotSupportedTypes"));
        }
    }

    /**
     * 解析字符串到日期类型，支持格式
     * <ul>
     * <li>yyyy-MM-dd hh:mm:ss</li>
     * <li>yyyy-MM-dd hh:mm</li>
     * <li>yyyy-MM-dd hh</li>
     * <li>yyyy-MM-dd</li>
     * </ul>
     * 
     * @param value 待解析的字符串
     * @return 解析后的日期对象
     */
    public static Date parseDate(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        value = value.trim();

        try {
            switch (value.length()) {
                case 19:
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
                case 16:
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value);
                case 13:
                    return new SimpleDateFormat("yyyy-MM-dd HH").parse(value);
                case 10:
                    return new SimpleDateFormat("yyyy-MM-dd").parse(value);
                default:
                    throw new RuntimeException(String.format(Messages.getString("loadTestCase.notSupportedDateFormat"), value));
            }
        } catch (ParseException e) {
            throw new RuntimeException(String.format(Messages.getString("loadTestCase.notSupportedDateFormat"), value), e);
        }
    }

}
