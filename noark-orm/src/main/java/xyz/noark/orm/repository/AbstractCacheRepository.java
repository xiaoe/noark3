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
package xyz.noark.orm.repository;

import java.io.Serializable;
import java.util.List;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.annotation.orm.Entity.FeatchType;
import xyz.noark.orm.DataModular;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.cache.DataCache;
import xyz.noark.orm.write.AsyncWriteService;

/**
 * 一种带有缓存类型的数据存储.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
abstract class AbstractCacheRepository<T, K extends Serializable> extends OrmRepository<T, K> implements CacheRepository<T, K> {
	// CacheRepository层的数据缓存
	protected DataCache<T, K> dataCache;
	@Value(DataModular.DATA_OFFLINE_INERVAL)
	private int offlineInterval = 3600;
	@Autowired
	protected AsyncWriteService asyncWriteService;

	/**
	 * 获取当前OrmRepository的实体类的描述对象
	 * 
	 * @return 实体类的描述对象
	 */
	@Override
	public EntityMapping<T> getEntityMapping() {
		return entityMapping;
	}

	@Override
	public void _checkEntityAndInitCache() {
		super._checkEntityAndInitCache();

		// 缓存抓取策略
		this.dataCache = buildDataCache(offlineInterval);

		if (entityMapping.getFeatchType() == FeatchType.START) {
			dataCache.initCacheData();
		}
	}

	/**
	 * 创建数据缓存.
	 */
	protected abstract DataCache<T, K> buildDataCache(int offlineInterval);

	/**
	 * 保存一个新增对象到缓存.
	 * 
	 * @param entity 新增对象.
	 * @exception DataException 当缓存已存在此对象时会抛出此异常。
	 */
	public void cacheInsert(T entity) {
		dataCache.insert(entity);

		asyncWriteService.insert(entityMapping, entity);
	}

	/**
	 * 删除缓存一个对象.
	 * 
	 * @param entity 实体对象.
	 */
	public void cacheDelete(T entity) {
		dataCache.delete(entity);

		asyncWriteService.delete(entityMapping, entity);
	}

	/**
	 * 删除当前模块全部缓存对象.
	 * <p>
	 * <b>这是删除全部，调用时，别犯2</b>
	 */
	public void cacheDeleteAll() {
		List<T> result = dataCache.deleteAll();

		asyncWriteService.deleteAll(entityMapping, result);
	}

	/**
	 * 修改缓存中的数据.
	 * 
	 * @param entity 实体对象.
	 */
	public void cacheUpdate(T entity) {
		dataCache.update(entity);

		asyncWriteService.update(entityMapping, entity);
	}
}