/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
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
package xyz.noark.log;

/**
 * 抽象的日志记录器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
abstract class AbstractLogger {
    /**
     * 日志输出管理器
     */
    private static final LogOutputManager OUTPUT_MANAGER = LogOutputManager.getInstance();

    /**
     * 记录日志，如果级别达标的话.
     *
     * @param level 日志级别
     * @param msg   日志文本
     * @param args  日志参数
     */
    protected void logIfEnabled(Level level, String msg, Object... args) {
        if (isEnabled(level)) {
            logMessage(level, msg, args);
        }
    }

    /**
     * 判定日志级别是否达标
     *
     * @param level 日志级别
     * @return 如果达标则返回true
     */
    private boolean isEnabled(Level level) {
        return LogConfigurator.DEFAULT_LEVEL.getValue() <= level.getValue();
    }

    /**
     * 记录日志.
     *
     * @param level 日志等级
     * @param msg   日志信息
     * @param args  日志参数
     */
    private void logMessage(Level level, String msg, Object... args) {
        OUTPUT_MANAGER.asyncLog(MessageFactory.create(level, msg, args));
    }
}