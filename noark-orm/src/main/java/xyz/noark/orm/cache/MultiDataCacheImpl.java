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
package xyz.noark.orm.cache;

import static xyz.noark.log.LogHelper.logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import xyz.noark.core.annotation.orm.Entity.FetchType;
import xyz.noark.core.exception.DataException;
import xyz.noark.orm.repository.CacheRepository;

/**
 * 这类的，就是一个玩家有多条数据.
 * 
 * @param <T> 实体类
 * @param <K> 实体类Id
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public class MultiDataCacheImpl<T, K extends Serializable> extends AbstractDataCache<T, K> {
	/** 角色Id <==> 一个数据集合 */
	private final LoadingCache<Serializable, ConcurrentMap<K, T>> caches;

	public MultiDataCacheImpl(CacheRepository<T, K> repository, long offlineInterval) {
		super(repository);

		CacheLoader<Serializable, ConcurrentMap<K, T>> loader = new CacheLoader<Serializable, ConcurrentMap<K, T>>() {
			@Override
			public ConcurrentHashMap<K, T> load(Serializable playerId) throws Exception {
				// 如果是启服就载入的，就没有必要再去访问DB了...
				if (entityMapping.getFetchType() == FetchType.START) {
					return new ConcurrentHashMap<>(16);
				}

				List<T> result = repository.loadAll(playerId);
				int initSize = result.size() > 32 ? result.size() : 32;
				ConcurrentHashMap<K, T> datas = new ConcurrentHashMap<>(initSize);
				for (T entity : result) {
					datas.put(getPrimaryIdValue(entity), entity);
				}
				return datas;
			}
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
		final Serializable playerId = entityMapping.getPlayerIdValue(entity);
		final ConcurrentMap<K, T> data = caches.get(playerId);
		final K entityId = this.getPrimaryIdValue(entity);
		if (data.containsKey(entityId)) {
			throw new DataException("插入了重复Key:" + entityId);
		}
		data.put(entityId, entity);
	}

	@Override
	public void delete(T entity) {
		Serializable playerId = entityMapping.getPlayerIdValue(entity);
		final ConcurrentMap<K, T> data = caches.get(playerId);

		K entityId = this.getPrimaryIdValue(entity);
		T result = data.remove(entityId);
		if (result == null) {
			throw new DataException("删除了一个不存在的Key:" + entityId);
		}
	}

	@Override
	public List<T> deleteAll() {
		// 疯了，有角色区别删全部，希望你是故意的
		List<T> result = loadAll();
		caches.invalidateAll();
		return result;
	}

	@Override
	public List<T> deleteAll(Serializable playerId) {
		final ConcurrentMap<K, T> data = caches.get(playerId);
		List<T> result = new ArrayList<>(data.values());
		data.clear();
		return result;
	}

	@Override
	public void update(T entity) {
		Serializable playerId = entityMapping.getPlayerIdValue(entity);
		final ConcurrentMap<K, T> data = caches.get(playerId);
		K entityId = this.getPrimaryIdValue(entity);
		if (!data.containsKey(entityId)) {
			throw new DataException("修改了一个不存在的Key:" + entityId);
		}
		data.put(entityId, entity);
	}

	@Override
	public T load(Serializable playerId, K entityId) {
		return caches.get(playerId).get(entityId);
	}

	@Override
	public T load(Serializable playerId, Predicate<T> filter) {
		return caches.get(playerId).values().stream().filter(filter).findFirst().orElse(null);
	}

	@Override
	public long count(Serializable playerId, Predicate<T> filter) {
		return caches.get(playerId).values().stream().filter(filter).count();
	}

	@Override
	public List<T> loadAll(Serializable playerId) {
		return new ArrayList<>(caches.get(playerId).values());
	}

	@Override
	public List<T> loadAll(Serializable playerId, Predicate<T> filter) {
		return caches.get(playerId).values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public List<T> loadAll() {
		this.assertEntityFetchTypeIsStart();

		ConcurrentMap<Serializable, ConcurrentMap<K, T>> map = caches.asMap();
		if (map.isEmpty()) {
			return Collections.emptyList();
		}

		ArrayList<T> result = new ArrayList<>(map.size());
		for (Entry<Serializable, ConcurrentMap<K, T>> e : map.entrySet()) {
			result.addAll(e.getValue().values());
		}
		return result;
	}

	@Override
	public List<T> loadAll(Predicate<T> filter) {
		this.assertEntityFetchTypeIsStart();

		ConcurrentMap<Serializable, ConcurrentMap<K, T>> map = caches.asMap();
		if (map.isEmpty()) {
			return Collections.emptyList();
		}

		ArrayList<T> result = new ArrayList<>(map.size());
		for (Entry<Serializable, ConcurrentMap<K, T>> e : map.entrySet()) {
			result.addAll(e.getValue().values().stream().filter(filter).collect(Collectors.toList()));
		}
		return result;
	}

	@Override
	public void initCacheData() {
		logger.debug("实体类[{}]抓取策略为启动服务器就加载缓存.", entityMapping.getEntityClass());
		List<T> result = repository.loadAll();
		if (!result.isEmpty()) {
			Map<Serializable, ConcurrentHashMap<K, T>> data = new HashMap<>(result.size());
			for (T entity : result) {
				Serializable playerId = entityMapping.getPlayerIdValue(entity);
				ConcurrentHashMap<K, T> ds = data.get(playerId);
				if (ds == null) {
					ds = new ConcurrentHashMap<>(result.size() * 2);
					data.put(playerId, ds);
				}
				ds.put(this.getPrimaryIdValue(entity), entity);
			}
			caches.putAll(data);
		}
		logger.debug("实体类[{}]初始化缓存完成,一共 {} 条数据.", entityMapping.getEntityClass(), result.size());
	}
}