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
import java.util.Map.Entry;
import java.util.TreeMap;

import static xyz.noark.log.LogConstant.LOG_CONSOLE;
import static xyz.noark.log.LogConstant.LOG_LEVEL;

/**
 * 日志配置.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
class LogConfigurator {
    /**
     * 如果连配置都没有，那就用这个默认的配置吧...
     */
    private static final LogConfig DEFAULT_CONFIG = new LogConfig(Level.DEBUG, true);
    /**
     * 缓存所有配置
     */
    private final Map<String, LogConfig> configMap = new TreeMap<>();

    LogConfigurator(Map<String, String> config) {
        // 初始化配置信息
        this.initConfig(config);
        // 修复层级传递关系
        this.fixConfigHierarchy();
    }

    /**
     * 根据日志名来取一个日志配置
     *
     * @param name 日志名
     * @return 日志配置
     */
    LogConfig getConfig(String name) {
        if (configMap.isEmpty()) {
            return DEFAULT_CONFIG;
        }
        LogConfig config = configMap.get(name);
        return config == null ? getParentConfig(name) : config;
    }

    private LogConfig getParentConfig(String name) {
        LogConfig config;
        do {
            int index = name.lastIndexOf(".");
            // 没有点了，那就使用根配置
            if (index == -1) {
                name = LogConstant.DEFAULT_LOGGER_NAME;
            }
            // 向上一级查询配置
            else {
                name = name.substring(0, index);
            }
            config = configMap.get(name);
        } while (config == null);
        return config;
    }

    /**
     * 修复层级传递关系
     */
    private void fixConfigHierarchy() {
        for (Entry<String, LogConfig> e : configMap.entrySet()) {
            // 根配置，忽略修正
            if (LogConstant.DEFAULT_LOGGER_NAME.equals(e.getKey())) {
                continue;
            }

            LogConfig config = e.getValue();
            // 确认需要修正，那就查找当前配置的爸爸配置
            if (config.ifNeedFix()) {
                config.fix(getParentConfig(e.getKey()));
            }
        }
    }

    /**
     * 初始化配置
     *
     * @param config 配置
     */
    private void initConfig(Map<String, String> config) {
        for (Entry<String, String> e : config.entrySet()) {
            String configKey = e.getKey();
            // 日志等级
            if (configKey.startsWith(LOG_LEVEL)) {
                this.handleLogLevelConfig(configKey, e.getValue());
            }
            // 控制台输出
            else if (configKey.startsWith(LOG_CONSOLE)) {
                this.handleLogConsoleConfig(configKey, e.getValue());
            }
            // 日志保留时间
            else if (configKey.startsWith(LogConstant.LOG_DELETE_TIME)) {
                this.handleLogPathDeleteTimeConfig(configKey, e.getValue());
            }
            // 输出文件配置
            else if (configKey.startsWith(LogConstant.LOG_PATH)) {
                this.handleLogPathConfig(configKey, e.getValue());
            }
            // 布局样式
            else if (configKey.startsWith(LogConstant.LOG_LAYOUT_PATTERN)) {
                this.handleLogLayoutPatternConfig(configKey, e.getValue());
            }
            // 不是日志相关的配置就忽略了
        }
    }

    private void handleLogPathDeleteTimeConfig(String configKey, String value) {
        String key = this.judgeLoggerConfigKey(configKey, LogConstant.LOG_DELETE_TIME);
        final LogDay logDay = LogDay.parse(value);
        configMap.computeIfAbsent(key, k -> new LogConfig()).setDay(logDay);
    }

    /**
     * 处理日志显示样式配置
     *
     * @param configKey 配置Key
     * @param value     配置值
     */
    private void handleLogLayoutPatternConfig(String configKey, String value) {
        String key = this.judgeLoggerConfigKey(configKey, LogConstant.LOG_LAYOUT_PATTERN);
        configMap.computeIfAbsent(key, k -> new LogConfig()).setLayoutPattern(value);
    }

    /**
     * 处理日志存档路径配置
     *
     * @param configKey 配置Key
     * @param value     配置值
     */
    private void handleLogPathConfig(String configKey, String value) {
        String key = this.judgeLoggerConfigKey(configKey, LogConstant.LOG_PATH);
        final LogPath path = new LogPath(value);
        configMap.computeIfAbsent(key, k -> new LogConfig()).setPath(path);
    }

    /**
     * 处理日志是否输出控制台配置
     *
     * @param configKey 配置Key
     * @param value     配置值
     */
    private void handleLogConsoleConfig(String configKey, String value) {
        String key = this.judgeLoggerConfigKey(configKey, LogConstant.LOG_CONSOLE);
        final boolean console = Boolean.parseBoolean(value);
        configMap.computeIfAbsent(key, k -> new LogConfig()).setConsole(console);
    }

    /**
     * 处理日志等级配置
     *
     * @param configKey 配置Key
     * @param value     配置值
     */
    private void handleLogLevelConfig(String configKey, String value) {
        String key = this.judgeLoggerConfigKey(configKey, LogConstant.LOG_LEVEL);
        final Level level = Level.valueOf(value.toUpperCase());
        configMap.computeIfAbsent(key, k -> new LogConfig()).setLevel(level);
    }

    private String judgeLoggerConfigKey(String configKey, String configPrefix) {
        String key = LogConstant.DEFAULT_LOGGER_NAME;
        int length = configPrefix.length();
        if (configKey.length() > length) {
            key = configKey.substring(length + 1);
        }
        return key;
    }
}