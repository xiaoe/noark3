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
package xyz.noark.redis;

import static xyz.noark.log.LogHelper.logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;
import xyz.noark.core.exception.ServerBootstrapException;

/**
 * Redis操作类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class Redis {
	/** 默认超时（毫秒） */
	private static final int DEFAULT_TIMEOUT = 3000;
	/** 默认DB */
	private static final int DEFAULT_INDEX = 0;
	/** Ping-Pong 响应值 */
	private static final String PONG = "PONG";

	/** REDIS链接池 */
	private final JedisPool pool;

	public Redis(String host, int port) {
		this(host, port, DEFAULT_INDEX);
	}

	public Redis(String host, int port, int database) {
		this(host, port, null, database);
	}

	public Redis(String host, int port, String password) {
		this(host, port, password, DEFAULT_INDEX);
	}

	public Redis(String host, int port, String password, int index) {
		this.pool = new JedisPool(new GenericObjectPoolConfig(), host, port, DEFAULT_TIMEOUT, password, index);
		logger.info("redis info. host={},port={},database={}", host, port, index);
	}

	public Redis ping() {
		try (Jedis j = pool.getResource()) {
			if (PONG.equals(j.ping())) {
				logger.info("Redis链接正常.");
			} else {
				logger.warn("Redis链接异常.");
			}
		} catch (Exception e) {
			throw new ServerBootstrapException("Redis ping exception.", e);
		}
		return this;
	}

	// ------------------------------Key相关命令------------------------------

	/**
	 * 删除给定的一个或多个key.
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(N)， N 为被删除的 key 的数量。<br>
	 * 删除单个字符串类型的 key ，时间复杂度为O(1)。<br>
	 * 删除单个列表、集合、有序集合或哈希表类型的 key ，时间复杂度为O(M)， M 为以上数据结构内的元素数量。
	 * <p>
	 * 不存在的 key 会被忽略。
	 * 
	 * @param keys 一个或多个key
	 * @return 被删除 key 的数量
	 */
	public long del(String... keys) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.del(keys);
		}
	}

	/**
	 * 序列化指定的键key，并返回被序列化的值，使用{@link #restore(String, int, byte[])}命令可以将这个值反序列化为Redis键.
	 * <p>
	 * 序列化生成的值有以下几个特点：
	 * <p>
	 * 它带有 64 位的校验和，用于检测错误， RESTORE 在进行反序列化之前会先检查校验和。<br>
	 * 值的编码格式和 RDB 文件保持一致。<br>
	 * RDB版本会被编码在序列化值当中，如果因为Redis的版本不同造成RDB格式不兼容，那么Redis会拒绝对这个值进行反序列化操作。<br>
	 * 序列化的值不包括任何生存时间信息。
	 * <p>
	 * 
	 * 可用版本： &gt;= 2.6.0<br>
	 * 时间复杂度：<br>
	 * 查找给定键的复杂度为O(1)，对键进行序列化的复杂度为
	 * O(N*M)，其中N是构成key的Redis对象的数量，而M则是这些对象的平均大小。<br>
	 * 如果序列化的对象是比较小的字符串，那么复杂度为 O(1) 。<br>
	 * 
	 * @param key 指定的键
	 * @return 如果key不存在，那么返回null,否则，返回序列化之后的值。
	 */
	public byte[] dump(String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.dump(key);
		}
	}

	/**
	 * 反序列化给定的序列化值，并将它和给定的key关联。
	 * <p>
	 * RESTORE在执行反序列化之前会先对序列化值的RDB版本和数据校验和进行检查，如果RDB版本不相同或者数据不完整的话，那么
	 * RESTORE会拒绝进行反序列化，并返回一个错误。
	 * <p>
	 * 更多信息可以参考{@link #dump(String)}命令。
	 * <p>
	 * 可用版本： &gt;= 2.6.0<br>
	 * 时间复杂度：<br>
	 * 查找给定键的复杂度为 O(1)，对键进行反序列化的复杂度为
	 * O(N*M)，其中N是构成key的Redis对象的数量，而M则是这些对象的平均大小。<br>
	 * 有序集合(sorted set)的反序列化复杂度为 O(N*M*log(N)) ，因为有序集合每次插入的复杂度为 O(log(N)) 。<br>
	 * 如果反序列化的对象是比较小的字符串，那么复杂度为 O(1) 。<br>
	 * 
	 * @param key 指定的键
	 * @param ttl 以毫秒为单位为key设置生存时间；如果ttl为 0 ，那么不设置生存时间。
	 * @param serializedValue 要反序列化的字节数组
	 * @return 如果反序列化成功那么返回OK，否则返回一个错误。
	 */
	public String restore(final String key, final int ttl, final byte[] serializedValue) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.restore(key, ttl, serializedValue);
		}
	}

	/**
	 * 判定指定的键是否存在。
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定的键
	 * @return 存在返回true,否则返回false.
	 */
	public boolean exists(final String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.exists(key);
		}
	}

	/**
	 * 给指定键Key设置生存时间，当key过期时(生存时间为 0)，它会被自动删除。
	 * <p>
	 * 更新生存时间<br>
	 * 可以对一个已经带有生存时间的 key 执行 EXPIRE 命令，新指定的生存时间会取代旧的生存时间。
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键
	 * @param seconds 秒
	 * @return 设置成功返回 1 。 当key不存在或者不能为 key设置生存时间时，返回 0 。
	 */
	public long expire(final String key, int seconds) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.expire(key, seconds);
		}
	}

	/**
	 * EXPIREAT 的作用和 {@link Redis#expire(String, int)} 类似，都用于为key设置生存时间。<br>
	 * 不同在于EXPIREAT命令接受的时间参数是UNIX时间戳(unix timestamp)。
	 * <p>
	 * 可用版本： &gt;= 1.2.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键
	 * @param unixTime UNIX时间戳
	 * @return 如果生存时间设置成功，返回 1 。 当key不存在或没办法设置生存时间，返回 0 。
	 */
	public long expireAt(final String key, long unixTime) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.expireAt(key, unixTime);
		}
	}

	/**
	 * 查找所有符合给定模式pattern的key.
	 * <p>
	 * 例：
	 * 
	 * <pre>
	 * KEYS * 匹配数据库中所有 key 。
	 * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
	 * KEYS h*llo 匹配 hllo 和 heeeeello 等。
	 * KEYS h[ae]llo 匹配 hello和 hallo ，但不匹配 hillo 。
	 * 特殊符号用 \ 转义
	 * </pre>
	 * 
	 * <b>注意：没事不要瞎用这个方法，会死人的</b>
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(N)， N 为数据库中 key 的数量。
	 * 
	 * @param pattern 给定模式
	 * @return 符合给定模式的key集合.
	 */
	public Set<String> keys(String pattern) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.keys(pattern);
		}
	}

	// ------------------------------String相关命令------------------------------

	/**
	 * 将指定键key所储存的值减去1.
	 * <p>
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。<br>
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。<br>
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键key
	 * @return 减去1之后key的值。
	 */
	public long decr(final String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.decr(key);
		}
	}

	/**
	 * 将指定键key所储存的值减去减量decrement.
	 * <p>
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。<br>
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键key
	 * @param decrement 要减少的值
	 * @return 减去decrement之后， key的值。
	 */
	public long decrBy(final String key, final long decrement) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.decrBy(key, decrement);
		}
	}

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
	public String get(final String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.get(key);
		}
	}

	/**
	 * 将指定键Key中储存的数字值增加1。
	 * <p>
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。<br>
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。<br>
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键Key
	 * @return 执行INCR命令之后key的值
	 */
	public long incr(final String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.incr(key);
		}
	}

	/**
	 * 将 key 所储存的值加上增量 increment
	 * <p>
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令<br>
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。<br>
	 * 
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定Key
	 * @param increment 要增加的值
	 * @return 加上increment之后，key的值
	 */
	public long incrBy(final String key, long increment) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.incrBy(key, increment);
		}
	}

	/**
	 * 返回一个或多个指定键Key的值集合.
	 * <p>
	 * 如果给定的 key里面，有某个 key不存在，那么这个 key返回特殊值null。因此，该命令永不失败。<br>
	 * 
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度: O(N) , N 为给定 key 的数量。
	 * 
	 * @param keys 指定键Key
	 * @return 一个包含指定键Key的值的列表
	 */
	public List<String> mget(String... keys) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.mget(keys);
		}
	}

	/**
	 * 同时设置一个或多个key-value对
	 * <p>
	 * 如果某个给定 key已经存在，那么 MSET会用新值覆盖原来的旧值， <br>
	 * 如果这不是你所希望的效果，请考虑使用 MSETNX命令它只会在所有给定 key都不存在的情况下进行设置操作。 <br>
	 * MSET是一个原子性(atomic)操作，所有给定 key都会在同一时间内被设置
	 * <p>
	 * 可用版本： &gt;= 1.0.1<br>
	 * 时间复杂度： O(N)， N 为要设置的 key 数量。
	 * <p>
	 * 总是返回 OK (因为 MSET 不可能失败)
	 * 
	 * @param keysvalues key-value对
	 */
	public void mset(String... keysvalues) {
		try (Jedis jedis = pool.getResource()) {
			jedis.mset(keysvalues);
		}
	}

	/**
	 * 同时设置一个或多个key-value对，当且仅当所有给定 key都不存在。
	 * <p>
	 * 即使只有一个给定 key已存在， MSETNX也会拒绝执行所有给定key的设置操作。<br>
	 * MSETNX 是原子性的，因此它可以用作设置多个不同 key表示不同字段(field)的唯一性逻辑对象(unique logic
	 * object)，所有字段要么全被设置，要么全不被设置。<br>
	 * <p>
	 * 可用版本： &gt;= 1.0.1<br>
	 * 时间复杂度： O(N)， N为要设置的 key的数量。<br>
	 * 
	 * @param keysvalues key-value对
	 * @return 当所有 key 都成功设置，返回 1 。 如果所有给定 key 都设置失败(至少有一个 key 已经存在)，那么返回 0 。
	 */
	public long msetnx(String... keysvalues) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.msetnx(keysvalues);
		}
	}

	/**
	 * 为指定键Key设计指定值Value.
	 * <p>
	 * 如果key已经持有其他值， SET就覆写旧值，无视类型。<br>
	 * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键Key
	 * @param value 指定值Value
	 * @return 返回OK。
	 */
	public String set(final String key, String value) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.set(key, value);
		}
	}

	/**
	 * 为指定键Key设计指定值Value.
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键Key
	 * @param value 指定值Value
	 * @param nxxx NX|XX, NX=只在键不存在时， XX=只在键已经存在时
	 * @return 返回OK。
	 */
	public String set(final String key, String value, String nxxx) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.set(key, value, nxxx);
		}
	}

	/**
	 * 为指定键Key设计指定值Value.
	 * <p>
	 * 可用版本： &gt;= 2.6.12<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键Key
	 * @param value 指定值Value
	 * @param nxxx NX|XX, NX=只在键不存在时， XX=只在键已经存在时
	 * @param expx EX|PX, 过期时间: EX=秒; PX=豪秒
	 * @param time 过期时间值.
	 * @return 从 Redis 2.6.12 版本开始， SET在设置操作成功完成时，才返回 OK。
	 *         如果设置了NX或者XX，但因为条件没达到而造成设置操作未执行，那么命令返回空批量回复（NULL Bulk Reply）。
	 */
	public String set(final String key, String value, String nxxx, String expx, final int time) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.set(key, value, nxxx, expx, time);
		}
	}

	/**
	 * 为指定键Key设计指定值Value.
	 * <p>
	 * 可用版本： &gt;= 2.6.12<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定键Key
	 * @param value 指定值Value
	 * @param nxxx NX|XX, NX=只在键不存在时， XX=只在键已经存在时
	 * @param expx EX|PX, 过期时间: EX=秒; PX=豪秒
	 * @param time 过期时间值.
	 * @return 从 Redis 2.6.12 版本开始， SET在设置操作成功完成时，才返回 OK。
	 *         如果设置了NX或者XX，但因为条件没达到而造成设置操作未执行，那么命令返回空批量回复（NULL Bulk Reply）。
	 */
	public String set(final String key, String value, String nxxx, String expx, final long time) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.set(key, value, nxxx, expx, time);
		}
	}

	// ------------------------------Hash相关命令------------------------------

	/**
	 * 删除指定Key的哈希表中的一个或多个键，不存在的键将被忽略
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度: O(N)， N 为要删除的键的数量。<br>
	 * 
	 * @param key 指定Key的哈希表
	 * @param field 指定要删除的键
	 * @return 被成功删除的键的数量，不包括被忽略的键。
	 */
	public long hdel(String key, String... field) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hdel(key, field);
		}
	}

	/**
	 * 判定指定Key的哈希表中是否存在指定键
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定Key的哈希表
	 * @param field 指定键
	 * @return 如果哈希表存在指定键则返回true，否则返回false.
	 */
	public boolean hexists(String key, String field) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hexists(key, field);
		}
	}

	/**
	 * 获取指定Key的哈希表中指定键所对应的值。
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定Key的哈希表
	 * @param field 指定字段
	 * @return 如果存在此字段则返回所对应的值，否则返回null
	 */
	public String hget(String key, String field) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hget(key, field);
		}
	}

	/**
	 * 获取指定Key的哈希表中所有的键和值。
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(N)， N 为哈希表的大小。
	 * 
	 * @param key 指定Key的哈希表
	 * @return 如果指定Key的哈希表存在，则返回此集合，否则返回空集合.
	 */
	public Map<String, String> hgetAll(String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hgetAll(key);
		}
	}

	/**
	 * 增加指定Key的哈希表中指定键的数值.
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(1)
	 * <p>
	 * 增量也可以为负数，相当于对给定域进行减法操作。<br>
	 * 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。<br>
	 * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。<br>
	 * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。<br>
	 * 本操作的值被限制在 64 位(bit)有符号数字表示之内。<br>
	 * 
	 * @param key 指定Key的哈希表
	 * @param field 指定键
	 * @param value 要加的数值
	 * @return 增值操作执行后该键的数值
	 */
	public long hincrBy(String key, String field, long value) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hincrBy(key, field, value);
		}
	}

	/**
	 * 获取指定Key的哈希表中所有键.
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(N)， N 为哈希表的大小。
	 * 
	 * @param key 指定Key的哈希表
	 * @return 一个包含哈希表中所有键的集合,如果指定Key的哈希表不存在则返回一个空集合.
	 */
	public Set<String> hkeys(String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hkeys(key);
		}
	}

	/**
	 * 返回指定Key的哈希表中所有键的数量。
	 * <p>
	 * 时间复杂度： O(1)
	 * 
	 * @param key 指定Key的哈希表
	 * @return 所有键的数量,如果指定的Key不存在则返回0
	 */
	public long hlen(String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hlen(key);
		}
	}

	/**
	 * 获取指定Key的哈希表中指定键所对应值的列表.
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(N)， N 为给定键的数量。
	 * <p>
	 * <b>注意：</b><br>
	 * 因为不存在的 key被当作一个空哈希表来处理，所以对一个不存在的 key进行 HMGET操作将返回一个只带有null值的列表。
	 * 
	 * <pre>
	 * redis.hmget("not-exist", "a", "b");
	 * out:[null, null]
	 * </pre>
	 * 
	 * @param key 指定Key的哈希表
	 * @param fields 指定键列表
	 * @return 一个包含多个给定键的关联值的列表，列表值的排列顺序和给定键参数的请求顺序一样。
	 */
	public List<String> hmget(String key, String... fields) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hmget(key, fields);
		}
	}

	/**
	 * 同时将多个field-value(键-值)对设置到指定Key的哈希表中。
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(N)， N 为 field-value 对的数量。
	 * <p>
	 * 此命令会覆盖哈希表中已存在的键<br>
	 * 如果 key不存在，一个空哈希表被创建并执行 HMSET操作。<br>
	 * 
	 * @param key 指定Key的哈希表
	 * @param value Hash键值
	 */
	public void hmset(String key, Map<String, String> value) {
		try (Jedis jedis = pool.getResource()) {
			jedis.hmset(key, value);
		}
	}

	/**
	 * 将指定Key的哈希表中键field的值设置为value.
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(1)
	 * <p>
	 * 如果key不存在，一个新的哈希表被创建并进行HSET操作。<br>
	 * 如果键field已经存在于哈希表中，旧值将被覆盖。
	 * 
	 * @param key 指定Key的哈希表
	 * @param field 键
	 * @param value 值
	 * @return 如果键field是一个新键，并且值设置成功，返回 1 。 如果已经存在且旧值已被新值覆盖，返回 0
	 */
	public Long hset(String key, String field, String value) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hset(key, field, value);
		}
	}

	/**
	 * 将指定Key的哈希表中键field的值设置为value，当且仅当键field不存在。
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(1)
	 * <p>
	 * 若域 field 已经存在，该操作无效。<br>
	 * 如果 key不存在，一个新哈希表被创建并执行HSETNX命令。
	 * 
	 * @param key 指定Key的哈希表
	 * @param field 键
	 * @param value 值
	 * @return 设置成功，返回 1 。 如果给定键已经存在且没有操作被执行，返回 0 。
	 */
	public long hsetnx(String key, String field, String value) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hsetnx(key, field, value);
		}
	}

	/**
	 * 获取指定Key的哈希表中所有值的集合。
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(N)， N 为哈希表的大小。
	 * 
	 * @param key 指定Key的哈希表
	 * @return 一个包含哈希表中所有值的集合,当key不存在时，返回一个空集合
	 */
	public List<String> hvals(String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.hvals(key);
		}
	}

	// ------------------------------SortedSet相关命令------------------------------

	/**
	 * 将一个member元素及其score值加入到有序集key当中.
	 * <p>
	 * 如果某 member已经是有序集的成员，那么更新这个member的score值，并通过重新插入这个member元素，来保证该
	 * member在正确的位置上。<br>
	 * score 值可以是整数值或双精度浮点数。<br>
	 * 如果key不存在，则创建一个空的有序集并执行ZADD操作。
	 * <p>
	 * 可用版本： &gt;= 1.2.0<br>
	 * 时间复杂度: O(M*log(N))，N是有序集的基数，M为成功添加的新成员的数量。
	 * 
	 * @param key 指定键Key
	 * @param score 成员分数值
	 * @param member 成员编码
	 * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 */
	public long zadd(final String key, final double score, final String member) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zadd(key, score, member);
		}
	}

	/**
	 * 将一个或多个member元素及其score值加入到有序集key当中.
	 * <p>
	 * 
	 * 如果某 member已经是有序集的成员，那么更新这个member的score值，并通过重新插入这个member元素，来保证该
	 * member在正确的位置上。<br>
	 * score 值可以是整数值或双精度浮点数。<br>
	 * 如果key不存在，则创建一个空的有序集并执行ZADD操作。
	 * <p>
	 * 可用版本： &gt;= 1.2.0<br>
	 * 时间复杂度: O(M*log(N))，N是有序集的基数，M为成功添加的新成员的数量。
	 * 
	 * @param key 指定键Key
	 * @param scoreMembers 带有分值的成员集合
	 * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 */
	public long zadd(final String key, Map<String, Double> scoreMembers) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zadd(key, scoreMembers);
		}
	}

	/**
	 * 获取指定Key的有序集里成员数量.
	 * <p>
	 * 可用版本： &gt;= 1.2.0<br>
	 * 时间复杂度: O(1)
	 * 
	 * @param key 指定有序集Key
	 * @return 成员数量,就算key不存在也会返回0.
	 */
	public long zcard(final String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zcard(key);
		}
	}

	/**
	 * 统计指定Key的有序集中，score值在min和max之间(默认包括score值等于min或max)的成员的数量.
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度: O(log(N)+M)，N为有序集的基数，M为值在min和max之间的元素的数量。
	 * 
	 * @param key 指定有序集Key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return score值在min和max之间的成员的数量
	 */
	public long zcount(final String key, final double min, final double max) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zcount(key, min, max);
		}
	}

	/**
	 * 统计指定Key的有序集中，score值在min和max之间(默认包括score值等于min或max)的成员的数量.
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度: O(log(N)+M)，N为有序集的基数，M为值在min和max之间的元素的数量。
	 * <p>
	 * 区别于上面的分值，这里的参数为String<br>
	 * 
	 * <pre>
	 * 1. -inf 和 +inf<br>
	 * 2. 返回所有符合条件 1 &lt; score &lt;= 5 的成员, zcount test (1 5<br>
	 * 3. 则返回所有符合条件 5 &lt; score &lt; 10 的成员, zcount test (5 (10
	 * </pre>
	 * 
	 * @param key 指定有序集Key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return score值在min和max之间的成员的数量
	 */
	public long zcount(final String key, final String min, final String max) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zcount(key, min, max);
		}
	}

	/**
	 * 为有序集key的成员member的score值加上增量increment.
	 * <p>
	 * 1. 可以通过传递一个负数值increment，让score减去相应的值，<br>
	 * 比如 ZINCRBY key -5 member，就是让member的score值减去 5 。<br>
	 * 2. 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment member<br>
	 * 等同于 ZADD key increment member 。<br>
	 * 3. 当 key 不是有序集类型时，返回一个错误。<br>
	 * 4. score 值可以是整数值或双精度浮点数。
	 * <p>
	 * 可用版本： &gt;= 1.2.0<br>
	 * 时间复杂度: O(log(N))
	 * 
	 * @param key 有序集key
	 * @param increment 要增加的分值
	 * @param member 成员
	 * @return member成员的新score值
	 */
	public double zincrby(final String key, final double increment, final String member) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zincrby(key, increment, member);
		}
	}

	/**
	 * 返回有序集 key中，指定区间内的成员
	 * <p>
	 * 其中成员的位置按 score 值递增(从小到大)来排序。<br>
	 * 具有相同 score 值的成员按字典序(lexicographical order )来排列。<br>
	 * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。<br>
	 * <p>
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。<br>
	 * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
	 * <p>
	 * 可用版本： &gt;= 1.2.0<br>
	 * 时间复杂度: O(log(N)+M)， N 为有序集的基数，而 M 为结果集的基数。
	 * 
	 * @param key 有序集key
	 * @param start 开始下标
	 * @param end 结束下标
	 * @return 指定区间内有序集成员的列表
	 */
	public Set<String> zrange(final String key, final long start, final long end) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zrange(key, start, end);
		}
	}

	/**
	 * 返回有序集 key中，指定区间内的成员
	 * <p>
	 * 等同{@link #zrange(String, long, long)}方法，只是返回值多了一个分值
	 * 
	 * @param key 有序集key
	 * @param start 开始下标
	 * @param end 结束下标
	 * @return 指定区间内，带有score值的有序集成员的列表
	 */
	public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zrangeWithScores(key, start, end);
		}
	}

	/**
	 * 返回有序集key中成员member的排名
	 * <p>
	 * 其中有序集成员按score值递增(从小到大)顺序排列。<br>
	 * 排名以 0 为底，也就是说， score 值最小的成员排名为 0<br>
	 * 使用 ZREVRANK 命令可以获得成员按 score 值递减(从大到小)排列的排名
	 * 
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度: O(log(N))
	 * 
	 * @param key 有序集key
	 * @param member 成员
	 * @return 如果member是有序集key的成员，返回member的排名。 如果member不是有序集key的成员，返回null
	 */
	public Long zrank(final String key, final String member) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zrank(key, member);
		}
	}

	/**
	 * 移除有序集key中的一个或多个成员，不存在的成员将被忽略
	 * <p>
	 * 可用版本： &gt;= 1.2.0<br>
	 * 时间复杂度: O(M*log(N))，N为有序集的基数，M为被成功移除的成员的数量。
	 * 
	 * @param key 有序集key
	 * @param members 成员列表
	 * @return 被成功移除的成员的数量，不包括被忽略的成员。
	 */
	public long zrem(final String key, final String... members) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zrem(key, members);
		}
	}

	/**
	 * 返回有序集key中成员member的排名。
	 * <p>
	 * 其中有序集成员按 score值递减(从大到小)排序。<br>
	 * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。<br>
	 * 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名。
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度: O(log(N))
	 * 
	 * @param key 有序集key
	 * @param member 成员
	 * @return 如果member是有序集key的成员，返回member的排名。 如果member不是有序集key的成员，返回null
	 */
	public Long zrevrank(final String key, final String member) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zrevrank(key, member);
		}
	}

	/**
	 * 返回有序集 key中，指定区间内的成员
	 * <p>
	 * 等同{@link #zrevrank(String, String)}方法，只是返回值多了一个分值
	 * 
	 * @param key 有序集key
	 * @param start 开始下标
	 * @param end 结束下标
	 * @return 指定区间内，带有score值的有序集成员的列表
	 */
	public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zrevrangeWithScores(key, start, end);
		}
	}

	/**
	 * 返回有序集key中，成员member的score值
	 * <p>
	 * 如果 member元素不是有序集 key的成员，或 key不存在，返回 null 。
	 * <p>
	 * 可用版本： &gt;= 1.2.0<br>
	 * 时间复杂度: O(1)
	 * 
	 * @param key 有序集key
	 * @param member 成员
	 * @return member成员的score值
	 */
	public Double zscore(final String key, final String member) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.zscore(key, member);
		}
	}

	// ------------------------------Pub/Sub（发布/订阅）------------------------------

	/**
	 * 订阅一个或多个符合给定模式的频道
	 * <p>
	 * 
	 * 每个模式以*作为匹配符，比如it*匹配所有以it开头的频道(it.news、it.blog、it.tweets等等)，<br>
	 * news.* 匹配所有以 news.开头的频道(news.it、news.global.today等等)，诸如此类。
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(N)， N是订阅的模式的数量。
	 * 
	 * @param jedisPubSub 订阅处理接口
	 * @param patterns 给定模式
	 */
	public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
		try (Jedis jedis = pool.getResource()) {
			jedis.psubscribe(jedisPubSub, patterns);
		}
	}

	/**
	 * 将信息message发送到指定的频道channel.
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(N+M)，其中N是频道channel的订阅者数量，而M则是使用模式订阅(subscribed patterns)的客户端的数量
	 * 
	 * @param channel 频道
	 * @param message 消息
	 */
	public void publish(final String channel, final String message) {
		try (Jedis jedis = pool.getResource()) {
			jedis.publish(channel, message);
		}
	}

	/**
	 * 订阅给定的一个或多个频道的信息.
	 * <p>
	 * 可用版本： &gt;= 2.0.0<br>
	 * 时间复杂度： O(N)，其中 N 是订阅的频道的数量
	 * 
	 * @param jedisPubSub 订阅处理接口
	 * @param channels 频道
	 */
	public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
		try (Jedis jedis = pool.getResource()) {
			jedis.subscribe(jedisPubSub, channels);
		}
	}

	// ------------------------------Sets 集合------------------------------
	/**
	 * 添加一个或多个指定的member元素到集合的key中.
	 * <p>
	 * 指定的一个或者多个元素member 如果已经在集合key中存在则忽略.<br>
	 * 如果集合key 不存在，则新建集合key,并添加member元素到集合key中. <br>
	 * 如果key 的类型不是集合则返回错误.
	 * <p>
	 * 可用版本： &gt;= 1.0.0<br>
	 * 时间复杂度： O(N) 其中N是添加成员的数量
	 * 
	 * @param key 集合key
	 * @param members 多个成员
	 * @return 返回新成功添加到集合里元素的数量，不包括已经存在于集合中的元素.
	 */
	public Long sadd(final String key, final String... members) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.sadd(key, members);
		}
	}

	// ------------------------------Lua脚本------------------------------

	/**
	 * 执行Lua脚本.
	 * <p>
	 * 脚本具备原子性
	 * <p>
	 * 可用版本： &gt;= 2.6.0<br>
	 * 
	 * @param script 脚本
	 * @return 脚本返回值
	 */
	public Object eval(final String script) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.eval(script);
		}
	}

	/**
	 * 销毁链接.
	 */
	public void destory() {
		pool.destroy();
	}

	/**
	 * 获取Jedis访问对象.
	 * 
	 * <pre>
	 * try (Jedis jedis = redis.getJedis()) {
	 * 	jedis.doSomething();
	 * }
	 * </pre>
	 * 
	 * @return Jedis访问对象
	 */
	public Jedis getJedis() {
		return pool.getResource();
	}
}