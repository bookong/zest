package com.github.bookong.zest.util;

import com.github.bookong.zest.exception.ZestException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具
 * 
 * @author Jiang Xu
 */
public class ZestReflectHelper {

    public static Field getField(Object obj, String fieldName) {
        for (Class<?> clazz : getAllClass(obj)) {
            for (Field f : clazz.getDeclaredFields()) {
                if (StringUtils.equals(fieldName, f.getName())) {
                    return f;
                }
            }
        }

        return null;
    }

    public static Method getMethod(Object obj, String methodName) {
        for (Class<?> clazz : getAllClass(obj)) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (StringUtils.equals(methodName, m.getName())) {
                    return m;
                }
            }
        }

        return null;
    }

    public static Object getValue(Object obj, String fieldName) {
        return getValue(obj, getField(obj, fieldName));
    }

    public static Object getValue(Object obj, Field field) {
        try {
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
        } catch (Exception e) {
            throw new ZestException("", e);
        }
    }

    public static void setValue(Object obj, String fieldName, Object value) {
        Field field = getField(obj, fieldName);
        if (field == null) {
            return;
        }

        try {
            if (field.isAccessible()) {
                field.set(obj, value);
            } else {
                field.setAccessible(true);
                field.set(obj, value);
                field.setAccessible(false);
            }
        } catch (Exception e) {
            throw new ZestException("", e);
        }
    }

    private static List<Class<?>> getAllClass(Object obj) {
        List<Class<?>> list = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        do {
            list.add(clazz);
            clazz = clazz.getSuperclass();
        } while (!clazz.getName().equals(Object.class.getName()));

        return list;
    }
}
