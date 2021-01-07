package xyz.noark.core.lock;

import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.util.ThreadUtils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁.
 *
 * @author 小流氓[176543888@qq.com]
 */
public abstract class DistributedLock implements Lock, AutoCloseable {

    @Override
    public void lock() {
        // 尝试获取锁，拿不到就等着...
        while (!tryLock()) {
            ThreadUtils.sleep(100);
        }
    }

    @Override
    public void close() {
        this.unlock();
    }

    @Override
    public void lockInterruptibly() {
        throw new UnrealizedException("暂不实现的方案，请勿使用此方法获取锁.");
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
