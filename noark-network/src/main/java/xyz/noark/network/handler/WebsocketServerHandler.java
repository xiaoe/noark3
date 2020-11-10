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
package xyz.noark.network.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import xyz.noark.core.annotation.Service;
import xyz.noark.core.network.PacketCodecHolder;
import xyz.noark.core.network.Session;
import xyz.noark.network.WebSocketSession;
import xyz.noark.network.codec.AbstractWebsocketPacketCodec;

import javax.annotation.PostConstruct;

import static xyz.noark.log.LogHelper.logger;

/**
 * Websocket服务器处理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
@Service
@Sharable
public class WebsocketServerHandler extends AbstractServerHandler<WebSocketFrame> {

    private AbstractWebsocketPacketCodec codec;

    @PostConstruct
    public void init() {
        this.codec = (AbstractWebsocketPacketCodec) PacketCodecHolder.getPacketCodec();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame wsf) throws Exception {
        // 整个封包解出来就跟Socket一样处理了...
        this.dispatchPacket(ctx, codec.decodePacket(wsf.content().retain()));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        // 握手成功完成，并将频道升级到websockets。
        if (evt instanceof HandshakeComplete) {
            logger.info("WebSocket链接成功....");
            this.channelActive(ctx.channel());
        }
    }

    @Override
    protected Session createSession(Channel channel) {
        // WebSocket强制不加密，走H5的标准加密，没有必要两次
        return new WebSocketSession(channel, false, secretKey);
    }
}