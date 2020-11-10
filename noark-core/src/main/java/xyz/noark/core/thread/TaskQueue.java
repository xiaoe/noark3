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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

/**
 * 任务处理队列.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class TaskQueue {
    /**
     * 队列ID
     */
    private final Serializable id;
    /**
     * 执行线程池
     */
    private final ExecutorService threadPool;
    /**
     * 任务处理队列
     */
    private LinkedList<AsyncQueueTask> queue;

    public TaskQueue(Serializable id, ExecutorService threadPool) {
        this.id = id;
        this.threadPool = threadPool;
        this.queue = new LinkedList<>();
    }

    /**
     * 获取任务队列ID
     *
     * @return 队列ID
     */
    public Serializable getId() {
        return id;
    }

    /**
     * 往任务队列里提交一个任务。
     *
     * @param task 任务
     */
    public void submit(AsyncQueueTask task) {
        synchronized (this) {
            queue.add(task);
            // 只有一个任务，那就是刚刚加的，直接开始执行...
            if (queue.size() == 1) {
                this.exec(task);
            }

            // FIXME，堆积太多给个日志......


        }
    }

    /**
     * 完成一个任务后续处理
     */
    public void complete() {
        synchronized (this) {
            // 移除已经完成的任务。
            queue.removeFirst();

            // 完成一个任务后，如果还有任务，则继续执行。
            if (!queue.isEmpty()) {
                this.exec(queue.getFirst());
            }
        }
    }

    /**
     * 获取执行业务线程池
     *
     * @return 执行业务线程池
     */
    protected ExecutorService getThreadPool() {
        return threadPool;
    }

    /**
     * 执行异步任务.
     * <p>
     * 就是把任务提交到线程池中
     *
     * @param task 异步任务
     */
    protected void exec(AsyncQueueTask task) {
        threadPool.execute(task);
    }
}