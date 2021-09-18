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

/**
 * 一个包目录的日志配置
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
class LogConfig {
    private boolean levelInitialized = false;
    private Level level = Level.DEBUG;

    private boolean consoleInitialized = false;
    private boolean console = false;

    private boolean pathInitialized = false;
    private LogPath path = null;

    private boolean patternInitialized = false;
    private String layoutPattern = LogConstant.DEFAULT_LAYOUT_PATTERN;

    LogConfig() {
    }

    LogConfig(Level level, boolean console) {
        this.level = level;
        this.console = console;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
        this.levelInitialized = true;
    }

    public boolean isConsole() {
        return console;
    }

    public void setConsole(boolean console) {
        this.console = console;
        this.consoleInitialized = true;
    }

    public LogPath getPath() {
        return path;
    }

    public void setPath(LogPath path) {
        this.path = path;
        this.pathInitialized = true;
    }

    public String getLayoutPattern() {
        return layoutPattern;
    }

    public void setLayoutPattern(String layoutPattern) {
        this.layoutPattern = layoutPattern;
        this.patternInitialized = true;
    }

    /**
     * 有一个没初始化就需要修正
     *
     * @return 如果需要修正返回true
     */
    public boolean ifNeedFix() {
        return !levelInitialized || !consoleInitialized || !pathInitialized || !patternInitialized;
    }

    /**
     * 拿爸爸的配置来修正自己的配置
     *
     * @param parentConfig 爸爸的配置
     */
    public void fix(LogConfig parentConfig) {
        if (!levelInitialized) {
            this.setLevel(parentConfig.getLevel());
        }

        if (!consoleInitialized) {
            this.setConsole(parentConfig.isConsole());
        }

        if (!pathInitialized) {
            this.setPath(parentConfig.getPath());
        }

        if (!patternInitialized) {
            this.setLayoutPattern(parentConfig.getLayoutPattern());
        }
    }
}