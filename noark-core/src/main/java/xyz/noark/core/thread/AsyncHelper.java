/*
 * Copyright © 2020 www.noark.xyz All Rights Reserved.
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

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.StaticComponent;

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
    /**
     * 构建一个ThreadLocal来存放执行期的任务上下文
     */
    private static final ThreadLocal<TaskContext> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置任务执行的上下文
     *
     * @param taskContext 任务执行的上下文
     */
    static void setTaskContext(TaskContext taskContext) {
        THREAD_LOCAL.set(taskContext);
    }

    /**
     * 移除任务执行的上下文
     */
    static void removeTaskContext() {
        THREAD_LOCAL.remove();
    }

    @Autowired
    private static ThreadDispatcher threadDispatcher;

    /**
     * 异步化一段逻辑.
     * <p>没有指定队列ID，就是在当前队列中执行</p>
     *
     * @param callback 异步逻辑
     */
    public static void call(TaskCallback callback) {
        TaskContext context = THREAD_LOCAL.get();
        call(context.getQueueId(), callback, context.getPlayerId());
    }

    /**
     * 异步化一段逻辑.
     *
     * @param queueId  指定队列ID
     * @param callback 异步逻辑
     */
    public static void call(Serializable queueId, TaskCallback callback) {
        TaskContext context = THREAD_LOCAL.get();
        call(queueId, callback, context.getPlayerId());
    }

    private static void call(Serializable queueId, TaskCallback callback, Serializable playerId) {
        threadDispatcher.dispatchAsyncCallback(queueId, callback, playerId);
    }
}
