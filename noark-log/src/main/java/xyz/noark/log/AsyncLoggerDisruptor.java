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
package xyz.noark.log;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步日志任务分发器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
class AsyncLoggerDisruptor {
    /**
     * 异步记录日志线程池
     */
    private final ScheduledExecutorService scheduledExecutor;

    AsyncLoggerDisruptor() {
        scheduledExecutor = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = new Thread(r, "async-log");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * 发布一个日志事件.
     * <p>就是丢给线程池就OK了，由消费线程去处理存档与显示</p>
     *
     * @param event 日志事件
     */
    public void publish(AsyncLogEvent event) {
        scheduledExecutor.execute(event);
    }

    /**
     * 安全停服
     */
    public void shutdown() {
        scheduledExecutor.shutdown();
        try {
            scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // 最大等待时间为1分钟，还没写完，那就丢了吧...
        }
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(LogOutputFlushTask task, int initialDelay, int delay, TimeUnit unit) {
        return scheduledExecutor.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }
}
