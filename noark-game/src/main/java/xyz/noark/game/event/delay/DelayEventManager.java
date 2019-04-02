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
package xyz.noark.game.event.delay;

import static xyz.noark.log.LogHelper.logger;

import java.util.List;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Service;
import xyz.noark.core.event.Event;
import xyz.noark.core.exception.HackerException;
import xyz.noark.core.ioc.manager.EventMethodManager;
import xyz.noark.core.ioc.manager.ScheduledMethodManager;
import xyz.noark.core.ioc.wrap.method.EventMethodWrapper;
import xyz.noark.core.ioc.wrap.method.ScheduledMethodWrapper;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.game.event.EventManager;

/**
 * 一个提供延迟执行事件功能的实现类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Service
public class DelayEventManager implements EventManager {
	private static final EventMethodManager MANAGER = EventMethodManager.getInstance();
	private static final ScheduledMethodManager SCHEDULED_MANAGER = ScheduledMethodManager.getInstance();
	private final DelayEventThread handler = new DelayEventThread(this);

	@Autowired
	private static ThreadDispatcher threadDispatcher;

	public void init() {
		handler.start();
		this.initScheduled();
	}

	// 延迟任务
	private void initScheduled() {
		for (ScheduledMethodWrapper sch : SCHEDULED_MANAGER.getHandlers()) {
			ScheduledEvent event = new ScheduledEvent();
			event.setId(sch.getId());
			event.setEndTime(sch.nextExecutionTime());
			this.publish(event);
		}
	}

	public void destroy() {
		handler.shutdown();
	}

	@Override
	public void publish(Event event) {
		this.notifyListeners(event);
	}

	void notifyListeners(Event event) {
		List<EventMethodWrapper> handlers = MANAGER.getEventMethodWrappers(event.getClass());
		if (handlers.isEmpty()) {
			logger.warn("No subscription event. class={}", event.getClass());
			return;
		}

		for (EventMethodWrapper handler : handlers) {
			try {
				// 异步执行，投递进线程池中派发
				if (handler.isAsync()) {
					threadDispatcher.dispatchEvent(handler, event);
				}
				// 有一些特别的情况需要同步执行.
				else {
					handler.invoke(event);
				}
			} catch (Exception e) {
				logger.warn("handle event exception. {}", e);
			}
		}
	}

	void notifyScheduledHandler(ScheduledEvent event) {
		final ScheduledMethodWrapper method = SCHEDULED_MANAGER.getHandler(event.getId());
		// 派发延迟任务...
		threadDispatcher.dispatchScheduled(method);

		// 修正时间，进行下一次事件发布...
		event.setEndTime(method.nextExecutionTime());
		this.publish(event);
	}

	@Override
	public void publish(DelayEvent event) {
		// 未配置这个结束时间，会死人的....
		if (event.getEndTime() == null) {
			throw new HackerException("未配置延迟事件的结束时间. class=" + event.getClass().getName());
		}
		handler.addDelayEvent(event);
	}

	@Override
	public boolean remove(DelayEvent event) {
		return handler.remove(event);
	}
}