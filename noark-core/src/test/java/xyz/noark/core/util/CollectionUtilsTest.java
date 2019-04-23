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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import xyz.noark.core.lang.PairMap;

/**
 * 集合工具类测试
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
public class CollectionUtilsTest {
	private static final int MAX = 20;
	List<Pet> list = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < MAX; i++) {
			Pet data = new Pet();
			data.id = i;
			data.level = i * 2;
			data.exp = i * i;
			list.add(data);
		}
	}

	@Test
	public void testIsEmpty() {
		assertFalse(CollectionUtils.isEmpty(list));
	}

	@Test
	public void testIsNotEmpty() {
		assertTrue(CollectionUtils.isNotEmpty(list));
	}

	@Test
	public void testGroupingBy() {
		PairMap<Integer, Integer, List<Pet>> result = CollectionUtils.groupingBy(list, Pet::getId, Pet::getLevel);
		assertTrue(result.size() == 2);
		assertTrue(result.get(1, 1).size() == 10);
	}

	@Test
	public void testMatching() {
		assertTrue(CollectionUtils.matching(list, Pet::getExp, 0).orElse(null).getId() == 0);
		assertTrue(CollectionUtils.matching(list, Pet::getExp, 1).orElse(null).getId() == 1);
		assertTrue(CollectionUtils.matching(list, Pet::getExp, 25L).orElse(null).getId() == 5);
		assertTrue(CollectionUtils.matching(list, Pet::getExp, 100000).orElse(null).getId() == 19);

		assertTrue(CollectionUtils.matching(list, Pet::getLevel, 0).orElse(null).getId() == 0);
		assertTrue(CollectionUtils.matching(list, Pet::getLevel, 1).orElse(null).getId() == 0);
		assertTrue(CollectionUtils.matching(list, Pet::getLevel, 25).orElse(null).getId() == 12);
		assertTrue(CollectionUtils.matching(list, Pet::getLevel, 100000).orElse(null).getId() == 19);
	}

	class Pet {
		private int id;
		private int level;
		private long exp;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public long getExp() {
			return exp;
		}

		public void setExp(long exp) {
			this.exp = exp;
		}

		@Override
		public String toString() {
			return "Pet [id=" + id + ", level=" + level + ", exp=" + exp + "]";
		}
	}
}
