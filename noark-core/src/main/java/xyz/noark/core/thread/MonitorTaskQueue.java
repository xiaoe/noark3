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
package xyz.noark.core.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 带有监控功能的任务处理队列.
 * <p>
 * 监控任务执行时间，如果超时了需要强制中断这个任务
 * 
 * @since 3.3.3
 * @author 小流氓(176543888@qq.com)
 */
public class MonitorTaskQueue extends TaskQueue {
	/** 独立监控线程池,可能没有实现，那就会业务线程去监控 */
	private final MonitorThreadPool monitorThreadPool;
	/** 监控一个执行超时时间(单位：秒) */
	private final int timeout;
	/** 任务执行超时输出线程执行堆栈信息，默认开启 */
	private final boolean outputStack;

	public MonitorTaskQueue(MonitorThreadPool monitorThreadPool, ExecutorService threadPool, int timeout, boolean outputStack) {
		super(threadPool);
		this.timeout = timeout;
		this.outputStack = outputStack;
		this.monitorThreadPool = monitorThreadPool;
	}

	@Override
	protected void exec(AsyncTask task) {
		this.monitor(this.getThreadPool().submit(task), task);
	}

	private void monitor(Future<?> future, AsyncTask task) {
		// 如果有独立的监控线程，则不会使用业务线程，不然所有业务线程都挂了，这个监控也就没有用了...
		final ExecutorService monitorService = monitorThreadPool == null ? this.getThreadPool() : monitorThreadPool.getMonitorService();
		if (!monitorService.isShutdown()) {
			monitorService.execute(() -> startMonitorExecTimeoutTask(future, task));
		}
	}

	/**
	 * 开启监控执行超时任务.
	 * <p>
	 * 如果超时了，记录超时信息，继续添加监控
	 * 
	 * @param future 任务句柄
	 * @param task 任务对象
	 */
	private void startMonitorExecTimeoutTask(Future<?> future, AsyncTask task) {
		try {
			future.get(timeout, TimeUnit.SECONDS);
		} catch (Exception e1) {
			task.logExecTimeoutInfo(outputStack);
			this.monitor(future, task);
		}
	}
}