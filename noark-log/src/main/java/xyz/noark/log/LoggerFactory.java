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
 * 日志工厂类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public class LoggerFactory {

    /**
     * 获取一个指定类的日志.
     * <p>private static final Logger logger = LoggerFactory.getLogger(XxxService.class);</p>
     * <p>由于日志的初始化配置依赖框架，所以不能在Main方法那个类里申明静态的Logger对象</p>
     * <p>建议：日志使用在框架初始化后面执行</p>
     *
     * @param klass 指定类
     * @return 日志对象
     */
    public static Logger getLogger(Class<?> klass) {
        return LogManager.getLoggerRegistry().getLogger(klass);
    }

    /**
     * 获取一个指定类的日志
     * <p>由于日志的初始化配置依赖框架，所以不能在Main方法那个类里申明静态的Logger对象</p>
     * <p>建议：日志使用在框架初始化后面执行</p>
     *
     * @param name 指定类
     * @return 日志对象
     */
    public static Logger getLogger(String name) {
        return LogManager.getLoggerRegistry().getLogger(name);
    }
}