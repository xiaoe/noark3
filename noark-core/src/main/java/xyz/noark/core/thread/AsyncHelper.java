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
package xyz.noark.core.thread;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.StaticComponent;
import xyz.noark.core.thread.task.TaskCallback;
import xyz.noark.core.thread.task.TaskQueue;
import xyz.noark.log.MDC;

import java.io.Serializable;

/**
 * 异步任务小助手.
 * <p>
 * 这个类就是优化一些逻辑中异步化流程的小助手，从而简化那些使用事件或异步指令的方案，主要是便于编码.<br>
 * 也可以理解为匿名事件或匿名内部指令
 * </p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@StaticComponent
public class AsyncHelper {
    @Autowired
    private static ThreadDispatcher threadDispatcher;

    /**
     * 私有化构造函数
     */
    private AsyncHelper() {
    }

    /**
     * 异步化一段逻辑.
     * <p>就是在当前队列中执行，可以理解为本线程执行完当前逻辑再去执行那异步逻辑</p>
     *
     * @param callback 异步逻辑
     */
    public static void localCall(TaskCallback callback) {
        call((Serializable) MDC.get(TaskQueue.QUEUE_ID), callback);
    }

    /**
     * 异步化一段逻辑.
     *
     * @param queueId  指定队列ID
     * @param callback 异步逻辑
     */
    public static void call(Serializable queueId, TaskCallback callback) {
        threadDispatcher.dispatchTask(queueId, callback, true);
    }

    /**
     * 异步化一段逻辑.
     * <p>非当前队列中执行，可以理解随便找个线程跑一下这个异步逻辑</p>
     *
     * @param callback 异步逻辑
     */
    public static void randomCall(TaskCallback callback) {
        // 不指定队列ID，随机一个空闲线程
        threadDispatcher.dispatchTask(null, callback, true);
    }
}