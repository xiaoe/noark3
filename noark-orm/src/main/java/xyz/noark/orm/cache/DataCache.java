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

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

/**
 * 数据缓存接口.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public interface DataCache<T, K extends Serializable> {

	/**
	 * 新增一个实体对象.
	 * 
	 * @param entity 实体对象
	 */
	void insert(T entity);

	/**
	 * 删除一个实体对象
	 * 
	 * @param entity 实体对象
	 */
	void delete(T entity);

	/**
	 * 根据实体ID来删除实体对象.
	 * 
	 * @param entityId 实体ID
	 * @return 被删除的实体对象
	 */
	T delete(K entityId);

	/**
	 * 删除所有缓存实体.
	 * 
	 * @return 被删除的实体对象集合
	 */
	List<T> deleteAll();

	/**
	 * 根据玩家ID来删除所有缓存实体.
	 * 
	 * @param playerId 玩家ID
	 * @return 被删除的实体对象集合
	 */
	List<T> deleteAll(Serializable playerId);

	/**
	 * 更新一个实体对象
	 * 
	 * @param entity 实体对象
	 */
	void update(T entity);

	/**
	 * 根据主键加载实体对象
	 * 
	 * @param entityId 主键
	 * @return 实体对象
	 */
	T load(K entityId);

	/**
	 * 根据玩家ID与主键值加载实体对象
	 * 
	 * @param playerId 玩家ID
	 * @param entityId 主键
	 * @return 实体对象
	 */
	T load(Serializable playerId, K entityId);

	/**
	 * 获取所有实体对象
	 * 
	 * @return 实体对象列表
	 */
	List<T> loadAll();

	/**
	 * 根据玩家ID实体获取所有实体对象
	 * 
	 * @param playerId 玩家ID
	 * @return 实体对象
	 */
	List<T> loadAll(Serializable playerId);

	/**
	 * 初始化缓存
	 */
	void initCacheData();

	/**
	 * 根据条件载入实体对象列表
	 * 
	 * @param filter 条件
	 * @return 实体对象列表
	 */
	List<T> loadAll(Predicate<T> filter);

	/**
	 * 根据玩家ID与条件载入实体对象.
	 * 
	 * @param playerId 玩家ID
	 * @param filter 条件
	 * @return 实体对象
	 */
	T load(Serializable playerId, Predicate<T> filter);

	/**
	 * 统计有多少实体对象
	 * 
	 * @return 实体对象数量
	 */
	long count();

	/**
	 * 统计以玩家ID所要条件有多少实体对象
	 * 
	 * @param playerId 玩家ID
	 * @param filter 条件
	 * @return 实体对象数量
	 */
	long count(Serializable playerId, Predicate<T> filter);

	/**
	 * 根据玩家ID与条件载入实体对象列表.
	 * 
	 * @param playerId 玩家ID
	 * @param filter 条件
	 * @return 实体对象列表
	 */
	List<T> loadAll(Serializable playerId, Predicate<T> filter);

}