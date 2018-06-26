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
package xyz.noark.core.ioc.definition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.ioc.BeanDefinition;
import xyz.noark.core.ioc.FieldDefinition;
import xyz.noark.core.ioc.IocMaking;
import xyz.noark.core.ioc.MethodDefinition;
import xyz.noark.core.ioc.NoarkIoc;
import xyz.noark.core.ioc.definition.field.DefaultFieldDefinition;
import xyz.noark.core.ioc.definition.field.ListFieldDefinition;
import xyz.noark.core.ioc.definition.field.MapFieldDefinition;
import xyz.noark.core.ioc.definition.field.ValueFieldDefinition;
import xyz.noark.core.ioc.definition.method.SimpleMethodDefinition;
import xyz.noark.core.ioc.wrap.method.BaseMethodWrapper;
import xyz.noark.reflectasm.MethodAccess;
import xyz.noark.util.ClassUtils;
import xyz.noark.util.FieldUtils;
import xyz.noark.util.MethodUtils;

/**
 * 默认的Bean定义描述类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class DefaultBeanDefinition implements BeanDefinition {
	private static final Set<Class<?>> IGNORE_ANNOTATION_BY_METHODS = new HashSet<>();
	static {
		IGNORE_ANNOTATION_BY_METHODS.add(Override.class);
		IGNORE_ANNOTATION_BY_METHODS.add(Deprecated.class);
		IGNORE_ANNOTATION_BY_METHODS.add(SuppressWarnings.class);
	}
	/** 缓存那个单例对象 */
	protected final Object single;
	private final Class<?> beanClass;
	protected final MethodAccess methodAccess;
	/** 所有需要注入的属性 */
	private final ArrayList<FieldDefinition> autowiredFields = new ArrayList<>();
	protected final HashMap<Class<? extends Annotation>, List<MethodDefinition>> customMethods = new HashMap<>();

	public DefaultBeanDefinition(Class<?> klass) {
		this(ClassUtils.newInstance(klass));
	}

	public DefaultBeanDefinition(Object object) {
		this.single = object;
		this.beanClass = object.getClass();
		this.methodAccess = MethodAccess.get(beanClass);
	}

	public DefaultBeanDefinition init() {
		this.analysisField();
		this.analysisMethod();
		return this;
	}

	@Override
	public String[] getNames() {
		return new String[] { beanClass.getName() };
	}

	/**
	 * 获取这个Bean的单例缓存对象.
	 * 
	 * @return 实例对象.
	 */
	public Object getSingle() {
		return single;
	}

	/**
	 * 获取当前Bean的Class
	 * 
	 * @return 当前Bean的Class
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}

	private void analysisMethod() {
		MethodUtils.getAllMethod(beanClass).forEach(method -> {
			Annotation[] annotations = method.getAnnotations();
			// 没有注解的忽略掉
			if (annotations != null && annotations.length > 0) {
				for (Annotation annotation : annotations) {
					final Class<? extends Annotation> annotationType = annotation.annotationType();
					// 忽略一些系统警告类的注解
					if (IGNORE_ANNOTATION_BY_METHODS.contains(annotationType)) {
						continue;
					}
					this.analysisMthodByAnnotation(annotationType, annotation, method);
				}
			}
		});
	}

	/**
	 * 分析方法上的注解.
	 */
	protected void analysisMthodByAnnotation(Class<? extends Annotation> annotationType, Annotation annotation, Method method) {
		customMethods.computeIfAbsent(annotationType, key -> new ArrayList<>(64)).add(new SimpleMethodDefinition(methodAccess, method));
	}

	private void analysisField() {
		FieldUtils.getAllField(beanClass).stream().filter(v -> v.isAnnotationPresent(Autowired.class) || v.isAnnotationPresent(Value.class)).forEach(v -> analysisAutowiredOrValue(v));
	}

	/** 分析注入的类型 */
	private void analysisAutowiredOrValue(Field field) {
		final Class<?> fieldClass = field.getType();

		Value value = field.getAnnotation(Value.class);
		if (value == null) {
			Autowired autowired = field.getAnnotation(Autowired.class);

			// List类型的注入需求
			if (fieldClass == List.class) {
				autowiredFields.add(new ListFieldDefinition(field, autowired.required()));
			}

			// Map类型的注入需求
			else if (fieldClass == Map.class) {
				autowiredFields.add(new MapFieldDefinition(field, autowired.required()));
			}

			// 其他就当普通Bean处理...
			else {
				autowiredFields.add(new DefaultFieldDefinition(field, autowired.required()));
			}
		}
		// @Value注入配置属性.
		else {
			autowiredFields.add(new ValueFieldDefinition(field, value.value()));
		}
	}

	@Override
	public void injection(IocMaking making) {
		this.autowiredFields.forEach((v) -> v.injection(single, making));
	}

	/**
	 * 分析此用的功能用途.
	 * 
	 * @param ioc 容器
	 */
	public void doAnalysisFunction(NoarkIoc ioc) {
		// 有自定义的注解需要送回来IOC容器中.
		customMethods.forEach((k, list) -> list.forEach(v -> ioc.addCustomMethod(k, new BaseMethodWrapper(v.getMethodAccess(), single, v.getMethodIndex()))));
	}
}