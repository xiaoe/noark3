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
import xyz.noark.reflectasm.MethodAccess;
import xyz.noark.util.ClassUtils;
import xyz.noark.util.FieldUtils;

/**
 * 默认的Bean定义描述类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class DefaultBeanDefinition implements BeanDefinition {
	private static final Set<Class<?>> ignoreAnnotationByMethods = new HashSet<>();
	static {
		ignoreAnnotationByMethods.add(Override.class);
		ignoreAnnotationByMethods.add(SuppressWarnings.class);
	}

	protected final Object single;// 缓存那个单例对象
	private final Class<?> beanClass;
	protected final MethodAccess methodAccess;
	private final ArrayList<FieldDefinition> autowiredFields = new ArrayList<>();// 所有需要注入的属性
	protected final HashMap<Class<? extends Annotation>, List<MethodDefinition>> customMethods = new HashMap<>();

	public DefaultBeanDefinition(Class<?> klass) {
		this(ClassUtils.newInstance(klass));
	}

	public DefaultBeanDefinition(Object object) {
		this.single = object;
		this.beanClass = object.getClass();
		this.methodAccess = MethodAccess.get(beanClass);

		this.analysisField();
		this.analysisMethod();
	}

	@Override
	public String[] getNames() {
		return new String[] { beanClass.getName() };
	}

	@Override
	public Object getSingle() {
		return single;
	}

	@Override
	public Class<?> getBeanClass() {
		return beanClass;
	}

	private void analysisMethod() {
		// TODO Auto-generated method stub

	}

	private void analysisField() {
		FieldUtils.getAllField(beanClass).stream().filter(v -> v.isAnnotationPresent(Autowired.class) || v.isAnnotationPresent(Value.class)).forEach(v -> analysisAutowiredOrValue(v));
	}

	// 分析注入的类型
	private void analysisAutowiredOrValue(Field field) {
		final Class<?> fieldClass = field.getType();

		Value value = field.getAnnotation(Value.class);
		if (value == null) {
			// List类型的注入需求
			if (fieldClass == List.class) {
				autowiredFields.add(new ListFieldDefinition(field));
			}

			// Map类型的注入需求
			else if (fieldClass == Map.class) {
				autowiredFields.add(new MapFieldDefinition(field));
			}

			// 其他就当普通Bean处理...
			else {
				autowiredFields.add(new DefaultFieldDefinition(field));
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

	@Override
	public Object doAnalysisHandler(NoarkIoc noarkIoc) {
		// TODO Auto-generated method stub
		return null;
	}
}