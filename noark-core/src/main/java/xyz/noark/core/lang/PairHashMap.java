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

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * 两个元素当Key的HashMap.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class PairHashMap<L, R, V> implements PairMap<L, R, V> {
	private final HashMap<Pair<L, R>, V> hashmap;

	public PairHashMap() {
		this.hashmap = new HashMap<>();
	}

	public PairHashMap(int initialCapacity) {
		this.hashmap = new HashMap<>(initialCapacity);
	}

	public PairHashMap(int initialCapacity, float loadFactor) {
		this.hashmap = new HashMap<>(initialCapacity, loadFactor);
	}

	public PairHashMap(List<V> templates, Function<? super V, ? extends L> leftMapper, Function<? super V, ? extends R> rightMapper) {
		this(templates.size() + 1, 1);
		templates.forEach(v -> put(v, leftMapper, rightMapper));
	}

	private V put(V value, Function<? super V, ? extends L> leftMapper, Function<? super V, ? extends R> rightMapper) {
		return put(leftMapper.apply(value), rightMapper.apply(value), value);
	}

	@Override
	public V put(L left, R right, V value) {
		return hashmap.put(Pair.of(left, right), value);
	}

	@Override
	public V remove(L left, R right) {
		return hashmap.remove(Pair.of(left, right));
	}

	@Override
	public int size() {
		return hashmap.size();
	}

	@Override
	public V get(L left, R right) {
		return hashmap.get(Pair.of(left, right));
	}

	@Override
	public V getOrDefault(L left, R right, V defaultValue) {
		return hashmap.getOrDefault(Pair.of(left, right), defaultValue);
	}

	@Override
	public String toString() {
		return "PairHashMap [data=" + hashmap + "]";
	}
}