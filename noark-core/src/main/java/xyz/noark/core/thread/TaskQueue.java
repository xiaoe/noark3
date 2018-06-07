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

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

/**
 * 任务处理队列.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class TaskQueue {
	private final Object lock = new Object();
	private final ExecutorService threadPool;
	private LinkedList<AsyncTask> queue;// 任务处理队列

	public TaskQueue(ExecutorService threadPool) {
		this.threadPool = threadPool;
		this.queue = new LinkedList<>();
	}

	/**
	 * 往任务队列里提交一个任务。
	 * 
	 * @param task 任务
	 */
	public void submit(AsyncTask task) {
		synchronized (lock) {
			queue.add(task);
			// 只有一个任务，那就是刚刚加的，直接开始执行...
			if (queue.size() == 1) {
				threadPool.execute(task);
			}
		}
	}

	/**
	 * 完成一个任务后续处理
	 */
	public void complete() {
		synchronized (lock) {
			// 移除已经完成的任务。
			queue.removeFirst();

			// 完成一个任务后，如果还有任务，则继续执行。
			if (!queue.isEmpty()) {
				this.threadPool.submit(queue.getFirst());
			}
		}
	}
}