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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.orm.DataCheckAndInit;
import xyz.noark.orm.AnnotationEntityMaker;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.accessor.DataAccessor;

/**
 * OrmRepository类为所有实体操作类的父类.
 * <p>
 * 向子类提供ORM操作接口.<br>
 * 
 * @param <T> 实体类型
 * @param <K> 实体的ID类型
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public class OrmRepository<T, K extends Serializable> {
	private static final AnnotationEntityMaker MAKER = new AnnotationEntityMaker();
	protected final EntityMapping<T> entityMapping;
	@Autowired
	protected DataAccessor dataAccessor;

	@SuppressWarnings("unchecked")
	protected OrmRepository() {
		Type mySuperClass = this.getClass().getGenericSuperclass();
		Type type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
		this.entityMapping = MAKER.make((Class<T>) type);
	}

	/**
	 * 检查实体类与DB映射关系并初始化缓存
	 */
	@DataCheckAndInit
	public void checkEntityAndInitCache() {
		dataAccessor.checkupEntityFieldsWithDatabase(entityMapping);
	}

	/**
	 * 向存储策略接口插入一个实体对象.
	 * 
	 * @param entity 实体类对象.
	 */
	public void insert(T entity) {
		dataAccessor.insert(entityMapping, entity);
	}

	/**
	 * 向存储策略接口插入一批实体对象.
	 * 
	 * @param entitys 一批实体对象
	 */
	public void batchInsert(List<T> entitys) {
		dataAccessor.batchInsert(entityMapping, entitys);
	}

	/**
	 * 向存储策略接口删除一个实体对象.
	 * 
	 * @param entity 实体类对象.
	 */
	public void delete(T entity) {
		dataAccessor.delete(entityMapping, entity);
	}

	/**
	 * 向存储策略接口删除一批实体对象.
	 * 
	 * @param entitys 一批实体对象
	 */
	public void batchDelete(List<T> entitys) {
		dataAccessor.batchDelete(entityMapping, entitys);
	}

	/**
	 * 向存储策略接口修改一个实体对象.
	 * 
	 * @param entity 实体类对象.
	 */
	public void update(T entity) {
		dataAccessor.update(entityMapping, entity);
	}

	/**
	 * 向存储策略接口修改一批实体对象.
	 * 
	 * @param entitys 一批实体对象
	 */
	public void batchUpdate(List<T> entitys) {
		dataAccessor.batchUpdate(entityMapping, entitys);
	}

	/**
	 * 根据角色ID和实体Id从存储策略层加载数据.
	 * 
	 * @param entityId 实体ID.
	 * @return 如果存在此ID的对象，则返回此对象，否则返回 null
	 */
	public T load(K entityId) {
		return dataAccessor.load(entityMapping, entityId);
	}

	/**
	 * 从存储策略层加载数据.
	 * <p>
	 * 业内替规则：返回集合时，不要返回null 如果为空也要返回空列表
	 * 
	 * @return 如果存在此类对象，则返回对象列表，否则返回 空列表.
	 */
	public List<T> loadAll() {
		return dataAccessor.loadAll(entityMapping);
	}

	/**
	 * 根据playerId从存储策略层加载数据.
	 * <p>
	 * 如果是系统的就直接调用LoadAll
	 * 
	 * @param playerId 角色ID.
	 * @return 如果存在此角色ID的对象，则返回对象列表，否则返回 空列表.
	 */
	public List<T> loadAll(Serializable playerId) {
		return dataAccessor.loadAll(entityMapping, playerId);
	}
}