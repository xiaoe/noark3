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
 * 输出抽象
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
interface Appender {
    /**
     * 输出日志消息.
     *
     * @param event 一次日志记录
     * @param text  输出内容
     */
    void output(LogEvent event, char[] text);

    /**
     * 控制台Appender
     */
    class ConsoleAppender implements Appender {
        @Override
        public void output(LogEvent event, char[] text) {
            switch (event.getLevel()) {
                case DEBUG:
                case INFO:
                    System.out.print(text);
                    break;
                default:
                    System.err.print(text);
                    break;
            }
        }
    }

    /**
     * 文件Appender
     */
    class FileAppender implements Appender {
        private final LogPath path;
        private final LogDay day;
        private final LogFileWriterManager writerManager;

        public FileAppender(LogPath path, LogDay day) {
            this.path = path;
            this.day = day;
            this.writerManager = LogManager.getWriterManager();
        }

        @Override
        public void output(LogEvent event, char[] text) {
            writerManager.getWriter(path, day).output(event, text);
        }
    }
}