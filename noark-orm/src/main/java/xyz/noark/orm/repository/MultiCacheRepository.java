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
package xyz.noark.orm.repository;

import xyz.noark.core.annotation.PlayerId;
import xyz.noark.core.annotation.orm.Id;
import xyz.noark.orm.cache.DataCache;
import xyz.noark.orm.cache.MultiDataCacheImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 封装了一套缓存机制的ORM数据访问层.
 * <p>
 * 应用于一种情况：<br>
 * 1.有{@link PlayerId}注解并且和{@link Id}注解标识不同属性的实体类. <br>
 * 可以理解为，一个角色可以有多条记录数据的类.<br>
 *
 * @param <T> 实体类
 * @param <K> 实体类的主键
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class MultiCacheRepository<T, K extends Serializable> extends AbstractCacheRepository<T, K> {

    /**
     * 从缓存中删除指定Id的对象.
     *
     * @param playerId 玩家Id
     * @param entityId 缓存对象的Id
     */
    public void cacheDelete(Serializable playerId, K entityId) {
        T result = dataCache.load(playerId, entityId);
        dataCache.delete(result);

        asyncWriteService.delete(entityMapping, result);
    }

    /**
     * 删除角色模块缓存里的所有数据.
     *
     * @param playerId 角色Id
     */
    public void cacheDeleteAll(Serializable playerId) {
        List<T> result = dataCache.deleteAll(playerId);

        asyncWriteService.deleteAll(entityMapping, result);
    }

    /**
     * 从角色缓存中根据实体Id获取对象.
     *
     * @param playerId 角色Id
     * @param entityId 实体Id
     * @return 实体对象.
     */
    public Optional<T> cacheLoad(Serializable playerId, K entityId) {
        return Optional.ofNullable(dataCache.load(playerId, entityId));
    }

    /**
     * 从角色缓存中根据实体Id获取对象.
     *
     * @param playerId 角色Id
     * @param entityId 实体Id
     * @return 实体对象.
     */
    public T cacheGet(Serializable playerId, K entityId) {
        return dataCache.load(playerId, entityId);
    }

    /**
     * 从角色缓存中根据过滤器获取对象.
     *
     * @param playerId 角色Id
     * @param filter   条件过滤器
     * @return 实体对象.
     */
    public T cacheGet(Serializable playerId, Predicate<T> filter) {
        return this.cacheLoad(playerId, filter).orElse(null);
    }

    /**
     * 从角色缓存中根据过滤器获取对象.
     *
     * @param playerId 角色Id
     * @param filter   条件过滤器
     * @return 实体对象.
     */
    public Optional<T> cacheLoad(Serializable playerId, Predicate<T> filter) {
        return Optional.ofNullable(dataCache.load(playerId, filter));
    }

    /**
     * 统计角色缓存中符合过滤条件的对象总数。
     *
     * @param playerId 角色Id
     * @param filter   条件过滤器
     * @return 符合过滤条件的对象总数。
     */
    public long cacheCount(Serializable playerId, Predicate<T> filter) {
        return dataCache.count(playerId, filter);
    }

    /**
     * 从角色缓存中获取一个模块所有缓存数据.
     *
     * @param playerId 角色Id
     * @return 一个模块所有缓存数据.
     */
    public List<T> cacheLoadAll(Serializable playerId) {
        return dataCache.loadAll(playerId);
    }

    /**
     * 从缓存中获取符合过虑器的需求的对象.
     *
     * @param playerId 角色Id
     * @param filter   过虑器
     * @return 符合过虑器的需求的对象列表.
     */
    public List<T> cacheLoadAll(Serializable playerId, Predicate<T> filter) {
        return dataCache.loadAll(playerId, filter);
    }

    @Override
    protected DataCache<T, K> buildDataCache(int offlineInterval) {
        return new MultiDataCacheImpl<>(this, offlineInterval);
    }
}