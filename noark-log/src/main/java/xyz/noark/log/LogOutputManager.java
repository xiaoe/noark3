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

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 日志输出管理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.6
 */
class LogOutputManager {
    private static final LogOutputManager INSTANCE = new LogOutputManager();
    /**
     * 异步记录日志线程池
     */
    private final ScheduledExecutorService scheduledExecutor;
    /**
     * 日志文件写入器
     */
    private LogFileWriter fileWriter = null;
    /**
     * 文件记录最后写入的时间（小时），用于切换输出日志文件
     */
    private int lastWriterHour = -1;

    private LogOutputManager() {
        this.scheduledExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "async-log");
                t.setDaemon(true);
                return t;
            }
        });
    }

    /**
     * 获取日志输出管理器
     *
     * @return 日志输出管理器
     */
    public static LogOutputManager getInstance() {
        return INSTANCE;
    }

    /**
     * 异步记录日志.
     *
     * @param message 日志
     */
    public void asyncLog(Message message) {
        // 日志交给异步线程池写入文件...
        scheduledExecutor.execute(new LogOutputTask(message, this));
    }

    /**
     * 日志输出管理器停止工作
     */
    public void shutdown() {
        scheduledExecutor.shutdown();

        try {// 最大等待时间为1分钟...
            scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            this.flushAndClose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录到控制台.
     *
     * @param level 日志等级
     * @param text  日志文本
     */
    public void recordToConsole(Level level, char[] text) {
        switch (level) {
            case DEBUG:
            case INFO:
                System.out.print(text);
                break;
            default:
                System.err.print(text);
                break;
        }
    }

    /**
     * 记录到文件.
     *
     * @param logTime 日志时间
     * @param message 日志文本
     * @throws IOException 记录时可能会抛出IO异常
     */
    public void recordToFile(LocalDateTime logTime, char[] text) throws IOException {
        this.checkRollover(logTime);
        fileWriter.writer(text);
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
        final File file = this.createNewFile(LogConfigurator.LOG_PATH.getPath(logTime));
        this.fileWriter = new LogFileWriter(file, scheduledExecutor);
        this.lastWriterHour = logTime.getHour();
    }

    private void flushAndClose() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
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