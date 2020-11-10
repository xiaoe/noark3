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

import java.util.Map;

import static xyz.noark.log.LogHelper.logger;

/**
 * 日志管理器.
 * <p>
 * 初始化日志系统与安全停止
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class LogManager {

    /**
     * 日志级别[debug|info|warn|error],默认值为debug
     */
    private static final String LOG_LEVEL = "log.level";

    /**
     * 是否输出到控制台[true|false],默认值为true
     */
    private static final String LOG_CONSOLE = "log.console";

    /**
     * 文件日志存储目录(默认:/data/log/game/1/game.{yyyy-MM-dd-HH}.log)
     */
    private static final String LOG_PATH = "log.path";

    /**
     * 日志功能初始化.
     *
     * @param config 配置参数
     */
    public static void init(Map<String, String> config) {
        // 日志等级
        LogConfigurator.DEFAULT_LEVEL = Level.valueOf(config.getOrDefault(LOG_LEVEL, "debug").toUpperCase());
        // 是否输出到控制台
        LogConfigurator.CONSOLE = Boolean.valueOf(config.getOrDefault(LOG_CONSOLE, "true"));
        // 日志存储路径
        LogConfigurator.LOG_PATH = new LogPath(config.get(LOG_PATH));
    }

    /**
     * 日志功能安全停止.
     */
    public static void shutdown() {
        LogOutputManager.getInstance().shutdown();
    }

    public static Logger getDefaultLogger() {
        return logger;
    }
}