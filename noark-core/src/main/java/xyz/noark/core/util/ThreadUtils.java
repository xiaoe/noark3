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
package xyz.noark.core.util;

/**
 * 线程工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2.5
 */
public class ThreadUtils {
    /**
     * 暂停执行.
     * <p>
     * 就是JDK的{@link Thread#sleep(long)}包装一下就不用管这个异常了.<br>
     * 这个方法只用于写一些测试用例时使用...
     *
     * @param millis 暂停毫秒数
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出当前线程正在运行的堆栈信息.
     *
     * @param thread 当前线程
     * @return 当前线程正在运行的堆栈信息
     */
    public static String printStackTrace(Thread thread) {
        final StackTraceElement[] st = thread.getStackTrace();
        StringBuilder sb = new StringBuilder(1024);
        sb.append("\n");
        for (StackTraceElement e : st) {
            sb.append("\tat ").append(e).append("\n");
        }
        return sb.toString();
    }
}