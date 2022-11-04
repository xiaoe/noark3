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
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 协议初始化解码器.
 * <p>
 * <b>这个功能就是用来判定实际使用什么协议.</b>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class InitializeDecoder extends ByteToMessageDecoder {

    private final InitializeHandlerManager initializeHandlerManager;

    public InitializeDecoder(InitializeHandlerManager initializeHandlerManager) {
        this.initializeHandlerManager = initializeHandlerManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 标记读位置...
        in.markReaderIndex();

        // 智能分析接头暗号增加相对应协议解码器
        initializeHandlerManager.init(ctx, in);

        // 事情办完了，移除自己
        ctx.pipeline().remove(this.getClass());
    }
}