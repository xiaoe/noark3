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

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 一个路径对应的输出类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public class LogFileWriter {
    /**
     * 配置的存档路径
     */
    private final LogPath path;
    /**
     * 当前正在记录的文件引用
     */
    private LogFile logFile = null;
    /**
     * 文件记录最后写入的时间（小时），用于切换输出日志文件
     */
    private int lastWriterHour = -1;

    public LogFileWriter(LogPath path) {
        this.path = path;
    }

    /**
     * 输出到文件.
     *
     * @param event 日志消息
     * @param text  输出内容
     */
    public void output(LogEvent event, char[] text) {
        try {
            this.recordToFile(event.getEventTime(), text);
        } catch (Throwable e) {
            // 日志输出到终端时有问题，那就丢掉吧...
            e.printStackTrace();
        }
    }

    /**
     * 记录到文件.
     *
     * @param logTime 日志时间
     * @param text    日志文本
     * @throws IOException 记录时可能会抛出IO异常
     */
    public void recordToFile(LocalDateTime logTime, char[] text) throws IOException {
        this.checkRollover(logTime);
        logFile.writer(text);
    }

    private void checkRollover(final LocalDateTime logTime) throws IOException {
        // 不是同一时间，就要切换输出目标
        if (logTime.getHour() != lastWriterHour) {
            rollover(logTime);
        }
    }

    private void rollover(LocalDateTime logTime) throws IOException {
        // 关闭上一小时的日志文件
        this.flushAndClose();

        // 创建下一小时的日志文件
        final File file = this.createNewFile(path.getPath(logTime));
        this.logFile = new LogFile(file);
        this.lastWriterHour = logTime.getHour();
    }

    void flushAndClose() throws IOException {
        if (logFile != null) {
            logFile.close();
        }
    }

    /**
     * 创建新的日志文件.
     * <p>
     * 如果文件已存在，直接返回那个文件，不存在才会创建，目录不存在也会自动创建
     *
     * @param path 文件路径
     * @return 日志文件
     * @throws IOException If an I/O error occurred
     */
    private File createNewFile(String path) throws IOException {
        File file = new File(path);
        // 文件已存在，直接返回这个文件
        if (file.exists()) {
            return file;
        }
        // 目录不存在直接创建目录
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        // 创建文件
        file.createNewFile();
        return file;
    }
}
