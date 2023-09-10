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
import xyz.noark.network.init.WebsocketInitializeHandler;
import xyz.noark.network.init.WebsocketSslInitializeHandler;
import xyz.noark.network.util.ByteBufUtils;

import java.util.Map;

/**
 * 第一个请求管理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class InitializeHandlerManager {
    /**
     * 默认暗号长度为23，为什么是23呢？你来问我啊，不问我就当你是知道的
     */
    private static final int MAX_LENGTH = 23;
    /**
     * WebSocket握手的协议前缀
     */
    private static final String WEBSOCKET_PREFIX = "GET /";
    /**
     * Socket的接头暗号是否开启，默认是开启状态
     */
    @Value(NetworkConstant.SOCKET_SIGNAL_ACTIVE)
    private boolean signalActive = true;

    @Autowired
    private Map<String, InitializeHandler> handlers;
    @Autowired
    private SocketInitializeHandler socketInitializeHandler;
    @Autowired
    private WebsocketSslInitializeHandler websocketSslInitializeHandler;

    /**
     * 初始化通道.
     *
     * @param ctx 通道上下文
     * @param in  封包缓冲区
     */
    public void init(ChannelHandlerContext ctx, ByteBuf in) {
        // 开启接口暗号，那就尝试分析
        if (signalActive) {
            this.doAnalyzeSignal(ctx, in);
        }
        // 没有开启暗号，那就当Socket处理了
        else {
            this.doInitSocketHandler(ctx, in);
        }
    }

    private void doInitSocketHandler(ChannelHandlerContext ctx, ByteBuf in) {
        this.socketInitializeHandler.handle(ctx);
    }

    private void doAnalyzeSignal(ChannelHandlerContext ctx, ByteBuf in) {
        // 取出接头暗号
        int length = Math.min(in.readableBytes(), MAX_LENGTH);
        String protocol = ByteBufUtils.readString(in, length);

        // 如果是使用WS链接，修正一个名称
        if (protocol.startsWith(WEBSOCKET_PREFIX)) {
            protocol = WebsocketInitializeHandler.WEBSOCKET_NAME;
        }

        InitializeHandler initializeHandler = handlers.get(protocol);

        // 没有命中规定的方案
        if (initializeHandler == null) {
            // 配置了SSL，那要尝试走一次WSS连接
            if (websocketSslInitializeHandler.isConfigSslContext()) {
                initializeHandler = websocketSslInitializeHandler;
            }
            // 没配置，就不用尝试了，当非法请求处理
            else {
                initializeHandler = new IllegalRequestHandler(protocol);
            }
        }

        // 如果目标是WebSocket的话，那还归还刚刚取出来的判定信息
        if (initializeHandler instanceof WebsocketInitializeHandler) {
            in.resetReaderIndex();
        }

        // 初始化处理器
        initializeHandler.handle(ctx);
    }
}