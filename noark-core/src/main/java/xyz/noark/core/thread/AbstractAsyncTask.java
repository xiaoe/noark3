package xyz.noark.core.thread;

import xyz.noark.core.exception.ExceptionHelper;
import xyz.noark.core.util.DateUtils;

import java.io.Serializable;

import static xyz.noark.log.LogHelper.logger;

/**
 * 抽象的异步任务.
 *
 * @author 小流氓[176543888@qq.com]
 */
public abstract class AbstractAsyncTask implements Runnable {
    /**
     * 任务创建时间
     */
    protected final long createTime;
    /**
     * 开始执行的时间.
     */
    protected long startExecuteTime;

    /**
     * 队列ID
     */
    private final Serializable queueId;
    /**
     * 玩家ID
     */
    protected final Serializable playerId;

    public AbstractAsyncTask(Serializable queueId, Serializable playerId) {
        this.createTime = System.nanoTime();
        this.queueId = queueId;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        // 开始执行的时间
        this.startExecuteTime = System.nanoTime();
        this.execCommandBefore();
        try {
            this.doSomething();
        } catch (Throwable e) {
            this.execCommandException(e);
        } finally {
            this.execCommandAfter(startExecuteTime);
        }
    }

    /**
     * 执行之前做一个逻辑.
     */
    protected void execCommandBefore() {
        AsyncHelper.setTaskContext(new TaskContext(queueId, playerId));
    }

    /**
     * 异步任务做具体的事
     */
    protected abstract void doSomething();

    /**
     * 执行任务中发生了异常情况.
     *
     * @param e 异常信息
     */
    protected void execCommandException(Throwable e) {
        // 记录异常信息
        if (playerId == null) {
            logger.error("handle {} exception.{}", logCode(), e);
        } else {
            logger.error("handle {} exception. playerId={}{}", logCode(), playerId, e);
        }
        ExceptionHelper.monitor(e);
    }


    /**
     * 执行之后做一个逻辑.
     *
     * @param startExecuteTime 开始执行时间
     */
    protected void execCommandAfter(long startExecuteTime) {
        AsyncHelper.removeTaskContext();

        if (this.isPrintLog()) {
            float delay = DateUtils.formatNanoTime(startExecuteTime - createTime);
            float exec = DateUtils.formatNanoTime(System.nanoTime() - startExecuteTime);
            if (playerId == null) {
                logger.info("handle {},delay={} ms,exec={} ms", logCode(), delay, exec);
            } else {
                logger.info("handle {},delay={} ms,exec={} ms playerId={}", logCode(), delay, exec, playerId);
            }
        }
    }


    /**
     * 记录日志这个异步任务的编号.
     *
     * @return 异步任务的编号
     */
    protected abstract String logCode();

    /**
     * 是否记录相关执行日志
     *
     * @return 是否记录相关执行日志
     */
    protected abstract boolean isPrintLog();
}
