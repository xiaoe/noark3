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

import xyz.noark.log.message.Message;
import xyz.noark.log.pattern.PatternFormatter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 一个日志事件，就是一次记录
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public class LogEvent {
    /**
     * 日志发生的时间
     */
    private final LocalDateTime eventTime;
    /**
     * 这行日志的等级
     */
    private final Level level;
    /**
     * 线程名称
     */
    private final String threadName;
    /**
     * 日志内容
     */
    protected final Message message;

    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 当前行号
     */
    private int lineNumber;

    LogEvent(AbstractLogger logger, Level level, Message message) {
        this.eventTime = LocalDateTime.now();
        this.level = level;

        final Thread thread = Thread.currentThread();
        this.threadName = thread.getName();

        this.message = message;

        // 需要记录类名和行号
        if (logger.getPrivateConfig().isIncludeLocation()) {
            this.initStackTraceInfo(thread);
        }
    }

    private void initStackTraceInfo(Thread thread) {
        StackTraceElement[] elements = thread.getStackTrace();
        for (int i = 7; i < elements.length; i++) {
            StackTraceElement stackTraceElement = elements[i];
            fileName = stackTraceElement.getFileName();
            lineNumber = stackTraceElement.getLineNumber();
            if (fileName != null && !fileName.endsWith("Logger.java")) {
                break;
            }
        }
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public Level getLevel() {
        return level;
    }

    public char[] build(List<PatternFormatter> formatterList) {
        StringBuilder sb = new StringBuilder();
        for (PatternFormatter formatter : formatterList) {
            formatter.format(this, sb);
        }

        final char[] result = new char[sb.length()];
        sb.getChars(0, result.length, result, 0);
        return result;
    }

    public String getThreadName() {
        return threadName;
    }

    public Message getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * 获取MDC中存储的Key对应值
     *
     * @param key 指定Key
     * @return 返回Key对应的值
     */
    public Object getMdcValue(String key) {
        // 同步日志直接取值，异步由子类重写
        return MDC.get(key);
    }
}