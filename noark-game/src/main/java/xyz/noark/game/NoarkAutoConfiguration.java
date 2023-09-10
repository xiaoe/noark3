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

import xyz.noark.core.Modular;
import xyz.noark.core.ModularManager;
import xyz.noark.core.annotation.Configuration;
import xyz.noark.core.annotation.configuration.Bean;
import xyz.noark.core.event.EventManager;
import xyz.noark.core.thread.ThreadModular;
import xyz.noark.game.event.DefaultEventManager;
import xyz.noark.game.event.EventModular;
import xyz.noark.game.template.ReloadManager;

/**
 * 基础Starter组件入口.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.5
 */
@Configuration
public class NoarkAutoConfiguration {

    /**
     * 模块管理器
     *
     * @return 模块管理器
     */
    @Bean
    public ModularManager modularManager() {
        return new ModularManager();
    }

    /**
     * 线程模块
     *
     * @return 线程模块
     */
    @Bean(name = Modular.THREAD_MODULAR)
    public ThreadModular threadModular() {
        return new ThreadModular();
    }

    /**
     * 事件模块
     *
     * @return 事件模块
     */
    @Bean(name = Modular.EVENT_MODULAR)
    public EventModular eventModular() {
        return new EventModular();
    }

    /**
     * 事件管理器
     *
     * @return 事件管理器
     */
    @Bean
    public EventManager eventManager() {
        return new DefaultEventManager();
    }

    /**
     * 配置重载管理类
     *
     * @return 配置重载管理类
     */
    @Bean
    public ReloadManager reloadManager() {
        return new ReloadManager();
    }
}
