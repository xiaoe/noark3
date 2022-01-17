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
package xyz.noark.orm.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import xyz.noark.core.annotation.orm.Entity.FetchType;
import xyz.noark.core.exception.DataException;
import xyz.noark.orm.repository.CacheRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static xyz.noark.log.LogHelper.logger;

/**
 * 这类的，要么没有角色Id，要么就是角色Id就是主键。
 *
 * @param <T> 实体类
 * @param <K> 实体类Id
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class UniqueDataCacheImpl<T, K extends Serializable> extends AbstractDataCache<T, K> {
    /**
     * 实体Id <==> 一个数据包装器
     */
    private final LoadingCache<K, DataWrapper<T>> caches;

    public UniqueDataCacheImpl(CacheRepository<T, K> repository, long offlineInterval) {
        super(repository);

        // 构建一个数据加载器
        CacheLoader<K, DataWrapper<T>> loader = entityId -> {
            // 如果是启服就载入的，就没有必要再去访问DB了...
            if (entityMapping.getFetchType() == FetchType.START) {
                return new DataWrapper<>(null);
            }

            // 没有缓存时，从数据访问策略中加载
            return new DataWrapper<>(repository.load(entityId));
        };

        // 启服时加载内存是需要永久缓存
        if (entityMapping.getFetchType() == FetchType.START) {
            caches = Caffeine.newBuilder().build(loader);
        }
        // 其他情况是有缓存超时的
        else {
            caches = Caffeine.newBuilder().expireAfterAccess(offlineInterval, TimeUnit.SECONDS).build(loader);
        }
    }

    @Override
    public void insert(T entity) {
        final K entityId = this.getPrimaryIdValue(entity);

        DataWrapper<T> wrapper = caches.get(entityId);
        if (wrapper.getEntity() == null) {
            wrapper.setEntity(entity);
        } else {
            throw new DataException("插入了重复Key:" + entityId);
        }
    }

    @Override
    public void delete(T entity) {
        this.delete(this.getPrimaryIdValue(entity));
    }

    @Override
    public List<T> deleteAll() {
        List<T> result = loadAll();
        caches.invalidateAll();
        return result;
    }

    @Override
    public void update(T entity) {
        final K entityId = this.getPrimaryIdValue(entity);

        DataWrapper<T> wrapper = caches.get(entityId);
        if (wrapper.getEntity() == null) {
            throw new DataException("修改了一个不存在的Key:" + entityId);
        } else {
            wrapper.setEntity(entity);
        }
    }

    @Override
    public T load(K entityId) {

        // 启服载入的实体加载，那不能主动创建包装，没人删除...
        if (entityMapping.getFetchType() == FetchType.START) {
            DataWrapper<T> wrapper = caches.getIfPresent(entityId);
            return wrapper == null ? null : wrapper.getEntity();
        }

        return caches.get(entityId).getEntity();
    }

    @Override
    public List<T> loadAll() {
        return loadAllByQueryFilter(null);
    }

    @Override
    public List<T> loadAll(Predicate<T> filter) {
        return loadAllByQueryFilter(filter);
    }

    private List<T> loadAllByQueryFilter(Predicate<T> filter) {
        this.assertEntityFetchTypeIsStart();

        ConcurrentMap<K, DataWrapper<T>> map = caches.asMap();
        if (map.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<T> result = new ArrayList<>(map.size());
        for (Entry<K, DataWrapper<T>> e : map.entrySet()) {
            T entity = e.getValue().getEntity();
            if (entity == null) {
                continue;
            }
            if (filter != null) {
                if (!filter.test(entity)) {
                    continue;
                }
            }
            result.add(entity);
        }
        return result;
    }

    @Override
    public T delete(K entityId) {
        DataWrapper<T> wrapper = caches.get(entityId);
        if (wrapper.getEntity() == null) {
            throw new DataException("删除了一个不存在的Key:" + entityId);
        } else {
            T result = wrapper.getEntity();
            wrapper.setEntity(null);

            // 启服载入的实体删除，包装类也要删除，非启服载入还是走原来的超时
            if (entityMapping.getFetchType() == FetchType.START) {
                caches.invalidate(entityId);
            }
            return result;
        }
    }

    @Override
    public long count() {
        this.assertEntityFetchTypeIsStart();

        long result = 0;
        // 包装类中有数据才能计数
        ConcurrentMap<K, DataWrapper<T>> map = caches.asMap();
        for (Entry<K, DataWrapper<T>> e : map.entrySet()) {
            T entity = e.getValue().getEntity();
            if (entity == null) {
                continue;
            }
            result++;
        }
        return result;
    }

    @Override
    public void initCacheData() {
        logger.debug("实体类[{}]抓取策略为启动服务器就加载缓存.", entityMapping.getEntityClass());
        List<T> result = repository.loadAll();
        result.forEach(entity -> caches.put(this.getPrimaryIdValue(entity), new DataWrapper<>(entity)));
        logger.debug("实体类[{}]初始化缓存完成,一共 {} 条数据.", entityMapping.getEntityClass(), result.size());
    }

    /**
     * 数据包装器.
     * <p>
     * 这个类里有一个实体对象，可能为空，主要用来初始化缓存数据
     *
     * @param <E> 实体对象
     */
    private static class DataWrapper<E> {
        private E entity;

        private DataWrapper(E entity) {
            this.entity = entity;
        }

        E getEntity() {
            return entity;
        }

        void setEntity(E entity) {
            this.entity = entity;
        }
    }
}