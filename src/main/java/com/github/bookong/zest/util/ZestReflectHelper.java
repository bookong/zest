package com.github.bookong.zest.util;

import java.lang.reflect.Field;

/**
 * 反射工具
 * 
 * @author jiangxu
 */
public class ZestReflectHelper {

    /**
     * 获取 obj 对象 fieldName 的 Field
     * 
     * @param obj 待操作对象
     * @param fieldName 方法名
     * @return 返回指定方法名的 Field 对象
     */
    public static Field getFieldByFieldName(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        while (!clazz.getName().equals(Object.class.getName())) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取 obj 对象 fieldName 的属性值
     * 
     * @param obj 待操作对象
     * @param fieldName 方法名
     * @return 返回指定方法的值
     * @throws IllegalAccessException
     */
    public static Object getValueByFieldName(Object obj, String fieldName) throws IllegalAccessException {
        return getValueByFieldName( obj, getFieldByFieldName(obj, fieldName));
    }

    /**
     * 获取 obj 对象 fieldName 的属性值
     *
     * @param obj 待操作对象
     * @param fieldName 方法名
     * @return 返回指定方法的值
     * @throws IllegalAccessException
     */
    public static Object getValueByFieldName(Object obj, Field field) throws IllegalAccessException {
        Object value = null;
        if (field != null) {
            if (field.isAccessible()) {
                value = field.get(obj);
            } else {
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        }
        return value;
    }

    /**
     * 设置 obj 对象 fieldName 的属性值
     * 
     * @param obj 待操作对象
     * @param fieldName 方法名
     * @param value 欲设置的值
     * @throws IllegalAccessException
     */
    public static void setValueByFieldName(Object obj, String fieldName, Object value) throws IllegalAccessException {
        Field field = getFieldByFieldName(obj, fieldName);
        if (field == null) {
            return;
        }

        if (field.isAccessible()) {
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        }
    }
}
