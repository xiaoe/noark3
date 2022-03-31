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
package xyz.noark.network.init;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.network.PacketCodecHolder;
import xyz.noark.network.codec.AbstractPacketCodec;
import xyz.noark.network.handler.SocketServerHandler;

import static xyz.noark.log.LogHelper.logger;

/**
 * Socket协议请求.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class SocketInitializeHandler extends AbstractInitializeHandler {
    public static final String SOCKET_NAME = "________socket_________";
    @Autowired
    private SocketServerHandler socketServerHandler;

    @Override
    public void handle(ChannelHandlerContext ctx) {
        logger.debug("Socket链接...");
        ChannelPipeline pipeline = ctx.pipeline();

        final AbstractPacketCodec codec = (AbstractPacketCodec) PacketCodecHolder.getPacketCodec();
        // Socket有封包长度编码器,但也可能会为空，有时候长度都直接编码进群发了也是一种提升...
        final ChannelHandler lengthEncoder = codec.lengthEncoder();
        if (lengthEncoder != null) {
            pipeline.addLast("encoder", lengthEncoder);
        }

        // Socket有封包长度解码器...
        pipeline.addLast("decoder", codec.lengthDecoder());

        pipeline.addLast("handler", socketServerHandler);

        // Socket是在接到喑号后进行初始化的...
        socketServerHandler.channelActive(ctx.channel());
    }
}