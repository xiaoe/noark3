/*
 * Copyright © 2015 www.noark.xyz All Rights Reserved.
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

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import xyz.noark.benchmark.Benchmark;

/**
 * 日志测试类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class LogTest {
	private final static org.apache.logging.log4j.Logger LOG4J2 = org.apache.logging.log4j.LogManager.getLogger(LogTest.class);
	private final static org.slf4j.Logger LOGBACK = LoggerFactory.getLogger(LogTest.class);

	@Before
	public void setUp() throws Exception {
		HashMap<String, String> config = new HashMap<>(16, 1);
		config.put("log.console", "true");
		LogManager.init(config);
	}

	@After
	public void tearDown() throws Exception {
		LogManager.shutdown();
	}

	@Test
	public void test() {
		logger.debug("haha{}", 123, "abc");
		logger.info("haha");
		logger.warn("123123123, {},{}", 1, null);
		logger.error("123123123", new RuntimeException("123"));
	}

	private final Benchmark benchmark = new Benchmark(10_0000);

	@Test
	public void testBenchmark() throws Exception {
		benchmark.doSomething("noark log:", () -> logger.error("test={},{},{}", 123, "abc", true));
		benchmark.doSomething("log4j2:", () -> LOG4J2.error("test={},{},{}", 123, "abc", true));
		benchmark.doSomething("logback:", () -> LOGBACK.error("test={},{},{}", 123, "abc", true));
	}
}