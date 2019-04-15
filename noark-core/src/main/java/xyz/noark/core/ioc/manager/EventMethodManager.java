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
package xyz.noark.core.ioc.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import xyz.noark.core.event.Event;
import xyz.noark.core.ioc.wrap.method.EventMethodWrapper;

/**
 * 事件处理管理类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class EventMethodManager {
	private final Map<Class<? extends Event>, List<EventMethodWrapper>> handlers = new ConcurrentHashMap<>();

	private static final EventMethodManager INSTANCE = new EventMethodManager();

	private EventMethodManager() {}

	public static EventMethodManager getInstance() {
		return INSTANCE;
	}

	/**
	 * 获取事件订阅列表.
	 * 
	 * @param eventClass 事件源类型
	 * @return 订阅列表
	 */
	public List<EventMethodWrapper> getEventMethodWrappers(Class<? extends Event> eventClass) {
		return handlers.getOrDefault(eventClass, Collections.emptyList());
	}

	/**
	 * 注册事件处理方法.
	 * 
	 * @param eventWrapper 事件处理方法包装对象
	 */
	public void resetEventHandler(EventMethodWrapper eventWrapper) {
		handlers.computeIfAbsent(eventWrapper.getEventClass(), key -> new ArrayList<>()).add(eventWrapper);
	}

	/**
	 * 事件处理器排序执行
	 */
	public void sort() {
		handlers.values().forEach(v -> v.sort((h1, h2) -> h1.getOrder() - h2.getOrder()));
	}

	/**
	 * 扩展事件监听处理器.
	 * 
	 * @return 事件处理管理类单例
	 */
	public EventMethodManager listenerExtend() {
		final Map<Class<? extends Event>, List<EventMethodWrapper>> extend = new HashMap<>(32);
		// 查找一下所有父类，有监听那就要增强扩展
		for (Class<? extends Event> klass : handlers.keySet()) {
			for (Map.Entry<Class<? extends Event>, List<EventMethodWrapper>> e : handlers.entrySet()) {
				// klass 是 e.getKey() 的父类
				if (!klass.equals(e.getKey()) && e.getKey().isAssignableFrom(klass)) {
					extend.computeIfAbsent(klass, key -> new ArrayList<>()).addAll(e.getValue());
				}
			}
		}
		// 再把上面的扩展加进去...
		extend.forEach((k, v) -> handlers.computeIfAbsent(k, key -> new ArrayList<>()).addAll(v));
		return INSTANCE;
	}
}