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

import static xyz.noark.log.LogHelper.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 写入测试.
 *
 * @since 3.3.6
 * @author 小流氓(176543888@qq.com)
 */
public class LogBenchmark {

	public static void main(String[] args) throws InterruptedException {
		HashMap<String, String> config = new HashMap<>(16, 1);
		config.put("log.console", "false");
		config.put("log.path", "/data/log/game/1/game.{yyyy-MM-dd-HH}.log");
		LogManager.init(config);

		long bigen = System.nanoTime();

		StringBuilder sb = new StringBuilder(1024);
		for (int i = 0; i < 1024; i++) {
			sb.append("x");
		}
		final String xx = sb.toString();
		CountDownLatch latch = new CountDownLatch(5);
		ArrayList<Thread> threads = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10_0000; i++) {
						logger.error(xx, i);
					}
					latch.countDown();
				}
			});
			thread.start();
			threads.add(thread);
		}
		latch.await();
		LogManager.shutdown();
		System.out.println((System.nanoTime() - bigen) / 100_0000F + " ms");
	}
}