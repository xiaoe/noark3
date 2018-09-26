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

import static xyz.noark.log.LogHelper.logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import xyz.noark.core.annotation.PlayerId;
import xyz.noark.core.annotation.orm.Column;
import xyz.noark.core.annotation.orm.Entity;
import xyz.noark.core.annotation.orm.Id;
import xyz.noark.core.annotation.orm.Table;
import xyz.noark.core.exception.NoEntityException;
import xyz.noark.core.util.FieldUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.orm.accessor.FieldType;
import xyz.noark.reflectasm.MethodAccess;

/**
 * 实体对象解析生成器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class AnnotationEntityMaker {

	private static final List<Class<? extends Annotation>> ANNOTATIONS = new ArrayList<>();
	static {
		ANNOTATIONS.add(Column.class);
		ANNOTATIONS.add(Id.class);
		ANNOTATIONS.add(PlayerId.class);
	}

	public <T> EntityMapping<T> make(Class<T> klass) {
		// 没有Entity注解，就认为他不是一个实体对象.
		if (!klass.isAnnotationPresent(Entity.class)) {
			throw new NoEntityException(klass.getName(), "没有@Entity注解标识 ≡ (^(OO)^) ≡");
		}
		return makeEntity(klass);
	}

	private <T> EntityMapping<T> makeEntity(Class<T> klass) {
		EntityMapping<T> em = new EntityMapping<>(klass);

		// 如果没有写TableName 默认为类的简单名称由驼峰式命名变成分割符分隔单词
		Table table = klass.getAnnotation(Table.class);
		em.setTableName((table == null || StringUtils.isEmpty(table.name())) ? StringUtils.lowerWord(klass.getSimpleName(), '_') : table.name());
		if (table != null) {
			em.setTableComment(table.comment());
		}

		// 解析属性
		Field[] fields = FieldUtils.scanAllField(klass, ANNOTATIONS);
		if (fields.length <= 0) {
			// 一个表没有属性，还ORM个蛋蛋~~
			throw new NoEntityException(klass.getName(), "没有可映射的属性 ≡ (^(OO)^) ≡");
		}

		ArrayList<FieldMapping> fieldInfo = new ArrayList<>(fields.length);
		for (Field field : fields) {
			FieldMapping fm = makeFieldMapping(klass, field, em.getMethodAccess());
			if (fm.isPrimaryId()) {
				em.setPrimaryId(fm);
			}
			if (fm.isPlayerId()) {
				em.setPlayerId(fm);
			}
			fieldInfo.add(fm);
		}
		em.setFieldInfo(fieldInfo);
		return em;
	}

	private FieldMapping makeFieldMapping(Class<?> klass, Field field, MethodAccess methodAccess) {
		FieldMapping fm = new FieldMapping(field, methodAccess);
		// 需要解析的解析，有些不要用动的还放注解里面
		if (fm.getColumn() == null || StringUtils.isEmpty(fm.getColumn().name())) {
			fm.setColumnName(StringUtils.lowerWord(field.getName(), '_'));
		} else {
			fm.setColumnName(fm.getColumn().name());
		}

		// 检测下划线命名方式
		if (DataModular.CheckUnderScoreCase && !fm.getColumnName().equals(fm.getColumnName().toLowerCase())) {
			logger.warn("数据库字段应该使用下划线命名方式,请检查{}类中的{}属性({})", klass.getName(), field.getName(), fm.getColumnName());
		}

		guessEntityFieldColumnType(fm);
		return fm;
	}

	/**
	 * 根据字段现有的信息，尽可能猜测一下字段的数据库类型
	 * 
	 * @param fm 映射字段
	 */
	public void guessEntityFieldColumnType(FieldMapping fm) {
		Type type = fm.getField().getGenericType();
		// 明确标识为时间类型的属性
		if (type == Date.class) {
			fm.setType(FieldType.AsDate);
		}
		// JDK8的时间
		else if (type == LocalDateTime.class) {
			fm.setType(FieldType.AsLocalDateTime);
		}
		// 明确标识为JSON类型的属性
		else if (fm.isJson()) {
			fm.setType(FieldType.AsJson);
			fm.setWidth(fm.getColumn() == null ? 1024 : fm.getColumn().length());
		}
		// 整型
		else if (fm.isInt()) {
			fm.setWidth(8);
			fm.setType(FieldType.AsInteger);
		}
		// 字符串
		else if (fm.isString()) {
			fm.setType(FieldType.AsString);
			fm.setWidth(fm.getColumn() == null ? 255 : fm.getColumn().length());
		}
		// 长整型
		else if (fm.isLong()) {
			fm.setWidth(16);
			fm.setType(FieldType.AsLong);
		}
		// 时间
		else if (Instant.class == type) {
			fm.setType(FieldType.AsInstant);
		}
		// 布尔
		else if (fm.isBoolean()) {
			fm.setType(FieldType.AsBoolean);
		}
		// Float
		else if (fm.isFloat()) {
			fm.setType(FieldType.AsFloat);
		}
		// Double
		else if (fm.isDouble()) {
			fm.setType(FieldType.AsDouble);
		}
		// AtomicInteger
		else if (type == AtomicInteger.class) {
			fm.setWidth(8);
			fm.setType(FieldType.AsAtomicInteger);
		}
		// Blob或byte[]
		else if (fm.isBlob()) {
			fm.setType(FieldType.AsBlob);
		}
		// 其他就是Json类型的.
		else {
			fm.setType(FieldType.AsJson);
			fm.setWidth(fm.getColumn() == null ? 1024 : fm.getColumn().length());
		}
	}
}