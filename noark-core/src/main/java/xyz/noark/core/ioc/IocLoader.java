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
package xyz.noark.core.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import xyz.noark.core.annotation.Component;
import xyz.noark.core.annotation.Configuration;
import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.Repository;
import xyz.noark.core.annotation.Service;
import xyz.noark.core.annotation.StaticComponent;
import xyz.noark.core.annotation.TemplateConverter;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.ioc.definition.ComponentBeanDefinition;
import xyz.noark.core.ioc.definition.ConfigurationBeanDefinition;
import xyz.noark.core.ioc.definition.ControllerBeanDefinition;
import xyz.noark.core.ioc.definition.DefaultBeanDefinition;
import xyz.noark.core.ioc.definition.StaticComponentBeanDefinition;
import xyz.noark.core.ioc.scan.Resource;
import xyz.noark.core.ioc.scan.ResourceScanning;
import xyz.noark.util.ClassUtils;

/**
 * 加载IOC容器接管的Bean资源.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class IocLoader {
	private final HashMap<Class<?>, DefaultBeanDefinition> beans = new HashMap<>(1024);
	private final List<BeanDefinition> configurations = new ArrayList<>();
	private final List<StaticComponentBeanDefinition> staticcomponents = new ArrayList<>();

	IocLoader(String... packages) {
		ResourceScanning.scanPackage(packages, (resource) -> analysisResource(resource));
	}

	/**
	 * 查找所有实现类.
	 * 
	 * @param klass 接口
	 * @return 实现类集合
	 */
	protected List<DefaultBeanDefinition> findImpl(final Class<?> klass) {
		return beans.values().stream().filter(v -> klass.isInstance(v.getSingle())).collect(Collectors.toList());
	}

	private void analysisResource(Resource resource) {
		String resourceName = resource.getName();

		// 忽略 package-info.class
		if ("package-info.class".equals(resourceName)) {
			return;
		}

		// 忽略非Class文件
		if (!resourceName.endsWith(".class")) {
			return;
		}

		// Class快速载入
		analysisClass(ClassUtils.loadClass(resourceName.substring(0, resourceName.length() - 6).replaceAll("[/\\\\]", ".")));
	}

	// 分析Class
	private void analysisClass(Class<?> klass) {
		// 接口、内部类、枚举、注解和匿名类 直接忽略
		if (klass.isInterface() || klass.isMemberClass() || klass.isEnum() || klass.isAnnotation() || klass.isAnonymousClass()) {
			return;
		}

		// 抽象类和非Public的也忽略
		int modify = klass.getModifiers();
		if (Modifier.isAbstract(modify) || (!Modifier.isPublic(modify))) {
			return;
		}

		for (Annotation annotation : klass.getAnnotations()) {
			Class<? extends Annotation> annotationType = annotation.annotationType();

			// 配置类
			if (annotationType == Configuration.class) {
				analytical(klass, Configuration.class.cast(annotation));
			}

			// 模板转化器.
			else if (annotationType == TemplateConverter.class) {
				analytical(klass, TemplateConverter.class.cast(annotation));
			}

			// 协议入口控制类
			else if (annotationType == Controller.class) {
				analytical(klass, Controller.class.cast(annotation));
			}

			// 业务逻辑处理类
			else if (annotationType == Service.class) {
				analytical(klass, Service.class.cast(annotation));
			}

			// 数据存储功能类
			else if (annotationType == Repository.class) {
				analytical(klass, Repository.class.cast(annotation));
			}

			// 静态组件
			else if (annotationType == StaticComponent.class) {
				analytical(klass, StaticComponent.class.cast(annotation));
			}

			// 没有归属
			else if (annotationType == Component.class) {
				analytical(klass, Component.class.cast(annotation));
			}
		}
	}

	// 静态组件
	private void analytical(Class<?> klass, StaticComponent component) {
		staticcomponents.add(new StaticComponentBeanDefinition(klass).init());
	}

	// 组件类型的Bean...
	private void analytical(Class<?> klass, Component component) {
		beans.put(klass, new ComponentBeanDefinition(klass, component).init());
	}

	private void analytical(Class<?> klass, Repository cast) {
		beans.put(klass, new DefaultBeanDefinition(klass).init());
	}

	private void analytical(Class<?> klass, Service cast) {
		beans.put(klass, new DefaultBeanDefinition(klass).init());
	}

	private void analytical(Class<?> klass, Controller controller) {
		beans.put(klass, new ControllerBeanDefinition(klass, controller).init());
	}

	private void analytical(Class<?> klass, TemplateConverter converter) {
		ConvertManager.getInstance().regist(klass, converter);
	}

	private void analytical(Class<?> klass, Configuration configuration) {
		configurations.add(new ConfigurationBeanDefinition(klass).init());
	}

	public HashMap<Class<?>, DefaultBeanDefinition> getBeans() {
		return beans;
	}

	public List<BeanDefinition> getConfigurations() {
		return configurations;
	}

	public List<StaticComponentBeanDefinition> getStaticComponents() {
		return staticcomponents;
	}
}