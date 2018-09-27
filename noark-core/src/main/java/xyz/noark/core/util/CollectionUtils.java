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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import xyz.noark.core.lang.PairHashMap;
import xyz.noark.core.lang.PairMap;

/**
 * Collection工具类.
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
public class CollectionUtils {
	/**
	 * 检测集合是否为{@code null}或长度为0
	 *
	 * @param collection 被检测集合
	 * @return 如果字符为{@code null}或长度为0则返回true,否则返回false.
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * 检测集合是否不为{@code null}或长度大于0
	 *
	 * @param collection 被检测集合
	 * @return 如果字符不为{@code null}或长度大于0则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final Collection<?> collection) {
		return !isEmpty(collection);
	}

	/**
	 * 根据两个属性为条件对指定集合进行分组.
	 * <p>
	 * 常用于模板分组缓存备用
	 * 
	 * @param collection 指定集合
	 * @param leftMapper 属性一
	 * @param rightMapper 属性二
	 * @return 返回分组后的PairMap集合
	 */
	public static <L, R, V> PairMap<L, R, List<V>> groupingBy(final Collection<V> collection, Function<? super V, ? extends L> leftMapper, Function<? super V, ? extends R> rightMapper) {
		PairMap<L, R, List<V>> result = new PairHashMap<>();
		collection.forEach(t -> result.computeIfAbsent(leftMapper.apply(t), rightMapper.apply(t), () -> new ArrayList<>()).add(t));
		return result;
	}
}