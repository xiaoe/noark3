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
 * 提供一套输出日志的接口.
 * <p>
 * 使用参考{@link LogHelper}<br>
 * 当调用记录方法时，非基本数据类型会被提前转化为String文本<br>
 * 如果目标对象被标识为{@code ThreadSafe}则不会被提前转化，用于一些特别的优化情况
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public interface Logger {

    /**
     * 输出一条Debug级别的日志.
     * <p>
     * 记录调试相关内容，用于追踪执行流程与测试打印相关日志.
     *
     * @param msg 文本
     */
    void debug(String msg);

    /**
     * 输出一条Debug级别的日志.
     * <p>
     * 记录调试相关内容，用于追踪执行流程与测试打印相关日志.
     *
     * @param msg  文本
     * @param args 参数
     */
    void debug(String msg, Object... args);

    /**
     * 输出一条Info级别的日志.
     * <p>
     * 记录行为结果内容，用于查证一些行为或结果相关日志.
     *
     * @param msg 文本
     */
    void info(String msg);

    /**
     * 输出一条Info级别的日志.
     * <p>
     * 记录行为结果内容，用于查证一些行为或结果相关日志.
     *
     * @param msg  文本
     * @param args 参数
     */
    void info(String msg, Object... args);

    /**
     * 输出一条Warn级别的日志.
     * <p>
     * 记录警告异常内容，此类区别于错误日志，有影响但不要命.
     *
     * @param msg 文本
     */
    void warn(String msg);

    /**
     * 输出一条Warn级别的日志.
     * <p>
     * 记录警告异常内容，此类区别于错误日志，有影响但不要命.
     *
     * @param msg  文本
     * @param args 参数
     */
    void warn(String msg, Object... args);

    /**
     * 输出一条Error级别的日志.
     * <p>
     * 记录错误内容，此类必需第一时间解决掉.
     *
     * @param msg 文本
     */
    void error(String msg);

    /**
     * 输出一条Error级别的日志.
     * <p>
     * 记录错误内容，此类必需第一时间解决掉.
     *
     * @param msg  文本
     * @param args 参数
     */
    void error(String msg, Object... args);
}