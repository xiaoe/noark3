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

import org.junit.Test;

import xyz.noark.core.lang.Point;

/**
 * 数学计算相关测试
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
public class MathUtilsTest {

	@Test
	public void testAddExactIntInt() {
		assertTrue(MathUtils.addExact(1, 1) == 2);
		assertTrue(MathUtils.addExact(1, Integer.MAX_VALUE) == Integer.MAX_VALUE);
	}

	@Test
	public void testAddExactLongLong() {
		assertTrue(MathUtils.addExact(1L, 1L) == 2);
		assertTrue(MathUtils.addExact(1, Long.MAX_VALUE) == Long.MAX_VALUE);
	}

	@Test
	public void testMultiplyExactIntInt() {
		assertTrue(MathUtils.multiplyExact(2, 2) == 4);
		assertTrue(MathUtils.multiplyExact(2, Integer.MAX_VALUE) == Integer.MAX_VALUE);
	}

	@Test
	public void testMultiplyExactLongLong() {
		assertTrue(MathUtils.multiplyExact(2L, 2L) == 4);
		assertTrue(MathUtils.multiplyExact(2L, Long.MAX_VALUE) == Long.MAX_VALUE);
	}

	@Test
	public void testDistance() {
		assertTrue(MathUtils.distance(0, 0, 3, 4) == 5.0D);
		assertTrue(MathUtils.distance(0.0D, 0.0D, 3.0D, 4.0D) == 5.0D);
		assertTrue(MathUtils.distance(Point.valueOf(0, 0), Point.valueOf(3, 4)) == 5.0D);
	}
}