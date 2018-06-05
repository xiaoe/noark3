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

import static xyz.noark.log.LogHelper.logger;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * Noark-IOC的核心容器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class NoarkIoc implements Ioc {
	// 容器中所有托管的Bean对象.
	private final ConcurrentHashMap<Class<?>, Object> singletons = new ConcurrentHashMap<>(512);;

	public NoarkIoc(String... packages) {
		logger.debug("init ioc, packages={}", new Object[] { packages });

		IocLoader loader = new IocLoader(packages);

		try (IocMaking making = new IocMaking(loader)) {
			// 优先构建Configuration里的显示申明的Bean.
			this.finishBeanInitialization(loader, making, loader.getConfigurations());

			// 完成其他Bean的初始化和依赖注入的关系
			this.finishBeanInitialization(loader, making, loader.getBeans().values());
		}

		// 完成分析Bean的功能用途
		this.finishBeanAnalysis(loader);
	}

	/**
	 * 分析对象，处理并提取到对应的缓存区.
	 * 
	 * @param loader IOC加载
	 */
	private void finishBeanAnalysis(IocLoader loader) {
		loader.getBeans().forEach((k, v) -> v.doAnalysisHandler(this));
		this.singletons.putAll(loader.getBeans().values().stream().collect(Collectors.toMap(BeanDefinition::getBeanClass, v -> v.getSingle())));
	}

	// 初始化和依赖注入的关系
	private void finishBeanInitialization(IocLoader loader, IocMaking making, Collection<? extends BeanDefinition> configurations) {
		loader.getBeans().forEach((k, bean) -> bean.injection(making));
	}

	@Override
	public <T> T get(Class<T> klass) {
		logger.debug("Get IOC Bean class={}", klass.getName());
		return klass.cast(singletons.get(klass));
	}

	public void invokeCustomAnnotationMethod(Class<PostConstruct> class1) {
		// TODO Auto-generated method stub
		
	}
}
