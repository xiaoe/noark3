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
package xyz.noark.game.bootstrap;

import xyz.noark.core.Modular;
import xyz.noark.core.annotation.orm.DataCheckAndInit;
import xyz.noark.core.network.PacketCodec;
import xyz.noark.game.template.ReloadManager;
import xyz.noark.network.NettyServer;
import xyz.noark.network.codec.json.SimpleJsonCodec;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * 一个默认的服务器启动引导类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public abstract class BaseServerBootstrap extends AbstractServerBootstrap {
    protected NettyServer nettyServer;
    protected Modular dataModular;
    protected Modular eventModular;
    protected Modular httpModular;
    protected Modular threadModular;

    @Override
    protected void onStart() {
        // 0、线程模型
        this.initThreadModular();

        // 1、DB检测与缓存初始化
        this.initDataModular();

        // 2、重载所有策划模板数据.
        ioc.get(ReloadManager.class).reload(true);

        // 3、初始化方法...
        ioc.invokeCustomAnnotationMethod(PostConstruct.class);

        // 4、延迟事件动起来.
        this.initEventModular();

        // 5、HTTP服务
        this.initHttpModular();

        // 6、对外网络...
        this.initNetworkModular();
    }

    /**
     * 初始化网络模块
     */
    protected void initNetworkModular() {
        nettyServer = ioc.get(NettyServer.class);
        nettyServer.startup();
    }

    /**
     * 初始化HTTP服务模块
     */
    protected void initHttpModular() {
        Optional<Modular> modular = modularManager.getModular(Modular.HTTP_MODULAR);
        modular.ifPresent(Modular::init);
        httpModular = modular.orElse(null);
    }

    /**
     * 初始化事件模块
     */
    protected void initEventModular() {
        Optional<Modular> modular = modularManager.getModular(Modular.EVENT_MODULAR);
        modular.ifPresent(Modular::init);
        eventModular = modular.orElse(null);
    }

    /**
     * 初始化数据模块
     */
    protected void initDataModular() {
        Optional<Modular> modular = modularManager.getModular(Modular.DATA_MODULAR);
        modular.ifPresent(dataModular -> {
            this.dataModular = dataModular;
            dataModular.init();
            ioc.invokeCustomAnnotationMethod(DataCheckAndInit.class);
        });
    }

    /**
     * 初始化线程模块
     */
    protected void initThreadModular() {
        Optional<Modular> modular = modularManager.getModular(Modular.THREAD_MODULAR);
        modular.ifPresent(Modular::init);
        threadModular = modular.orElse(null);
    }

    @Override
    protected PacketCodec getPacketCodec() {
        return new SimpleJsonCodec();
    }

    @Override
    protected void onStop() {
        // 停止对外网络
        Optional.ofNullable(nettyServer).ifPresent(NettyServer::shutdown);

        // 停止HTTP服务
        this.onStopHttpModular();

        // 停止延迟任务调度
        this.onStopEventModular();

        // 等待所有任务处理完
        this.onStopThreadModular();

        // 保存数据
        this.onStopDataModular();
    }

    protected void onStopHttpModular() {
        Optional.ofNullable(httpModular).ifPresent(Modular::destroy);
    }

    protected void onStopEventModular() {
        Optional.ofNullable(eventModular).ifPresent(Modular::destroy);
    }

    /**
     * 停止线程模块.
     * <p>可由子类重写来实现一些特别的情况</p>
     */
    protected void onStopThreadModular() {
        Optional.ofNullable(threadModular).ifPresent(Modular::destroy);
    }

    protected void onStopDataModular() {
        Optional.ofNullable(dataModular).ifPresent(Modular::destroy);
    }
}