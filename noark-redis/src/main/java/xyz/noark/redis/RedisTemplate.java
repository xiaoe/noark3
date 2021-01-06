package xyz.noark.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

import java.util.List;

import static xyz.noark.log.LogHelper.logger;

/**
 * @author 小流氓[176543888@qq.com]
 */
public class RedisTemplate {
    /**
     * 默认超时（毫秒）
     */
    private static final int DEFAULT_TIMEOUT = 3000;
    /**
     * 默认DB
     */
    private static final int DEFAULT_INDEX = 0;

    private final Redis redis;

    public RedisTemplate(String host, int port) {
        this(host, port, DEFAULT_INDEX);
    }

    public RedisTemplate(String host, int port, int database) {
        this(host, port, null, database);
    }

    public RedisTemplate(String host, int port, String password) {
        this(host, port, password, DEFAULT_INDEX);
    }


    public RedisTemplate(String host, int port, String password, int index) {
        JedisPool pool = new JedisPool(new GenericObjectPoolConfig(), host, port, DEFAULT_TIMEOUT, password, index);
        logger.info("redis info. host={},port={},database={}", host, port, index);
        this.redis = new Redis(pool).ping();
    }


    public Object eval(final String script, List<String> keys, List<String> args) {
        return redis.eval(script, keys, args);
    }

    public Redis opsForList() {
        return redis;
    }

    public Redis opsForValue() {
        return redis;
    }
}
