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
package xyz.noark.redis;

import redis.clients.jedis.params.SetParams;

/**
 * Redis之简单的KV操作.
 *
 * @author 小流氓[176543888@qq.com]
 */
public interface ValueOperations {

    /**
     * 获取指定键Key所关联的字符串值.
     * <p>
     * 如果key不存在那么返回特殊值null<br>
     * 假如key储存的值不是字符串类型，返回一个错误，因为 GET只能用于处理字符串值。
     * <p>
     * 可用版本： &gt;= 1.0.0<br>
     * 时间复杂度： O(1)
     *
     * @param key 指定键Key
     * @return 当key不存在时，返回 null，否则，返回 key的值
     */
    String get(final String key);

    /**
     * 为指定键Key设计指定值Value.
     * <p>
     * 如果key已经持有其他值， SET就覆写旧值，无视类型。<br>
     * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
     * <p>
     * 可用版本： &gt;= 1.0.0<br>
     * 时间复杂度： O(1)
     *
     * @param key   指定键Key
     * @param value 指定值Value
     * @return 返回OK。
     */
    String set(final String key, String value);

    /**
     * 为指定键Key设计指定值Value.
     * <p>
     * 可用版本： &gt;= 1.0.0<br>
     * 时间复杂度： O(1)
     *
     * @param key    指定键Key
     * @param value  指定值Value
     * @param params NX|XX, NX=只在键不存在时， XX=只在键已经存在时
     *               EX|PX, 过期时间: EX=秒; PX=豪秒
     * @return 从 Redis 2.6.12 版本开始， SET在设置操作成功完成时，才返回 OK。
     * 如果设置了NX或者XX，但因为条件没达到而造成设置操作未执行，那么命令返回空批量回复（NULL Bulk Reply）。
     */
    String set(final String key, String value, SetParams params);
}