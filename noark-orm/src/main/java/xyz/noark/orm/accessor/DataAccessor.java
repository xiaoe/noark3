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
package xyz.noark.orm.accessor;

import xyz.noark.orm.EntityMapping;

import java.io.Serializable;
import java.util.List;

/**
 * 数据访问策略接口.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public interface DataAccessor {
    /**
     * 猜测一下用的DB信息
     */
    public void judgeAccessType();

    /**
     * 检查实体类对应的数据库中的表结构.
     * <p>
     * 如果是关系型数据库，没有表则创建表，属性不一样就修改成一样的.
     *
     * @param <T> 实体对象类型
     * @param em  对象实体描述类.
     */
    public <T> void checkupEntityFieldsWithDatabase(EntityMapping<T> em);

    /**
     * 插入一条数据.
     *
     * @param <T>    实体对象类型
     * @param em     对象实体描述类.
     * @param entity 对象数据.
     * @return 返回插入所受影响行数.
     */
    public <T> int insert(EntityMapping<T> em, T entity);

    /**
     * 插入一批数据.
     *
     * @param <T>     实体对象类型
     * @param em      对象实体描述类.
     * @param entitys 一批对象数据
     * @return 返回插入所受影响行数
     */
    public <T> int[] batchInsert(EntityMapping<T> em, List<T> entitys);

    /**
     * 删除一条数据.
     *
     * @param <T>    实体对象类型
     * @param em     对象实体描述类.
     * @param entity 对象数据.
     * @return 返回删除所受影响行数.
     */
    public <T> int delete(EntityMapping<T> em, T entity);

    /**
     * 删除一批数据.
     *
     * @param <T>     实体对象类型
     * @param em      对象实体描述类.
     * @param entitys 一批对象数据
     * @return 返回删除所受影响行数.
     */
    public <T> int[] batchDelete(EntityMapping<T> em, List<T> entitys);

    /**
     * 修改一条数据.
     *
     * @param <T>    实体对象类型
     * @param em     对象实体描述类.
     * @param entity 对象数据.
     * @return 返回修改所受影响行数.
     */
    public <T> int update(EntityMapping<T> em, T entity);

    /**
     * 修改一批数据.
     *
     * @param <T>     实体对象类型
     * @param em      对象实体描述类.
     * @param entitys 一批对象数据
     * @return 返回修改所受影响行数.
     */
    public <T> int[] batchUpdate(EntityMapping<T> em, List<T> entitys);

    /**
     * 加载一个指定ID的数据.
     *
     * @param <T> 实体对象类型
     * @param <K> 实体主键类型
     * @param em  对象实体描述类.
     * @param id  对象Id.
     * @return 返回对象数据.
     */
    public <T, K extends Serializable> T load(EntityMapping<T> em, K id);

    /**
     * 加载表里所有的数据.
     *
     * @param <T> 实体对象类型
     * @param em  对象实体描述类.
     * @return 返回对象数据列表，就算没有数据，也会返回空列表.
     */
    public <T> List<T> loadAll(EntityMapping<T> em);

    /**
     * 加载指定角色Id对应模块数据.
     *
     * @param <T>      实体对象类型
     * @param em       对象实体描述类
     * @param playerId 玩家Id
     * @return 返回这个角色Id的模块数据，就算没有数据，也会返回空列表.
     */
    public <T> List<T> loadAll(EntityMapping<T> em, Serializable playerId);
}