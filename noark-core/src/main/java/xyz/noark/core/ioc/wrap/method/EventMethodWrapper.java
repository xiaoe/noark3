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
package xyz.noark.core.ioc.wrap.method;

import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.event.Event;
import xyz.noark.core.ioc.definition.method.EventMethodDefinition;
import xyz.noark.reflectasm.MethodAccess;

/**
 * 事件处理方法的包装类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class EventMethodWrapper extends AbstractControllerMethodWrapper {
	private final Class<? extends Event> eventClass;
	private final boolean async;

	public EventMethodWrapper(MethodAccess methodAccess, Object single, EventMethodDefinition emd, ExecThreadGroup threadGroup, Class<?> controllerMasterClass) {
		super(methodAccess, single, emd.getMethodIndex(), threadGroup, controllerMasterClass.getName(), emd.getOrder(), "event(" + emd.getEventClass().getSimpleName() + ")");
		this.eventClass = emd.getEventClass();
		this.printLog = emd.isPrintLog();
		this.async = emd.isAsync();
	}

	public Class<? extends Event> getEventClass() {
		return eventClass;
	}

	public boolean isAsync() {
		return async;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}