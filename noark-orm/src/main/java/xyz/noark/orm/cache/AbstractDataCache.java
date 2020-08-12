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

import xyz.noark.core.annotation.orm.Entity.FetchType;
import xyz.noark.core.exception.HackerException;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.repository.CacheRepository;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

/**
 * 数据缓存抽象的实现类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
abstract class AbstractDataCache<T, K extends Serializable> implements DataCache<T, K> {
    protected final EntityMapping<T> entityMapping;
    protected final CacheRepository<T, K> repository;

    public AbstractDataCache(CacheRepository<T, K> repository) {
        this.repository = repository;
        this.entityMapping = repository.getEntityMapping();
    }

    @SuppressWarnings("unchecked")
    protected K getPrimaryIdValue(T entity) {
        return (K) entityMapping.getPrimaryIdValue(entity);
    }

    @Override
    public T load(K entityId) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public T load(Serializable roleId, K entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> loadAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> loadAll(Serializable roleId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(T entity) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(T entity) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> deleteAll(Serializable playerId) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(T entity) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public void initCacheData() {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> loadAll(Predicate<T> filter) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public T load(Serializable playerId, Predicate<T> filter) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("非法操作 ...");
    }

    @Override
    public long count(Serializable playerId, Predicate<T> filter) {
        throw new UnsupportedOperationException("非法操作 ...");
    }

    @Override
    public List<T> loadAll(Serializable playerId, Predicate<T> filter) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    @Override
    public T delete(K entityId) {
        // 当不支持请求的操作时，抛出该异常。
        throw new UnsupportedOperationException();
    }

    /**
     * 断言实体的抓取策略为启服载入
     */
    protected void assertEntityFetchTypeIsStart() {
        if (entityMapping.getFetchType() != FetchType.START) {
            throw new HackerException("调用LoadAll接口时，当前实体类抓取策略为启服载入");
        }
    }
}