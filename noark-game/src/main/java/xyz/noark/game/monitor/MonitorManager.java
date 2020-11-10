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
package xyz.noark.game.monitor;

import xyz.noark.core.thread.MonitorThreadPool;
import xyz.noark.core.thread.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static xyz.noark.log.LogHelper.logger;

/**
 * 监控服务管理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class MonitorManager implements MonitorThreadPool {
    private static final int POOL_SIZE = 1;
    private static final int SHUTDOWN_MAX_TIME = 5;
    private final ScheduledExecutorService scheduledExecutor;

    public MonitorManager() {
        this.scheduledExecutor = new ScheduledThreadPoolExecutor(POOL_SIZE, new NamedThreadFactory("monitor", false));
    }

    @Override
    public ExecutorService getMonitorService() {
        return scheduledExecutor;
    }

    /**
     * 添加控制服务
     *
     * @param monitorService 控制服务
     */
    public void addMonitorService(AbstractMonitorService monitorService) {
        scheduledExecutor.scheduleWithFixedDelay(monitorService, monitorService.getInitialDelay(), monitorService.getDelay(), monitorService.getUnit());
    }

    /**
     * 添加一个停止方法，用于游戏服务器安全停服
     */
    public void shutdown() {
        logger.info("开始通知监控服务线程池停止服务.");
        scheduledExecutor.shutdown();
        try {
            if (!scheduledExecutor.awaitTermination(SHUTDOWN_MAX_TIME, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
            logger.info("处理监控服务线程池已停止服务");
        } catch (InterruptedException ie) {
            logger.error("停止监控服务线程时发生异常.", ie);
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}