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
package xyz.noark.core.ioc.definition.method;

import java.lang.reflect.Method;

import xyz.noark.core.annotation.controller.EventListener;
import xyz.noark.core.event.Event;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.definition.ControllerBeanDefinition;
import xyz.noark.core.util.ArrayUtils;
import xyz.noark.reflectasm.MethodAccess;

/**
 * 事件处理入口的定义.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class EventMethodDefinition extends SimpleMethodDefinition {
	private final EventListener eventListener;
	private final Class<? extends Event> eventClass;

	@SuppressWarnings("unchecked")
	public EventMethodDefinition(MethodAccess methodAccess, Method method, EventListener eventListener, ControllerBeanDefinition beanDefinition) {
		super(methodAccess, method);
		this.eventListener = eventListener;

		Class<? extends Event> eventClass = eventListener.value();
		if (eventClass == Event.class) {
			final String className = beanDefinition.getBeanClass().getName();
			Class<?>[] array = method.getParameterTypes();
			if (ArrayUtils.isEmpty(array)) {
				throw new ServerBootstrapException("事件监听处理方法，没有申请事件类型，也没有事件参数 class=" + className + ", method=" + method.getName());
			} else if (array.length > 1) {
				throw new ServerBootstrapException("事件监听处理方法，有且只能有一个参数 class=" + className + ", method=" + method.getName());
			} else if (!Event.class.isAssignableFrom(array[0])) {
				throw new ServerBootstrapException("事件监听处理方法，参数类型必需实现Event接口 class=" + className + ", method=" + method.getName());
			} else {
				eventClass = (Class<? extends Event>) array[0];
			}
		}
		this.eventClass = eventClass;
	}

	public Class<? extends Event> getEventClass() {
		return eventClass;
	}

	public boolean isAsync() {
		return eventListener.async();
	}

	public boolean isPrintLog() {
		return eventListener.printLog();
	}
}