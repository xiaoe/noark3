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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Map工具类测试用例.
 *
 * @since 3.2.4
 * @author 小流氓(176543888@qq.com)
 */
public class MapUtilsTest {

	@Test
	public void testOf() {
		Map<Integer, Integer> map = MapUtils.of(1, 1);
		assertTrue(map.get(1) == 1);
	}

	@Test
	public void testGetOrMaxKey() {
		Map<Integer, Integer> map = new HashMap<>(16);
		map.put(1, 10);
		map.put(2, 15);
		map.put(3, 20);
		map.put(4, 25);
		map.put(5, 30);
		map.put(10, 100);
		assertTrue(MapUtils.getOrMaxKey(map, 1) == 10);
		assertTrue(MapUtils.getOrMaxKey(map, 2) == 15);
		assertTrue(MapUtils.getOrMaxKey(map, 4) == 25);
		assertTrue(MapUtils.getOrMaxKey(map, 5) == 30);
		assertTrue(MapUtils.getOrMaxKey(map, 6) == 30);
		assertTrue(MapUtils.getOrMaxKey(map, 12) == 100);
	}

	@Test
	public void testAddValue() {
		Map<Integer, Integer> map = new HashMap<>(16);
		assertTrue(MapUtils.addValue(map, 1, 0) == 0);
		assertTrue(MapUtils.addValue(map, 1, 1) == 1);
		assertTrue(MapUtils.addValue(map, 1, 1) == 2);
		assertTrue(MapUtils.addValue(map, 1, 0) == 2);
	}
}