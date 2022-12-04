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
package xyz.noark.core.thread.task;

import xyz.noark.core.exception.ExceptionHelper;
import xyz.noark.core.thread.AsyncHelper;
import xyz.noark.core.thread.ThreadCommand;
import xyz.noark.core.util.DateUtils;
import xyz.noark.core.util.ThreadUtils;

import java.io.Serializable;

/**
 * 异步任务.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class AsyncQueueTask extends AbstractAsyncTask implements Runnable {
    protected final TaskQueue taskQueue;
    private final ThreadCommand command;

    /**
     * 当前执行的线程，用于监控队列超时输出堆栈信息
     */
    private Thread currentThread;


    /**
     * 队列ID
     */
    private final Serializable queueId;

    public AsyncQueueTask(TaskQueue taskQueue, ThreadCommand command) {
        this.queueId = taskQueue.getId();
        this.taskQueue = taskQueue;
        this.command = command;
    }


    @Override
    protected void doSomethingBefore() {
        super.doSomethingBefore();
        // 记录当前执行线程
        this.currentThread = Thread.currentThread();
        // 设计当前任务上下文
        AsyncHelper.setTaskContext(new TaskContext(queueId));
    }

    @Override
    protected void doSomething() {
        command.exec();
    }

    @Override
    protected void doSomethingException(Throwable e) {
        try {
            // 正常被捕获了异常并正确处理了
            if (command.catchExecException(e)) {
                return;
            }

            // 未能正常处理，那还是要走这个默认兜底方案
            this.defaultHandleException(e);
        } catch (Throwable ex) {
            // 处理异常时发生了异常
            this.defaultHandleException(e);

            // 记录异常信息
            super.doSomethingException(ex);
            logger.error("catch exec exception. {}", ex);
        }
    }

    private void defaultHandleException(Throwable e) {
        logger.error("handle {} exception. {}", logCode(), e);
        ExceptionHelper.monitor(e);
    }

    @Override
    protected void doSomethingAfter() {
        // 通知队列完成当前任务，继续后面的逻辑...
        taskQueue.complete();

        // 记录日志等业务
        super.doSomethingAfter();

        AsyncHelper.removeTaskContext();
    }

    @Override
    protected String logCode() {
        return command.code();
    }

    @Override
    protected boolean isPrintLog() {
        return command.isPrintLog();
    }

    /**
     * 记录执行超时信息.
     *
     * @param outputStack 是否输出执行线程当前执行堆栈信息
     */
    public void logExecTimeoutInfo(boolean outputStack) {
        // 延迟时间与执行时间
        float delay = DateUtils.formatNanoTime(startExecTime - createTime);
        float exec = DateUtils.formatNanoTime(System.nanoTime() - startExecTime);
        logger.error("exec timeout {},delay={} ms,exec={} ms", command.code(), delay, exec);

        // 输出当前执行线程执行堆栈信息
        if (outputStack) {
            logger.error(ThreadUtils.printStackTrace(currentThread));
        }
    }
}