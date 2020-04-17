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
package xyz.noark.core.lang;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 一种可变Int类型的实现测试
 *
 * @since 3.2
 * @author 小流氓[176543888@qq.com]
 */
public class MutableIntTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@Before
	public void setUp() throws Exception {}

	@Test
	public void testHashCode() {
		MutableInt i = new MutableInt(1);
		assertTrue(i.hashCode() == 1);
	}

	@Test
	public void testIntValue() {
		MutableInt i = new MutableInt(1);
		assertTrue(i.intValue() == 1);
	}

	@Test
	public void testLongValue() {
		MutableInt i = new MutableInt(1);
		assertTrue(i.longValue() == 1L);
	}

	@Test
	public void testFloatValue() {
		MutableInt i = new MutableInt(1);
		assertTrue(i.floatValue() == 1F);
	}

	@Test
	public void testDoubleValue() {
		MutableInt i = new MutableInt(1);
		assertTrue(i.doubleValue() == 1D);
	}

	@Test
	public void testMutableIntString() {
		MutableInt i = new MutableInt("1");
		assertTrue(i.intValue() == 1);
	}

	@Test
	public void testGetValue() {
		MutableInt i = new MutableInt("1");
		assertTrue(i.getValue() == 1);
	}

	@Test
	public void testSetValueInt() {
		MutableInt i = new MutableInt("1");
		i.setValue(0);
		assertTrue(i.getValue() == 0);
	}

	@Test
	public void testEqualsObject() {
		MutableInt x1 = new MutableInt("1");
		MutableInt x2 = new MutableInt(1);
		assertTrue(x1.equals(x2));
	}

	@Test
	public void testToString() {
		MutableInt i = new MutableInt("1");
		assertTrue("1".equals(i.toString()));
	}

	@Test
	public void testGetAndIncrement() {
		MutableInt i = new MutableInt("1");
		assertTrue(i.getAndIncrement() == 1);
	}

	@Test
	public void testGetAndDecrement() {
		MutableInt i = new MutableInt("1");
		assertTrue(i.getAndDecrement() == 1);
	}

	@Test
	public void testGetAndAdd() {
		MutableInt i = new MutableInt(0);
		assertTrue(i.getAndAdd(1) == 0);
	}

	@Test
	public void testIncrementAndGet() {
		MutableInt i = new MutableInt(0);
		assertTrue(i.incrementAndGet() == 1);
	}

	@Test
	public void testDecrementAndGet() {
		MutableInt i = new MutableInt(1);
		assertTrue(i.decrementAndGet() == 0);
	}

	@Test
	public void testAddAndGet() {
		MutableInt i = new MutableInt(0);
		assertTrue(i.addAndGet(1) == 1);
	}
}