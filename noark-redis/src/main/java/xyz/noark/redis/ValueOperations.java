package xyz.noark.redis;

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
     * @param key   指定键Key
     * @param value 指定值Value
     * @param nxxx  NX|XX, NX=只在键不存在时， XX=只在键已经存在时
     * @return 返回OK。
     */
    String set(final String key, String value, String nxxx);

    /**
     * 为指定键Key设计指定值Value.
     * <p>
     * 可用版本： &gt;= 2.6.12<br>
     * 时间复杂度： O(1)
     *
     * @param key   指定键Key
     * @param value 指定值Value
     * @param nxxx  NX|XX, NX=只在键不存在时， XX=只在键已经存在时
     * @param expx  EX|PX, 过期时间: EX=秒; PX=豪秒
     * @param time  过期时间值.
     * @return 从 Redis 2.6.12 版本开始， SET在设置操作成功完成时，才返回 OK。
     * 如果设置了NX或者XX，但因为条件没达到而造成设置操作未执行，那么命令返回空批量回复（NULL Bulk Reply）。
     */
    String set(final String key, String value, String nxxx, String expx, final int time);

    /**
     * 为指定键Key设计指定值Value.
     * <p>
     * 可用版本： &gt;= 2.6.12<br>
     * 时间复杂度： O(1)
     *
     * @param key   指定键Key
     * @param value 指定值Value
     * @param nxxx  NX|XX, NX=只在键不存在时， XX=只在键已经存在时
     * @param expx  EX|PX, 过期时间: EX=秒; PX=豪秒
     * @param time  过期时间值.
     * @return 从 Redis 2.6.12 版本开始， SET在设置操作成功完成时，才返回 OK。
     * 如果设置了NX或者XX，但因为条件没达到而造成设置操作未执行，那么命令返回空批量回复（NULL Bulk Reply）。
     */
    String set(final String key, String value, String nxxx, String expx, final long time);
}
