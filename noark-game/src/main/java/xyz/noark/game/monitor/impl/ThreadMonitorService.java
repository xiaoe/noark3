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
package xyz.noark.game.monitor.impl;

import static xyz.noark.log.LogHelper.logger;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

import xyz.noark.game.monitor.AbstractMonitorService;

/**
 * 线程监控服务.
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public class ThreadMonitorService extends AbstractMonitorService {

	public ThreadMonitorService() {}

	@Override
	protected long getInitialDelay() {
		return 1;
	}

	@Override
	protected long getDelay() {
		return 30;
	}

	@Override
	protected TimeUnit getUnit() {
		return TimeUnit.MINUTES;
	}

	@Override
	protected void exe() throws Exception {
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		int run = 0;
		int blocked = 0;
		int waiting = 0;
		for (long threadId : threadMXBean.getAllThreadIds()) {
			ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
			switch (threadInfo.getThreadState()) {
			case RUNNABLE:
				run++;
				break;
			// 受阻塞并等待某个监视器锁的线程处于这种状态
			case BLOCKED:
				blocked++;
				break;
			// 无限期地等待另一个线程来执行某一特定操作的线程处于这种状态。
			case WAITING:
				waiting++;
				break;
			default:
				break;
			}
		}
		logger.info("运行状态线程数：{}, 阻塞状态线程数:{}, 等待状态线程数：{}", run, blocked, waiting);
	}
}