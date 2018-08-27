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
package xyz.noark.orm.write.impl;

import xyz.noark.core.exception.DataException;
import xyz.noark.orm.EntityMapping;

/**
 * 实体操作包装类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class EntityOperate<T> {
	private String id;

	private EntityMapping<T> em;
	private T entity;

	private boolean insert = false;
	private boolean update = false;
	private boolean delete = false;

	public EntityOperate(String entityId, EntityMapping<T> em) {
		this.em = em;
		this.id = entityId;
	}

	private void updateEntity(T entity) {
		this.entity = entity;
	}

	public void insert(T insertEntity) {
		if (delete) {
			throw new DataException("illeagle [insert] after [delete]," + insertEntity.getClass().getName());
		}

		this.insert = true;
		this.updateEntity(insertEntity);
	}

	public void update(T entity) {
		this.update = true;
		this.updateEntity(entity);
	}

	public boolean delete(T deleteEntity) {
		// 如果是刚插入的状态，直接返回true，由调用层删除
		if (insert) {
			return true;
		}
		this.delete = true;
		this.updateEntity(deleteEntity);
		return false;
	}

	public String getId() {
		return id;
	}

	public EntityMapping<T> getEntityMapping() {
		return em;
	}

	public boolean isDelete() {
		return delete;
	}

	public boolean isInsert() {
		if (delete) {
			return false;
		}
		return insert;
	}

	public boolean isUpdate() {
		if (delete || insert) {
			return false;
		}
		return update;
	}

	public T getEntity() {
		// FIXME 这里再好弄成深拷贝
		return entity;
	}
}