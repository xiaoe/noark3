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

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 两个元素当键的Map.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public interface PairMap<L, R, V> {

	/**
	 * 插入键与值.
	 * 
	 * @param left 键之左边元素
	 * @param right 键之右边元素
	 * @param value 值
	 * @return 返回插入前老的值，可能会为空.
	 */
	public V put(final L left, final R right, V value);

	/**
	 * 移除指定键的值并返回.
	 * 
	 * @param left 键之左边元素
	 * @param right 键之右边元素
	 * @return 返回与之关联的值，如果没有返回null.
	 */
	V remove(final L left, final R right);

	/**
	 * 返回Map中键值数量.
	 * 
	 * @return Map中键值数量
	 */
	public int size();

	/**
	 * 根据两个元素的键来取出来对应的值.
	 * 
	 * @param left 键之左边元素
	 * @param right 键之右边元素
	 * @return 对应的值，可能会为空.
	 */
	public V get(final L left, final R right);

	/**
	 * 根据两个元素的键来取出来对应的值，如果不存在则返回默认值.
	 * 
	 * @param left 键之左边元素
	 * @param right 键之右边元素
	 * @param defaultValue 默认值
	 * @return 对应的值，如果不存在则返回默认值
	 */
	public V getOrDefault(final L left, final R right, V defaultValue);

	/**
	 * 根据两个元素的键来取出来对应的值，如果不存在则调用创建方法.
	 * 
	 * @param left 键之左边元素
	 * @param right 键之右边元素
	 * @param createSupplier 创建方法
	 * @return 对应的值
	 */
	default V computeIfAbsent(final L left, final R right, Supplier<? extends V> createSupplier) {
		Objects.requireNonNull(createSupplier);
		V v;
		if ((v = get(left, right)) == null) {
			V newValue;
			if ((newValue = createSupplier.get()) != null) {
				put(left, right, newValue);
				return newValue;
			}
		}
		return v;
	}
}