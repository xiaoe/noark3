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
package xyz.noark.orm.accessor.sql.mysql.adaptor;

import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.sql.PreparedStatementProxy;

import java.sql.ResultSet;

/**
 * 属性值适配转换接口.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.4
 */
public interface ValueAdaptor {
    /**
     * 把结果集中的数据转化为属性值
     *
     * @param em     实体映射描述对象
     * @param fm     属性映射描述对象
     * @param rs     结果集
     * @param result 属性的归属对象
     * @throws Exception 可能会出现转化失败的异常
     */
    void resultSetToParameter(EntityMapping<?> em, FieldMapping fm, ResultSet rs, Object result) throws Exception;

    /**
     * 把属性值转化为查询参数
     *
     * @param em     实体映射描述对象
     * @param fm     属性映射描述对象
     * @param pstmt  PreparedStatement代理对象
     * @param entity 实体类
     * @param index  参数占位编号
     * @throws Exception 可能会出现转化失败的异常
     */
    void parameterToPreparedStatement(EntityMapping<?> em, FieldMapping fm, PreparedStatementProxy pstmt, Object entity, int index) throws Exception;

    /**
     * 把结果集中的自增主键转化为属性值
     *
     * @param em     实体映射描述对象
     * @param fm     属性映射描述对象
     * @param rs     结果集
     * @param result 属性的归属对象
     * @throws Exception 可能会出现转化失败的异常
     */
    void resultSetToPrimaryId(EntityMapping<?> em, FieldMapping fm, ResultSet rs, Object result) throws Exception;
}