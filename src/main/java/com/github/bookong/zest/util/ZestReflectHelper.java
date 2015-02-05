package com.github.bookong.zest.util;

import java.lang.reflect.Field;

/**
 * 反射工具
 * 
 * @author jiangxu
 * 
 */
public class ZestReflectHelper {
	
	/**
	 * 获取obj 对象 fieldName 的 Field
	 */
	public static Field getFieldByFieldName(Object obj, String fieldName) {
		Class<?> superClass = obj.getClass();
		while (!superClass.getName().equals(Object.class.getName())){
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				superClass = superClass.getSuperclass();
				continue;
			}
		}
		return null;
	}

	/**
	 * 获取 obj 对象 fieldName 的属性值
	 */
	public static Object getValueByFieldName(Object obj, String fieldName) {
		try {
			Field field = getFieldByFieldName(obj, fieldName);
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
			throw new RuntimeException(e);
		}
	}

	/**
	 * 设置 obj 对象 fieldName 的属性值
	 */
	public static void setValueByFieldName(Object obj, String fieldName, Object value){
		try {
			Field field = getFieldByFieldName(obj, fieldName);
			if (field.isAccessible()) {
				field.set(obj, value);
			} else {
				field.setAccessible(true);
				field.set(obj, value);
				field.setAccessible(false);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
