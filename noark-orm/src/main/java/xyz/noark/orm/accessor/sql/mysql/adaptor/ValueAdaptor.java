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