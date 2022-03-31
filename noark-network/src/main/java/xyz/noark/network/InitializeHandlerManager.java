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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.network.init.IllegalRequestHandler;
import xyz.noark.network.init.SocketInitializeHandler;

import java.util.Map;

/**
 * 第一个请求管理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class InitializeHandlerManager {
    /**
     * Socket的接头暗号是否开启，默认是开启状态
     */
    @Value(NetworkConstant.SOCKET_SIGNAL_ACTIVE)
    private boolean signalActive = true;

    @Autowired
    private Map<String, InitializeHandler> handlers;
    @Autowired
    private SocketInitializeHandler socketInitializeHandler;

    /**
     * 初始化通道.
     *
     * @param ctx      通道上下文
     * @param protocol 暗号协议
     * @param in       封包缓冲区
     */
    public void init(ChannelHandlerContext ctx, String protocol, ByteBuf in) {
        InitializeHandler initializeHandler = socketInitializeHandler;
        // 开启暗号
        if (signalActive) {
            initializeHandler = handlers.getOrDefault(protocol, new IllegalRequestHandler(protocol));
        }
        // 关闭暗号，前面用来判定的协议头要还回去.
        else {
            in.resetReaderIndex();
        }
        // 初始化处理器
        initializeHandler.handle(ctx);
    }
}