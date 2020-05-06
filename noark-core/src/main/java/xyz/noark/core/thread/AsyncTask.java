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
package xyz.noark.core.thread;

import xyz.noark.core.network.NetworkListener;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.ResultHelper;
import xyz.noark.core.network.Session;
import xyz.noark.core.util.MathUtils;
import xyz.noark.core.util.ThreadUtils;

import java.io.Serializable;

import static xyz.noark.log.LogHelper.logger;

/**
 * 异步任务.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class AsyncTask implements Runnable {
    /**
     * 任务创建时间
     */
    protected final long createTime = System.nanoTime();
    protected final TaskQueue taskQueue;
    private final ThreadCommand command;
    private final Serializable playerId;
    private final NetworkListener networkListener;

    /**
     * 用于响应请求时
     */
    private final NetworkPacket packet;
    private final Session session;

    private Thread currentThread;
    private long startExecuteTime;

    public AsyncTask(NetworkListener networkListener, TaskQueue taskQueue, ThreadCommand command, Serializable playerId, NetworkPacket packet, Session session) {
        this.taskQueue = taskQueue;
        this.command = command;
        this.playerId = playerId;
        this.session = session;
        this.packet = packet;
        this.networkListener = networkListener;
    }

    @Override
    public void run() {
        // 开始执行的时间
        this.startExecuteTime = System.nanoTime();
        this.currentThread = Thread.currentThread();
        try {
            // 开始处理协议，并发送结果
            ResultHelper.trySendResult(session, packet, command.exec());
        } catch (Throwable e) {
            // 记录异常信息
            if (playerId == null) {
                logger.error("handle {} exception.{}", command.code(), e);
            } else {
                logger.error("handle {} exception. playerId={}{}", command.code(), playerId, e);
            }
            // 额外处理逻辑
            if (networkListener != null) {
                networkListener.handleException(session, packet, e);
            }
        } finally {
            taskQueue.complete();// 后继逻辑...
            // 执行之后
            this.execCommandAfter(startExecuteTime);
        }
    }

    /**
     * 执行之后做一个逻辑.
     *
     * @param startExecuteTime 开始执行时间
     */
    private void execCommandAfter(long startExecuteTime) {
        if (command.isPrintLog()) {
            // 执行结束的时间
            long endExecuteTime = System.nanoTime();
            if (playerId == null) {
                logger.info("handle {},delay={} ms,exec={} ms", command.code(), formatScale(startExecuteTime - createTime), formatScale(endExecuteTime - startExecuteTime));
            } else {
                logger.info("handle {},delay={} ms,exec={} ms playerId={}", command.code(), formatScale(startExecuteTime - createTime), formatScale(endExecuteTime - startExecuteTime), playerId);
            }
        }
    }

    /**
     * 记录执行超时信息.
     *
     * @param outputStack 是否输出执行线程当前执行堆栈信息
     */
    public void logExecTimeoutInfo(boolean outputStack) {
        final long now = System.nanoTime();
        if (playerId == null) {
            logger.error("exec timeout {},delay={} ms,exec={} ms", command.code(), formatScale(startExecuteTime - createTime), formatScale(now - startExecuteTime));
        } else {
            logger.error("exec timeout {},delay={} ms,exec={} ms playerId={}", command.code(), formatScale(startExecuteTime - createTime), formatScale(now - startExecuteTime), playerId);
        }

        // 输出当前执行线程执行堆栈信息
        if (outputStack) {
            logger.error(ThreadUtils.printStackTrace(currentThread));
        }
    }

    /**
     * 把纳秒转化为毫秒显示（保留小数点后面两位）
     *
     * @param nanoTime 纳秒
     * @return 毫秒
     */
    private float formatScale(long nanoTime) {
        // 除100W，然后格式化
        return MathUtils.formatScale(nanoTime / 100_0000F, 2);
    }
}