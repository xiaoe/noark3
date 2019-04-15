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
package xyz.noark.core.util;

import static org.junit.Assert.assertTrue;

import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import xyz.noark.benchmark.Benchmark;

/**
 * 字符串工具类测试用例.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class StringUtilsTest {
	private final Benchmark benchmark = new Benchmark(1000);

	@Test
	public void testIsEmpty() {
		assertTrue(StringUtils.isEmpty(""));
		assertTrue(StringUtils.isEmpty(null));
		assertTrue(!StringUtils.isEmpty(" "));
		assertTrue(!StringUtils.isEmpty("test"));
		assertTrue(!StringUtils.isEmpty("  test "));
	}

	@Test
	public void testIsNotEmpty() {
		assertTrue(!StringUtils.isNotEmpty(""));
		assertTrue(!StringUtils.isNotEmpty(null));
		assertTrue(StringUtils.isNotEmpty(" "));
		assertTrue(StringUtils.isNotEmpty("test"));
		assertTrue(StringUtils.isNotEmpty("  test "));
	}

	@Test
	public void testLength() {
		assertTrue(StringUtils.length(null) == 0);
		assertTrue(StringUtils.length("") == 0);
		assertTrue(StringUtils.length(" ") == 1);
		assertTrue(StringUtils.length("test") == 4);
		assertTrue(StringUtils.length("我爱中国") == 8);
	}

	@Test
	public void testSplit() throws Exception {
		assertTrue(StringUtils.split("1,2,3", ",").length == 3);
		assertTrue(StringUtils.split("1,2,3,", ",").length == 3);
		assertTrue(StringUtils.split("1,2,3,,,", ",").length == 3);
		assertTrue(StringUtils.split("127.0.0.1", ".").length == 4);
		assertTrue(testSplitByJdk("127.0.0.1", "\\.").length == 4);

		benchmark.doSomething("Jdk:", () -> StringUtils.split("127.0.0.1", "."));
		benchmark.doSomething("Noark:", () -> testSplitByJdk("127.0.0.1", "\\."));
	}

	private String[] testSplitByJdk(String ip, String regex) {
		return ip.split(regex);
	}

	@Test
	public void asciiSizeInBytes() throws Exception {
		long value = Long.MIN_VALUE;
		assertTrue(StringUtils.asciiSizeInBytes(value) == String.valueOf(value).length());
		benchmark.doSomething("OKIO的方案:", () -> StringUtils.asciiSizeInBytes(value));
		benchmark.doSomething("成龙的方案:", () -> String.valueOf(value).length());
	}

	@Test
	public void test() throws Exception {
		String[] strings = new String[] { "aaaaaaaaaaaaaaaaaaa", "bbbbbbbbbbbbbbbbbbbbbbbbb" };
		benchmark.doSomething("join:", () -> StringUtils.build(",", "{", "}", strings));
		benchmark.doSomething("join1:", () -> join1(",", "{", "}", strings));
		benchmark.doSomething("join2:", () -> join2(",", "{", "}", strings));
	}

	private static String join1(String delimiter, String prefix, String suffix, String... strings) {
		StringJoiner result = new StringJoiner(delimiter, prefix, suffix);
		for (String str : strings) {
			result.add(str);
		}
		return result.toString();
	}

	private static String join2(String delimiter, String prefix, String suffix, String... strings) {
		return Stream.of(strings).collect(Collectors.joining(delimiter, prefix, suffix));
	}

	@Test
	public void testFormat() throws Exception {
		assertTrue("hahatrue,false,10000,false".equals(StringUtils.format("haha{1},{2},{0},{2}", 10000, true, false, 4)));
	}

}