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
package xyz.noark.orm.accessor.sql;

import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.FieldMapping;

/**
 * SQL专家
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public interface SqlExpert {
    /**
     * 获取创建表的SQL语句.
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @return SQL语句
     */
    <T> String genCreateTableSql(EntityMapping<T> em);

    /**
     * 获取插入的SQL语句.
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @return SQL语句
     */
    <T> String genInsertSql(EntityMapping<T> em);

    /**
     * 获取更新的SQL语句.
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @return SQL语句
     */
    <T> String genUpdateSql(EntityMapping<T> em);

    /**
     * 以玩家ID的方式去查询SQL语句.
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @return SQL语句
     */
    <T> String genSelectByPlayerId(EntityMapping<T> em);

    /**
     * 获取删除的SQL语句.
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @return SQL语句
     */
    <T> String genDeleteSql(EntityMapping<T> em);

    /**
     * 获取查询的SQL语句.
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @return SQL语句
     */
    <T> String genSelectSql(EntityMapping<T> em);

    /**
     * 获取查询全部的SQL语句.
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @return SQL语句
     */
    <T> String genSelectAllSql(EntityMapping<T> em);

    /**
     * 生成带值的一条语句
     *
     * @param <T>    实体对象类型
     * @param em     实体映射对象
     * @param entity 实体类
     * @return SQL语句
     */
    <T> String genInsertSql(EntityMapping<T> em, T entity);

    /**
     * 生成带值的一条语句
     *
     * @param <T>    实体对象类型
     * @param em     实体映射对象
     * @param entity 实体类
     * @return SQL语句
     */
    <T> String genUpdateSql(EntityMapping<T> em, T entity);

    /**
     * 生成带值的一条语句
     *
     * @param <T>    实体对象类型
     * @param em     实体映射对象
     * @param entity 实体类
     * @return SQL语句
     */
    <T> String genDeleteSql(EntityMapping<T> em, T entity);

    /**
     * 生成添加表字段的SQL
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @param fm  属性映射对象
     * @return SQL语句
     */
    <T> String genAddTableColumnSql(EntityMapping<T> em, FieldMapping fm);

    /**
     * 生成更新表字段的SQL
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @param fm  属性映射对象
     * @return SQL语句
     */
    <T> String genUpdateTableColumnSql(EntityMapping<T> em, FieldMapping fm);

    /**
     * 生成删除表字段的SQL
     *
     * @param <T>        实体对象类型
     * @param em         实体映射对象
     * @param columnName 字段名称
     * @return SQL语句
     */
    <T> String genDropTableColumnSql(EntityMapping<T> em, String columnName);

    /**
     * 生成带值的一条语句
     *
     * @param <T> 实体对象类型
     * @param em  实体映射对象
     * @param fm  属性映射对象
     * @return SQL语句
     */
    <T> String genUpdateDefaultValueSql(EntityMapping<T> em, FieldMapping fm);
}