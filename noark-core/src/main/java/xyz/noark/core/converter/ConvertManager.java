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
package xyz.noark.core.converter;

import java.util.HashMap;
import java.util.Map;

import xyz.noark.core.annotation.TemplateConverter;
import xyz.noark.core.converter.impl.BooleanConverter;
import xyz.noark.core.converter.impl.FloatConverter;
import xyz.noark.core.converter.impl.FloatListConverter;
import xyz.noark.core.converter.impl.IntListConverter;
import xyz.noark.core.converter.impl.IntegerConverter;
import xyz.noark.core.converter.impl.LongConverter;
import xyz.noark.core.converter.impl.StringConverter;
import xyz.noark.core.util.ClassUtils;

/**
 * 模板转换实现管理类.
 * <p>
 * 内置一些系统级别的转化功能.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class ConvertManager {
	private static final ConvertManager INSTANCE = new ConvertManager();
	private static final Map<Class<?>, Converter<?>> CONVERTERS = new HashMap<>();

	static {
		INSTANCE.regist(BooleanConverter.class);
		INSTANCE.regist(IntegerConverter.class);
		INSTANCE.regist(LongConverter.class);
		INSTANCE.regist(StringConverter.class);
		INSTANCE.regist(FloatConverter.class);
		INSTANCE.regist(IntListConverter.class);
		INSTANCE.regist(FloatListConverter.class);
	}

	private ConvertManager() {}

	public static ConvertManager getInstance() {
		return INSTANCE;
	}

	public Converter<?> getConverter(Class<?> type) {
		return CONVERTERS.get(type);
	}

	/**
	 * 注册一个模板转化实现类.
	 * 
	 * @param klass 类型
	 * @param templateConverter 转化器
	 */
	public void regist(Class<?> klass, TemplateConverter templateConverter) {
		Object object = ClassUtils.newInstance(klass);
		if (!(object instanceof Converter<?>)) {
			throw new RuntimeException("非法的转化器." + klass.getName());
		}
		this.putConvert((Converter<?>) object, templateConverter);
	}

	/**
	 * 系统内部使用,不判定注解和接口
	 * 
	 * @param klass 转化类
	 */
	private void regist(Class<? extends Converter<?>> klass) {
		this.putConvert(ClassUtils.newInstance(klass), klass.getAnnotation(TemplateConverter.class));
	}

	private void putConvert(Converter<?> converter, TemplateConverter annotation) {
		for (Class<?> targetClass : annotation.value()) {
			CONVERTERS.put(targetClass, converter);
		}
	}
}