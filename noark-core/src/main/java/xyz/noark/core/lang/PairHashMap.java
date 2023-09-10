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

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.util.MapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 两个元素当Key的HashMap.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class PairHashMap<L, R, V> implements PairMap<L, R, V> {
    private final HashMap<Pair<L, R>, V> hashmap;

    public PairHashMap() {
        this.hashmap = new HashMap<>();
    }

    public PairHashMap(int size) {
        this.hashmap = MapUtils.newHashMap(size);
    }

    public PairHashMap(List<V> templates, Function<? super V, ? extends L> leftMapper, Function<? super V, ? extends R> rightMapper) {
        this(templates.size());
        for (V template : templates) {
            V object = put(template, leftMapper, rightMapper);
            if (object != null) {
                String name = object.getClass().getName();
                L left = leftMapper.apply(object);
                R right = rightMapper.apply(object);
                throw new ServerBootstrapException("重复主键 class=" + name + ", left=" + left + ", right=" + right);
            }
        }
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
    public Collection<V> values() {
        return hashmap.values();
    }

    /**
     * 提供一个遍历方法
     *
     * @param action 遍历元素时的逻辑
     */
    public void forEach(BiConsumer<Pair<L, R>, V> action) {
        hashmap.forEach(action);
    }

    @Override
    public String toString() {
        return "PairHashMap [data=" + hashmap + "]";
    }
}