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
package xyz.noark.game;

import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.thread.TraceIdFactory;
import xyz.noark.core.util.ClassUtils;
import xyz.noark.game.bootstrap.ServerBootstrap;
import xyz.noark.log.LogManager;

import java.util.Map;

/**
 * Noark系统初始化.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
class NoarkInitializer {

    /**
     * 初始化Noark.
     *
     * @param klass 启动引导类
     * @param args  Main方法的启动参数
     */
    public void init(Class<? extends ServerBootstrap> klass, String... args) {
        // 为启动服务器的主线程也绑定一个traceId
        TraceIdFactory.initFixedTraceIdByStartServer();

        // 载入配置
        EnvConfigHolder.setProperties(this.loadProperties(args));

        // 初始化日志系统
        LogManager.init(EnvConfigHolder.getProperties());

        // 初始化启动引导程序，参考ServerBootstrap的具体实现
        ClassUtils.newInstance(klass).start();
    }

    /**
     * 根据启动参数分析加载相应的配置文件
     */
    private Map<String, String> loadProperties(String... args) {
        // 载入配置文件...
        NoarkPropertiesLoader loader = new NoarkPropertiesLoader();
        // 加载命令行参数
        loader.loadingArgs(args);
        // 加载配置文件参数
        loader.loadingProperties();
        // 加载配置中心参数
        loader.loadingConfigCentre();
        // 最终的配置
        return loader.getProperties();
    }
}