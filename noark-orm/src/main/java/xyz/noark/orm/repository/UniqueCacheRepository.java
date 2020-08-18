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
import xyz.noark.orm.cache.UniqueDataCacheImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 封装了一套缓存机制的ORM数据访问层.
 * <p>
 * 应用于两种情况：<br>
 * 1.没有{@link PlayerId}注解的实体类.<br>
 * 2.有{@link PlayerId}注解并且和{@link Id}注解同一个属性的实体类. <br>
 * 可以理解为，一个角色只有一条记录或不属于任何角色的数据的类.<br>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class UniqueCacheRepository<T, K extends Serializable> extends AbstractCacheRepository<T, K> {

    /**
     * 从缓存中删除指定Id的对象.
     * <p>
     *
     * @param entityId 缓存对象的Id
     */
    public void cacheDelete(K entityId) {
        T result = dataCache.delete(entityId);

        asyncWriteService.delete(entityMapping, result);
    }

    /**
     * 从缓存中Load指定ID的对象
     *
     * @param entityId 缓存对象的Id
     * @return 对象
     */
    public Optional<T> cacheLoad(K entityId) {
        return Optional.ofNullable(dataCache.load(entityId));
    }

    /**
     * 从缓存中Get指定ID的对象
     *
     * @param entityId 缓存对象的Id
     * @return 对象
     */
    public T cacheGet(K entityId) {
        return dataCache.load(entityId);
    }

    /**
     * 根据条件从缓存中获取所有缓存数据.
     *
     * @param filter 条件
     * @return 所有缓存数据
     */
    public List<T> cacheLoadAll(Predicate<T> filter) {
        return dataCache.loadAll(filter);
    }

    /**
     * 直接统计缓存中数据的数量
     *
     * @return 缓存中数据的数量
     */
    public long cacheCount() {
        return dataCache.count();
    }

    @Override
    protected DataCache<T, K> buildDataCache(int offlineInterval) {
        return new UniqueDataCacheImpl<>(this, offlineInterval);
    }
}