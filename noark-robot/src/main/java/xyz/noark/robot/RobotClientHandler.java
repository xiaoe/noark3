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
package xyz.noark.robot;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.ioc.manager.PacketMethodManager;
import xyz.noark.core.ioc.wrap.PacketMethodWrapper;
import xyz.noark.core.ioc.wrap.method.LocalPacketMethodWrapper;
import xyz.noark.core.network.SessionManager;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.network.codec.DefaultNetworkPacket;

import static xyz.noark.log.LogHelper.logger;

/**
 * 机器人客户端封包处理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@Sharable
public class RobotClientHandler extends SimpleChannelInboundHandler<DefaultNetworkPacket> {

    @Autowired
    private ThreadDispatcher threadDispatcher;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultNetworkPacket packet) {
        PacketMethodWrapper pmw = PacketMethodManager.getInstance().getPacketMethodWrapper(packet.getOpcode());
        if (pmw == null) {
            logger.warn("undefined protocol, opcode={}", packet.getOpcode());
            return;
        }
        threadDispatcher.dispatchPacket(SessionManager.getSession(ctx.channel().id()), packet, (LocalPacketMethodWrapper) pmw);
    }
}