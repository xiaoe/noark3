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

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 随机工具类测试.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class RandomUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@Test
	public void testNextIntInt() {
		assertTrue(RandomUtils.nextInt(1, 2) == 1);
		assertTrue(RandomUtils.nextInt(0, 2) >= 0);
	}

	@Test
	public void testRandomListListOfT() {
		List<Integer> list = new ArrayList<>();
		assertTrue(RandomUtils.randomList(list) == null);

		list.add(1);
		assertTrue(RandomUtils.randomList(list) == 1);
	}

	@Test
	public void testRandomByWeight() {
		List<TestData> data = new ArrayList<>();
		TestData e = new TestData();
		e.setId(1);
		e.setWeight(RandomUtils.nextInt(100));
		data.add(e);

		TestData random = RandomUtils.randomByWeight(data, TestData::getWeight);
		assertTrue(random.getId() == e.getId());
	}

	static class TestData {
		private int id;
		private int weight;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}

		@Override
		public String toString() {
			return "TestData [id=" + id + ", weight=" + weight + "]";
		}
	}
}