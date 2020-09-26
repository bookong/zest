/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.util;

import com.github.bookong.zest.exception.ZestException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection tool.
 * 
 * @author Jiang Xu
 */
public class ZestReflectHelper {

    /**
     * Get field from the class.
     *
     * @param theClass
     *          The object class.
     * @param fieldName
     *          Field name.
     * @return a {@link Field} object.
     */
    public static Field getField(Class<?> theClass, String fieldName) {
        for (Class<?> clazz : getAllClass(theClass)) {
            for (Field f : clazz.getDeclaredFields()) {
                if (StringUtils.equals(fieldName, f.getName())) {
                    return f;
                }
            }
        }

        return null;
    }

    /**
     * Get field from the object.
     * @param obj
     *          The object.
     * @param fieldName
     *          Field name.
     * @return a {@link Field} object.
     */
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

    /**
     * Get method from the object.
     * @param obj
     *          The object.
     * @param methodName
     *          Method name.
     * @return a {@link Method} object.
     */
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

    /**
     * Get field value.
     *
     * @param obj
     *          The object.
     * @param fieldName
     *          Field Name.
     * @return field value.
     */
    public static Object getValue(Object obj, String fieldName) {
        return getValue(obj, getField(obj, fieldName));
    }

    /**
     * Get field value.
     * @param obj
     *          The object.
     * @param field
     *          a {@link Field} object.
     * @return field value.
     */
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
            throw new ZestException(e);
        }
    }

    /**
     * set value to field.
     *
     * @param obj
     *          The object.
     * @param fieldName
     *          Field Name.
     * @param value
     *          The value.
     */
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
            throw new ZestException(e);
        }
    }

    private static List<Class<?>> getAllClass(Object obj) {
        return getAllClass(obj.getClass());
    }

    private static List<Class<?>> getAllClass(Class<?> clazz) {
        List<Class<?>> list = new ArrayList<>();

        do {
            list.add(clazz);
            clazz = clazz.getSuperclass();
        } while (!Object.class.getName().equals(clazz.getName()));

        return list;
    }
}
