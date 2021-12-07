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
package xyz.noark.network;

import xyz.noark.core.Modular;
import xyz.noark.core.annotation.Configuration;
import xyz.noark.core.annotation.configuration.Bean;
import xyz.noark.network.handler.SocketServerHandler;
import xyz.noark.network.handler.WebsocketServerHandler;
import xyz.noark.network.http.DispatcherServlet;
import xyz.noark.network.http.HandleInterceptChain;
import xyz.noark.network.http.HttpModular;
import xyz.noark.network.http.HttpServer;
import xyz.noark.network.init.PolicyFileHandler;
import xyz.noark.network.init.SocketInitializeHandler;
import xyz.noark.network.init.WebsocketInitializeHandler;

/**
 * 网络Starter组件入口.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.5
 */
@Configuration
public class NetworkAutoConfiguration {
    /**
     * 基于Netty实现的一个网络服务.
     *
     * @return 网络服务
     */
    @Bean(name = "NettyServer")
    public NettyServer nettyServer() {
        return new NettyServer();
    }

    /**
     * Netty实现的服务协议处理类
     *
     * @return 协议处理类
     */
    @Bean
    public NettyServerHandler nettyServerHandler() {
        return new NettyServerHandler();
    }

    @Bean
    public InitializeHandlerManager initializeHandlerManager() {
        return new InitializeHandlerManager();
    }

    @Bean(name = PolicyFileHandler.POLICY_FILE_NAME)
    public PolicyFileHandler policyFileHandler() {
        return new PolicyFileHandler();
    }

    @Bean(name = SocketInitializeHandler.SOCKET_NAME)
    public SocketInitializeHandler socketInitializeHandler() {
        return new SocketInitializeHandler();
    }

    @Bean(name = WebsocketInitializeHandler.WEBSOCKET_NAME)
    public WebsocketInitializeHandler websocketInitializeHandler() {
        return new WebsocketInitializeHandler();
    }


    @Bean
    public SocketServerHandler socketServerHandler() {
        return new SocketServerHandler();
    }

    @Bean
    public WebsocketServerHandler websocketServerHandler() {
        return new WebsocketServerHandler();
    }


    @Bean(name = Modular.HTTP_MODULAR)
    public HttpModular httpModular() {
        return new HttpModular();
    }

    @Bean
    public HttpServer httpServer() {
        return new HttpServer();
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean
    public HandleInterceptChain handleInterceptChain() {
        return new HandleInterceptChain();
    }
}
