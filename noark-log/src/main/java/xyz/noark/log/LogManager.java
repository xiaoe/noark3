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

import java.util.Collections;
import java.util.Map;

/**
 * 日志管理器.
 * <p>
 * 初始化日志系统与安全停止
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class LogManager {
    private static final LogManager INSTANCE = new LogManager();
    /**
     * 所有Logger的注册表
     */
    private final LoggerRegistry loggerRegistry;
    /**
     * 日志输出管理器
     */
    private final LogFileWriterManager writerManager;
    /**
     * 异步日志调度器
     */
    private final AsyncLoggerDisruptor asyncLoggerDisruptor;

    /**
     * 当前配置
     */
    private LogConfigurator configurator;

    /**
     * 私有化构建函数
     */
    private LogManager() {
        this.loggerRegistry = new LoggerRegistry();
        this.writerManager = new LogFileWriterManager();
        this.asyncLoggerDisruptor = new AsyncLoggerDisruptor();
    }

    /**
     * 日志功能初始化.
     *
     * @param config 配置参数
     */
    public static void init(Map<String, String> config) {
        INSTANCE.reconfigureAndUpdateAll(config);
    }

    /**
     * 重新配置，然后还要更新所有Logger对象
     *
     * @param config 配置
     */
    public static void reconfigure(Map<String, String> config) {
        INSTANCE.reconfigureAndUpdateAll(config);
    }

    private void reconfigureAndUpdateAll(Map<String, String> config) {
        this.configurator = new LogConfigurator(config);
        synchronized (INSTANCE) {
            loggerRegistry.updateLoggers(configurator);
        }
    }

    /**
     * 日志功能安全停止.
     */
    public static void shutdown() {
        getAsyncLoggerDisruptor().shutdown();
        getWriterManager().shutdown();
    }

    /**
     * 获取当前的配置
     *
     * @return 当前的配置
     */
    static LogConfigurator getConfigurator() {
        // 如果没有初始化
        if (INSTANCE.configurator == null) {
            INSTANCE.reconfigureAndUpdateAll(Collections.emptyMap());
        }
        return INSTANCE.configurator;
    }

    /**
     * 获取所有Logger注册表
     *
     * @return Logger注册表
     */
    static LoggerRegistry getLoggerRegistry() {
        return INSTANCE.loggerRegistry;
    }

    /**
     * 获取异步日志调度器
     *
     * @return 日志调度器
     */
    static AsyncLoggerDisruptor getAsyncLoggerDisruptor() {
        return INSTANCE.asyncLoggerDisruptor;
    }

    /**
     * 获取日志输出管理器
     *
     * @return 日志输出管理器
     */
    static LogFileWriterManager getWriterManager() {
        return INSTANCE.writerManager;
    }
}