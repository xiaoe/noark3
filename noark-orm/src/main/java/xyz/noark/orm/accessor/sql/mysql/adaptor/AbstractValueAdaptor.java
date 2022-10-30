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

import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.sql.PreparedStatementProxy;

import java.sql.ResultSet;

/**
 * 属性值适配转换接口.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public abstract class AbstractValueAdaptor<T> implements ValueAdaptor {

    @Override
    @SuppressWarnings("unchecked")
    public void parameterToPreparedStatement(EntityMapping<?> em, FieldMapping fm, PreparedStatementProxy pstmt, Object entity, int index) throws Exception {
        T value = (T) em.getMethodAccess().invoke(entity, fm.getGetMethodIndex());
        this.toPreparedStatement(fm, pstmt, value, index);
    }

    /**
     * 属性转化到PreparedStatement中
     *
     * @param fm             属性映射描述
     * @param pstmt          PreparedStatement代理对象
     * @param value          值
     * @param parameterIndex 参数位置
     * @throws Exception 可能出现SQL异常
     */
    protected abstract void toPreparedStatement(FieldMapping fm, PreparedStatementProxy pstmt, T value, final int parameterIndex) throws Exception;

    @Override
    public void resultSetToParameter(EntityMapping<?> em, FieldMapping fm, ResultSet rs, Object result) throws Exception {
        Object value = this.toParameter(fm, rs);
        em.getMethodAccess().invoke(result, fm.getSetMethodIndex(), value);
    }

    /**
     * ResultSet中取出值
     *
     * @param fm 属性映射对象
     * @param rs 结果集
     * @return 返回属性值
     * @throws Exception 可能出现SQL异常
     */
    protected abstract Object toParameter(FieldMapping fm, ResultSet rs) throws Exception;

    @Override
    public void resultSetToPrimaryId(EntityMapping<?> em, FieldMapping fm, ResultSet rs, Object result) throws Exception {
        Object value = this.readGeneratedValue(fm, rs);
        em.getMethodAccess().invoke(result, fm.getSetMethodIndex(), value);
    }

    /**
     * 从Rs里取出自增主键值.
     *
     * @param fm 属性映射对象
     * @param rs 结果集
     * @return 返回自增主键值
     * @throws Exception 可能出现SQL异常
     */
    protected Object readGeneratedValue(FieldMapping fm, ResultSet rs) throws Exception {
        throw new UnrealizedException("此类型不支持自增主键. class=" + fm.getFieldClass().getClass());
    }
}