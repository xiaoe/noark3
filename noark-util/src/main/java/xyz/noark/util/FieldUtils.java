/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 * 
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 属性工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class FieldUtils {

	/**
	 * 强制给一个属性{@link Field}写入值.
	 * 
	 * @param target 目标对象
	 * @param field 要写入的属性
	 * @param value 要写入的值
	 */
	public static void writeField(final Object target, final Field field, final Object value) {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		try {
			field.set(target, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(target.getClass() + " 的 " + field.getName() + " 属性无法注入.", e);
		}
	}

	/**
	 * 获取指定类的所有属性，包含父类的属性.
	 * 
	 * @param klass 指定类
	 * @return 指定类的属性集合.
	 */
	public static List<Field> getAllField(final Class<?> klass) {
		List<Field> result = new ArrayList<>();
		for (Class<?> target = klass; target != Object.class; target = target.getSuperclass()) {
			for (Field field : target.getDeclaredFields()) {
				result.add(field);
			}
		}
		return result;
	}
}