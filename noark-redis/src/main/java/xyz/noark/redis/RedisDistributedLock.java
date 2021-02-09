package xyz.noark.redis;

import xyz.noark.core.lock.DistributedLock;
import xyz.noark.core.util.StringUtils;
import xyz.noark.core.util.ThreadUtils;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 一种简单的Redis锁实现
 *
 * @author 小流氓[176543888@qq.com]
 */
public class RedisDistributedLock extends DistributedLock {
    /**
     * 解锁LUA脚本
     */
    private static final String SCRIPT_UNLOCK = "" +
            "if redis.call('get', KEYS[1]) == ARGV[1] then\n" +
            "    return redis.call('del', KEYS[1]);\n" +
            "else\n" +
            "    return 0;\n" +
            "end";

    /**
     * 锁定时间：1分钟
     */
    private final int timeout = 60;

    private final RedisTemplate redisTemplate;

    /**
     * 当前锁的唯一ID
     */
    private final String id;
    /**
     * Redis中锁的Key
     */
    private final String lockKey;


    RedisDistributedLock(RedisTemplate redisTemplate, String id) {
        this.redisTemplate = redisTemplate;
        this.id = UUID.randomUUID().toString();
        this.lockKey = StringUtils.join("lock:", id);
    }

    @Override
    public boolean tryLock() {
        final ValueOperations operations = redisTemplate.opsForValue();
        String result = operations.set(lockKey, id, "NX", "EX", timeout);
        return "OK".equals(result);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        boolean result = tryLock();

        // 直接拿到锁了就返回
        if (result) {
            return true;
        }

        int waitingTime = 0;
        // 最大等待时长（秒）
        long max = unit.toSeconds(time);
        // 没有拿到锁就再次获取
        while (max > 0 && max > waitingTime && !result) {
            waitingTime += 100;
            ThreadUtils.sleep(100);
            result = tryLock();
        }
        return result;
    }

    @Override
    public void unlock() {
        redisTemplate.eval(SCRIPT_UNLOCK, Collections.singletonList(lockKey), Collections.singletonList(id));
    }
}