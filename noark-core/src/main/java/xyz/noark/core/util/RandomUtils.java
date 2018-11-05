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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToIntFunction;

/**
 * 随机数相关操作工具类.
 * <p>
 * 本工具类中统一以{@link ThreadLocalRandom}为基础的封装
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class RandomUtils {

	/**
	 * 返回一个随机Boolean值.
	 * 
	 * @return 随机Boolean值
	 */
	public static boolean nextBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	/**
	 * 返回一个0到指定区间的随机数字.
	 * <p>
	 * 0 &lt;= random &lt; bound
	 *
	 * @param bound 最大值（不包含）
	 * @return 返回一个0到指定区间的随机数字
	 */
	public static int nextInt(int bound) {
		return ThreadLocalRandom.current().nextInt(bound);
	}

	/**
	 * 返回一个指定区间的随机数字.
	 * <p>
	 * origin &lt;= random &lt; bound
	 *
	 * @param origin 最小值（包含）
	 * @param bound 最大值（不包含）
	 * @return 返回一个指定区间的随机数字
	 */
	public static int nextInt(int origin, int bound) {
		return ThreadLocalRandom.current().nextInt(origin, bound);
	}

	/**
	 * 返回一个0到指定区间的随机数字.
	 * <p>
	 * 0 &lt;= random &lt; bound
	 *
	 * @param bound 最大值（不包含）
	 * @return 返回一个0到指定区间的随机数字
	 */
	public static long nextLong(long bound) {
		return ThreadLocalRandom.current().nextLong(bound);
	}

	/**
	 * 返回一个指定区间的随机数字.
	 * <p>
	 * origin &lt;= random &lt; bound
	 *
	 * @param origin 最小值（包含）
	 * @param bound 最大值（不包含）
	 * @return 返回一个指定区间的随机数字
	 */
	public static long nextLong(int origin, int bound) {
		return ThreadLocalRandom.current().nextLong(origin, bound);
	}

	/**
	 * 判定一次随机事件是否成功.
	 * 
	 * <pre>
	 * 如果rate&gt;=1,则百分百返回true.<br>
	 * 如果rate&lt;=0,则百分百返回false.
	 * </pre>
	 * 
	 * @param rate 成功率
	 * @return 如果成功返回true,否则返回false.
	 */
	public static boolean isSuccess(float rate) {
		return ThreadLocalRandom.current().nextFloat() < rate;
	}

	/**
	 * 判定一次随机事件是否成功.
	 * 
	 * <pre>
	 * 如果rate&gt;=1,则百分百返回true.<br>
	 * 如果rate&lt;=0,则百分百返回false.
	 * </pre>
	 * 
	 * @param rate 成功率
	 * @return 如果成功返回true,否则返回false.
	 */
	public static boolean isSuccess(double rate) {
		return ThreadLocalRandom.current().nextDouble() < rate;
	}

	/**
	 * 在指定集合中随机出一个元素.
	 * <p>
	 * 所以元素无权重的随机.
	 * 
	 * @param <T> 要随机集合里的元素类型
	 * @param list 指定集合
	 * @return 随机返回集合中的一个元素.
	 */
	public static <T> T randomList(List<T> list) {
		// 没有东东的集合，随机个毛线啊...
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(nextInt(list.size()));
	}

	/**
	 * 从一个List集合中随机出指定数量的元素.
	 * <p>
	 * <code>
	 * source = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]<br>
	 * random(source, 5) = [5, 3, 6, 7, 2]
	 * </code>
	 * 
	 * @param <T> 要随机集合里的元素类型
	 * @param source List集合
	 * @param num 指定数量
	 * @return 如果源为空或指定数量小于1，则返回空集合，否则随机抽取元素组装新集合并返回
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> randomList(final List<T> source, int num) {
		// 没有源或要取的数小于1个就直接返回空列表
		if (source == null || num < 1) {
			return Collections.emptyList();
		}

		// 数量刚刚好
		if (source.size() <= num) {
			List<T> result = new ArrayList<>(source);
			Collections.shuffle(source);
			return result;
		}

		// 随机位，最后一个元素向前移动的方式
		Object[] rs = source.toArray();
		List<T> result = new ArrayList<>(num);
		for (int i = 0; i < num; i++) {
			int index = nextInt(rs.length - i);
			result.add((T) rs[index]);
			rs[index] = rs[rs.length - 1 - i];
		}
		return result;
	}

	/**
	 * 在指定集合中按权重随机出一个元素.
	 * <p>
	 * K为元素，如果是自定义对象记得重写HashCode和equals.<br>
	 * V为权重，机率为V/(sum(All))
	 * 
	 * @param <K> 要随机的元素类型，也是Map的Key
	 * @param data 随机集合
	 * @return 按权重随机返回集合中的一个元素.
	 */
	public static <K> K randomByWeight(Map<K, Integer> data) {
		final int sum = data.values().stream().reduce(0, (a, b) -> a + b);
		if (sum <= 0) {
			return randomList(new ArrayList<>(data.keySet()));
		}

		final int random = nextInt(sum);
		int step = 0;
		for (Map.Entry<K, Integer> e : data.entrySet()) {
			step += e.getValue().intValue();
			if (step > random) {
				return e.getKey();
			}
		}
		throw new RuntimeException("randomByWeight的实现有Bug：" + random);
	}

	/**
	 * 在指定集合中按权重随机出一个元素.
	 * <p>
	 * 权重，机率为V/(sum(All))
	 * 
	 * @param <T> 要随机的元素类型
	 * @param data 随机集合
	 * @param weightFunction 元素中权重方法
	 * @return 按权重随机返回集合中的一个元素
	 */
	public static <T> T randomByWeight(List<T> data, ToIntFunction<? super T> weightFunction) {
		final int sum = data.stream().mapToInt(weightFunction).reduce(0, (a, b) -> a + b);
		if (sum <= 0) {
			return randomList(data);
		}

		final int random = nextInt(sum);
		int step = 0;
		for (T e : data) {
			step += weightFunction.applyAsInt(e);
			if (step > random) {
				return e;
			}
		}
		throw new RuntimeException("randomByWeight的实现有Bug：" + random);
	}

	/**
	 * 在指定集合中按权重随机出指定数量个元素.
	 * <p>
	 * 权重，机率为V/(sum(All))
	 * 
	 * @param <T> 要随机的元素类型
	 * @param data 随机集合
	 * @param weightFunction 元素中权重方法
	 * @param num 指定数量
	 * @return 按权重随机返回集合中的指定数量个元素
	 */
	public static <T> List<T> randomByWeight(List<T> data, ToIntFunction<? super T> weightFunction, int num) {
		if (num <= 0) {
			return Collections.emptyList();
		}

		final int sum = data.stream().mapToInt(weightFunction).reduce(0, (a, b) -> a + b);
		if (sum <= 0) {
			return randomList(data, num);
		}

		List<T> result = new ArrayList<>(num);
		for (int i = 1; i <= num; i++) {
			final int random = nextInt(sum);
			int step = 0;
			for (T e : data) {
				step += weightFunction.applyAsInt(e);
				if (step > random) {
					result.add(e);
				}
			}
		}
		return result;
	}
}