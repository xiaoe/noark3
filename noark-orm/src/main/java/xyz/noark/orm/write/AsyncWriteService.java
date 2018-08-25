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
package xyz.noark.orm.write;

import java.io.Serializable;
import java.util.List;

import xyz.noark.orm.EntityMapping;

/**
 * 异步回写服务接口.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public interface AsyncWriteService {

	/**
	 * 插入一个实体对象.
	 * 
	 * @param <T> 实体对象类型
	 * @param em 实体映射对象
	 * @param entity 实体对象
	 */
	public <T> void insert(final EntityMapping<T> em, final T entity);

	/**
	 * 删除一个实体对象.
	 * 
	 * @param <T> 实体对象类型
	 * @param em 实体映射对象
	 * @param entity 实体对象
	 */
	public <T> void delete(final EntityMapping<T> em, final T entity);

	/**
	 * 删除一批实体对象.
	 * 
	 * @param <T> 实体对象类型
	 * @param em 实体映射对象
	 * @param result 实体对象列表
	 */
	public <T> void deleteAll(final EntityMapping<T> em, List<T> result);

	/**
	 * 更新一个实体对象.
	 * 
	 * @param <T> 实体对象类型
	 * @param em 实体映射对象
	 * @param entity 实体对象
	 */
	public <T> void update(final EntityMapping<T> em, final T entity);

	/**
	 * 同步式的清理全部回写数据.
	 * <p>
	 * 一般用于停机维护.
	 */
	public void syncFlushAll();

	/**
	 * 停机存档
	 */
	public void shutdown();

	/**
	 * 异步存储，由存储系统保存.
	 * 
	 * @param playerId 玩家ID
	 */
	public void asyncFlushByPlayerId(Serializable playerId);

	/**
	 * 初始化存储系统
	 * 
	 * @param saveInterval 定时保存间隔
	 * @param offlineInterval 定时清理缓存
	 */
	public void init(int saveInterval, int offlineInterval);
}