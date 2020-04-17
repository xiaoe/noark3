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

import java.util.LinkedList;
import java.util.List;

import xyz.noark.core.exception.IllegalExpressionException;
import xyz.noark.core.util.StringUtils;

/**
 * 一种自由编排的int类型所表示的范围集合
 *
 * @since 3.3.4
 * @author 小流氓[176543888@qq.com]
 */
public class IntRange {
	/** 区间列表 */
	private final List<IntSection> sectionList = new LinkedList<>();
	/** 是否匹配全部 */
	private boolean flag = false;

	public IntRange(String expression) {
		this.analysis(expression);
	}

	/**
	 * 解析表达式
	 * 
	 * @param expression 表达式
	 */
	private void analysis(String expression) {
		if (StringUtils.ASTERISK.equals(expression)) {
			this.flag = true;
			return;
		}

		if (StringUtils.isBlank(expression)) {
			return;
		}

		for (String x : StringUtils.split(expression, StringUtils.COMMA)) {
			if (StringUtils.isBlank(x)) {
				continue;
			}

			// 切割
			String[] array = StringUtils.split(x, "-");
			// 只有一个值
			if (array.length == 1) {
				this.sectionList.add(new IntSection(parseInt(array[0])));
			}
			// 两个值，区间
			else if (array.length == 2) {
				this.sectionList.add(new IntSection(parseInt(array[0]), parseInt(array[1])));
			}
			// 异常情况
			else {
				throw new IllegalExpressionException("数字区间表达式格式错误：" + expression);
			}
		}
	}

	private Integer parseInt(String data) {
		try {
			return Integer.parseInt(data);
		} catch (Exception e) {
			// 有一种时间表达式里有星期的配置，需要容错处理...
			return Integer.parseInt(data.substring(1));
		}
	}

	/**
	 * 检测指定元素是不是在这个自由编排的范围内.
	 * 
	 * @param element 指定元素
	 * @return 如果存在则返回true
	 */
	public boolean contains(final int element) {
		return flag ? true : sectionList.stream().anyMatch(v -> v.contains(element));
	}

	/**
	 * Int数字区间.
	 * 
	 * @since 3.3.4
	 * @author 小流氓[176543888@qq.com]
	 */
	private class IntSection {
		private final int min;
		private final int max;

		public IntSection(int value) {
			this(value, value);
		}

		public IntSection(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public boolean contains(int element) {
			return min <= element && element <= max;
		}

		@Override
		public String toString() {
			return "IntSection [min=" + min + ", max=" + max + "]";
		}
	}
}