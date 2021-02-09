package xyz.noark.redis;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.lock.DistributedLock;
import xyz.noark.core.lock.DistributedLockManager;

import java.io.Serializable;

/**
 * 使用Redis实现的分布式锁管理器.
 *
 * @author 小流氓[176543888@qq.com]
 */
public class RedisDistributedLockManager implements DistributedLockManager {
    private static final ThreadLocal<DistributedLock> LOCK_CACHE = new ThreadLocal<>();

    @Autowired
    private RedisTemplate redisTemplate;




    @Override
    public DistributedLock getLock(Serializable id) {
        DistributedLock distributedLock = LOCK_CACHE.get();



        return new RedisDistributedLock(redisTemplate, id == null ? "null" : id.toString());
    }
}
