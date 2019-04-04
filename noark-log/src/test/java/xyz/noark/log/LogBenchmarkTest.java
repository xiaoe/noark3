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

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import xyz.noark.benchmark.BenchmarkCallback;

/**
 * 发现一个Logger测试结论，虽然感觉他们的结论不靠普，还是拿来跑跑看.
 *
 * @since 3.2.6
 * @author 小流氓(176543888@qq.com)
 */
public class LogBenchmarkTest {
	private final static org.apache.logging.log4j.Logger LOG4J2 = org.apache.logging.log4j.LogManager.getLogger(LogTest.class);
	private final static xyz.noark.log.Logger NOARKLOG = xyz.noark.log.LogManager.getDefaultLogger();

	/**
	 * 这里只是测试日志的并发写入速度，并不管输出到文件，那我们就关闭所有日志的文件输出和控制台输出.
	 * <p>
	 * 要注意的是：各种日志输出格式要一致，不然就有失公允.
	 */
	public static void main(String[] args) throws Exception {

		// 由于Noark的日志输出是固定格式，所以其他框架的配置靠向此格式
		HashMap<String, String> config = new HashMap<>(16, 1);
		config.put("log.console", "false");
		config.put("log.path", "/data/log/benchmark/test.{yyyy-MM-dd-HH}.log");
		LogManager.init(config);

		BenchmarkCallback log4j2 = () -> LOG4J2.info("info---------------{}", 1);
		BenchmarkCallback noarkLog = () -> NOARKLOG.info("info---------------{}", 2);

		for (int i = 4; i < 5; i++) {
			System.out.println("============== >> " + i);
			int messageSize = 100_0000;// 消息总量
			int threadSize = 10 + i * 10;// 并发线程数
			doBenchmark("Log4j2", messageSize, threadSize, log4j2);
			doBenchmark("NoarkLog", messageSize, threadSize, noarkLog);
			System.out.println();
		}

		LogManager.shutdown();
	}

	private static final void doBenchmark(String name, int messageSize, int threadSize, BenchmarkCallback callback) throws InterruptedException {
		final int everySize = messageSize / threadSize;

		final CountDownLatch cdl = new CountDownLatch(threadSize);
		long startTime = System.currentTimeMillis();
		for (int ts = 0; ts < threadSize; ts++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int es = 0; es < everySize; es++) {
						try {
							callback.doSomething();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					cdl.countDown();
				}
			}, "log-" + ts).start();
		}

		cdl.await();
		long endTime = System.currentTimeMillis();
		System.out.println(name + ":messageSize = " + messageSize + ",threadSize = " + threadSize + ",costTime = " + (endTime - startTime) + "ms");
	}
}