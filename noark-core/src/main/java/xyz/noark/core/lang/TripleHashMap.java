/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 *        http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.lang;

import xyz.noark.core.util.MapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * 三个元素当Key的HashMap.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class TripleHashMap<L, M, R, V> implements TripleMap<L, M, R, V> {
    private final HashMap<Triple<L, M, R>, V> hashmap;

    /**
     * 构建一个TripleHashMap
     */
    public TripleHashMap() {
        this.hashmap = new HashMap<>();
    }

    /**
     * 根据指定存储元素数量来构建一个TripleHashMap
     *
     * @param size 存储元素数量
     */
    public TripleHashMap(int size) {
        this.hashmap = MapUtils.newHashMap(size);
    }

    public TripleHashMap(List<V> templates, Function<? super V, ? extends L> leftMapper, Function<? super V, ? extends M> middleMapper, Function<? super V, ? extends R> rightMapper) {
        this(templates.size());
        templates.forEach(v -> put(v, leftMapper, middleMapper, rightMapper));
    }

    private V put(V value, Function<? super V, ? extends L> leftMapper, Function<? super V, ? extends M> middleMapper, Function<? super V, ? extends R> rightMapper) {
        return put(leftMapper.apply(value), middleMapper.apply(value), rightMapper.apply(value), value);
    }

    @Override
    public int size() {
        return hashmap.size();
    }

    @Override
    public V put(L left, M middle, R right, V value) {
        return hashmap.put(Triple.of(left, middle, right), value);
    }

    @Override
    public V remove(L left, M middle, R right) {
        return hashmap.remove(Triple.of(left, middle, right));
    }

    @Override
    public V get(L left, M middle, R right) {
        return hashmap.get(Triple.of(left, middle, right));
    }

    @Override
    public V getOrDefault(L left, M middle, R right, V defaultValue) {
        return hashmap.getOrDefault(Triple.of(left, middle, right), defaultValue);
    }

    @Override
    public Collection<V> values() {
        return hashmap.values();
    }

    @Override
    public String toString() {
        return "TripleHashMap [hashmap=" + hashmap + "]";
    }
}