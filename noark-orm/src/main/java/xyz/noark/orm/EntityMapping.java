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
package xyz.noark.orm;

import java.io.Serializable;
import java.util.List;

import xyz.noark.core.annotation.orm.Entity;
import xyz.noark.core.annotation.orm.Entity.FeatchType;
import xyz.noark.reflectasm.ConstructorAccess;
import xyz.noark.reflectasm.MethodAccess;

/**
 * 实体映射描述接口.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class EntityMapping<T> {
	protected final Class<T> klass;
	private final MethodAccess methodAccess;
	private final ConstructorAccess<T> constructorAccess;

	// 抓取策略
	protected final FeatchType featchType;
	// 表名
	protected String tableName;
	// 注释
	private String tableComment;

	// 主键字段
	protected FieldMapping primaryId;
	// 玩家ID字段
	protected FieldMapping playerId;
	// 全部属性
	protected List<FieldMapping> fieldInfo;
	

	public EntityMapping(Class<T> klass) {
		this.klass = klass;
		Entity entity = klass.getAnnotation(Entity.class);
		this.featchType = entity.fetch();
		this.methodAccess = MethodAccess.get(klass);
		this.constructorAccess = ConstructorAccess.get(klass);
	}

	public Class<T> getKlass() {
		return klass;
	}

	public FeatchType getFeatchType() {
		return featchType;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableComment() {
		return tableComment;
	}

	public FieldMapping getPrimaryId() {
		return primaryId;
	}

	public FieldMapping getPlayerId() {
		return playerId;
	}

	public List<FieldMapping> getFieldInfo() {
		return fieldInfo;
	}

	/**
	 * 获取主键的值.
	 * 
	 * @param entity 实体对象
	 * @return 对象的主键值
	 */
	public Serializable getPrimaryIdValue(Object entity) {
		return (Serializable) methodAccess.invoke(entity, primaryId.getGetMethodIndex());
	}

	/**
	 * 获取玩家ID的值.
	 * 
	 * @param entity 实体对象
	 * @return 对象的玩家ID
	 */
	public Serializable getPlayerIdValue(Object entity) {
		return (Serializable) methodAccess.invoke(entity, playerId.getGetMethodIndex());
	}

	public Class<T> getEntityClass() {
		return klass;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setPrimaryId(FieldMapping primaryId) {
		this.primaryId = primaryId;
	}

	public void setPlayerId(FieldMapping playerId) {
		this.playerId = playerId;
	}

	public void setFieldInfo(List<FieldMapping> fieldInfo) {
		this.fieldInfo = fieldInfo;
	}

	public List<FieldMapping> getFieldMapping() {
		return fieldInfo;
	}

	public void setTableComment(String tableComment) {
		this.tableComment = tableComment;
	}

	/**
	 * 构造一个回写数据的唯一Key.
	 * <p>
	 * 类的全名+主键值
	 */
	public String getPrimaryKey(Object entity) {
		return new StringBuilder(64).append(klass.getName()).append(':').append(this.getPrimaryIdValue(entity)).toString();
	}

	public T newEntity() {
		return constructorAccess.newInstance();
	}

	public MethodAccess getMethodAccess() {
		return methodAccess;
	}
}