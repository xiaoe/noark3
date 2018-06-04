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
package xyz.noark.log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 日志中心.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
class LogCenter {

	private static final LogCenter instance = new LogCenter();
	/** 异步日志线程 */
	private final ExecutorService ANSYC_LOG_EXEC;

	private LogCenter() {
		ANSYC_LOG_EXEC = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "ansyc-log");
				t.setDaemon(true);
				return t;
			}
		});
	}

	static LogCenter getInstance() {
		return instance;
	}

	void execute(LogExecutor info) {
		ANSYC_LOG_EXEC.execute(info);
	}

	public void shutdown() {
		ANSYC_LOG_EXEC.shutdown();

		try {// 最大等待时间为1分钟...
			ANSYC_LOG_EXEC.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {}
	}
}