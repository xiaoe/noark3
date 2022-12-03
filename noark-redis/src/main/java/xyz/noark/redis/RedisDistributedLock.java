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
    private final long timeout = 60;

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
        String result = operations.set(lockKey, id, SetParams.setParams().nx().ex(timeout));
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