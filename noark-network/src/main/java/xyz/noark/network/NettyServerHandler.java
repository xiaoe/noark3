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

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.network.NetworkListener;
import xyz.noark.core.network.Session;
import xyz.noark.core.network.SessionManager;
import xyz.noark.core.thread.TraceIdFactory;
import xyz.noark.core.util.IpUtils;
import xyz.noark.log.MDC;

import java.io.IOException;

import static xyz.noark.log.LogHelper.logger;

/**
 * Netty链接默认功能处理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
@Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final IpManager ipManager = new IpManager();
    /**
     * 心跳功能，默认值为0，则不生效
     */
    @Value(NetworkConstant.HEARTBEAT)
    protected int heartbeat = 0;
    /**
     * 网络安全之相同IP最大链接数，默认为：256
     */
    @Value(NetworkConstant.SOME_IP_MAX)
    protected int maxSomeIp = 256;

    @Autowired(required = false)
    private NetworkListener networkListener;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 使用链接ID做为TraceId
        MDC.put(TraceIdFactory.TRACE_ID, ctx.channel().id().asLongText());
        logger.info("发现客户端链接，channel={}", ctx.channel());
        if (ipManager.active(IpUtils.getIp(ctx.channel().remoteAddress())) > maxSomeIp) {
            logger.warn("同一个IP链接数超出上限 max={}", maxSomeIp);
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 使用链接ID做为TraceId
        MDC.put(TraceIdFactory.TRACE_ID, ctx.channel().id().asLongText());
        logger.info("客户端断开链接，channel={}", ctx.channel());
        ipManager.inactive(IpUtils.getIp(ctx.channel().remoteAddress()));

        Session session = SessionManager.getSession(ctx.channel().id());
        if (session != null) {
            try {
                if (networkListener != null) {
                    networkListener.channelInactive(session);
                }
            } finally {
                SessionManager.removeSession(session);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (heartbeat > 0 && evt instanceof IdleStateEvent) {
            ctx.close();// 超时T人.
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            logger.debug("Netty捕获异常，cause={}", cause);
        }
    }
}