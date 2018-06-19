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
import java.lang.reflect.Method;
import java.util.ArrayList;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.controller.EventListener;
import xyz.noark.core.annotation.controller.PacketMapping;
import xyz.noark.core.ioc.NoarkIoc;
import xyz.noark.core.ioc.definition.method.EventMethodDefinition;
import xyz.noark.core.ioc.definition.method.PacketMethodDefinition;
import xyz.noark.core.ioc.manager.EventMethodManager;
import xyz.noark.core.ioc.manager.PacketMethodManager;
import xyz.noark.core.ioc.wrap.method.EventMethodWrapper;
import xyz.noark.core.ioc.wrap.method.PacketMethodWrapper;

/**
 * 控制器的Bean定义描述类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class ControllerBeanDefinition extends DefaultBeanDefinition {
	private final Controller controller;
	private final ArrayList<PacketMethodDefinition> pmds = new ArrayList<>();
	private final ArrayList<EventMethodDefinition> emds = new ArrayList<>();

	public ControllerBeanDefinition(Class<?> klass, Controller controller) {
		super(klass);
		this.controller = controller;
	}

	@Override
	protected void analysisMthodByAnnotation(Class<? extends Annotation> annotationType, Annotation annotation, Method method) {
		// 客户端过来的协议入口.
		if (annotationType == PacketMapping.class) {
			pmds.add(new PacketMethodDefinition(methodAccess, method, PacketMapping.class.cast(annotation)));
		}
		// 事件监听
		else if (annotationType == EventListener.class) {
			emds.add(new EventMethodDefinition(methodAccess, method, EventListener.class.cast(annotation)));
		}
	}

	@Override
	public void doAnalysisFunction(NoarkIoc noarkIoc) {
		super.doAnalysisFunction(noarkIoc);

		this.doAnalysisPacketHandler(noarkIoc);

		this.doAnalysisEventHandler(noarkIoc);
	}

	// 分析事件处理入口.
	private void doAnalysisEventHandler(NoarkIoc ioc) {
		final EventMethodManager manager = EventMethodManager.getInstance();
		emds.forEach(emd -> manager.resetEventHander(new EventMethodWrapper(methodAccess, single, emd, controller)));
	}

	// 分析一下封包处理方法.
	private void doAnalysisPacketHandler(NoarkIoc noarkIoc) {
		final PacketMethodManager manager = PacketMethodManager.getInstance();
		pmds.forEach(pmd -> manager.resetPacketHandler(new PacketMethodWrapper(methodAccess, single, pmd, controller)));
	}
}