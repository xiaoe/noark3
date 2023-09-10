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

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 超时功能的HashMap.
 * <p>
 * 此版本由Caffeine缓存来实现.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class TimeoutHashMap<K, V> {
    private final LoadingCache<K, V> caches;

    public TimeoutHashMap(long duration, TimeUnit unit, Supplier<? extends V> loading) {
        this.caches = Caffeine.newBuilder().expireAfterAccess(duration, unit).build(key -> loading.get());
    }

    public TimeoutHashMap(long duration, TimeUnit unit, int maximumSize, Supplier<? extends V> loading) {
        this.caches = Caffeine.newBuilder().expireAfterAccess(duration, unit).maximumSize(maximumSize).build(key -> loading.get());
    }

    public TimeoutHashMap(long duration, TimeUnit unit, CacheLoader<? super K, V> loader) {
        this.caches = Caffeine.newBuilder().expireAfterAccess(duration, unit).build(loader);
    }

    public TimeoutHashMap(long duration, TimeUnit unit, int maximumSize, CacheLoader<? super K, V> loader) {
        this.caches = Caffeine.newBuilder().expireAfterAccess(duration, unit).maximumSize(maximumSize).build(loader);
    }

    public V get(K key) {
        return caches.get(key);
    }
}
