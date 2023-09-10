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
package xyz.noark.game.event;

import xyz.noark.core.event.DelayEvent;
import xyz.noark.core.thread.TraceIdFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import static xyz.noark.log.LogHelper.logger;

/**
 * 延迟事件处理线程.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
class DelayEventThread extends Thread {
    private static final int SHUTDOWN_TIMEOUT = 1;
    static final DelayQueue<DelayEvent> QUEUE = new DelayQueue<>();
    private final DefaultEventManager eventManager;
    private volatile boolean starting = true;

    public DelayEventThread(DefaultEventManager eventManager) {
        super("delay-event");
        this.eventManager = eventManager;
    }

    @Override
    public void run() {
        TraceIdFactory.initFixedTraceIdBySchedulingThread();
        logger.info("延迟任务调度线程开始啦...");
        while (starting) {
            try {
                DelayEvent event = QUEUE.take();
                // 停止事件...
                if (event instanceof ShutdownEvent) {
                    logger.info("延迟任务调度线程停止啦...");
                    this.starting = false;
                    ((ShutdownEvent) event).countDown();
                }

                // 延迟任务...
                else if (event instanceof ScheduledEvent) {
                    eventManager.notifyScheduledHandler((ScheduledEvent) event);
                }

                // 定时任务
                else if (event instanceof FixedTimeEventWrapper) {
                    eventManager.notifyFixedTimeEventHandler((FixedTimeEventWrapper) event);
                }

                // 延迟事件
                else {
                    eventManager.notifyListeners(event.getTraceId(), event);
                }
            } catch (Throwable e) {
                logger.error("调度线程异常", e);
            }
        }
    }

    public boolean addDelayEvent(DelayEvent event) {
        return QUEUE.add(event);
    }

    public boolean remove(DelayEvent event) {
        return QUEUE.remove(event);
    }

    public boolean removeAll(DelayEvent event) {
        return QUEUE.removeIf(v -> v.equals(event));
    }

    /**
     * 停止执行延迟事件.
     */
    public void shutdown() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.addDelayEvent(new ShutdownEvent(countDownLatch));
        try {
            countDownLatch.await(SHUTDOWN_TIMEOUT, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.warn("shutdown event exec exception. {}", e);
        }
    }
}