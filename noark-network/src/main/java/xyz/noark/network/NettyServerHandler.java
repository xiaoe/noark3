/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 * 
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.network;

import static xyz.noark.log.LogHelper.logger;

import java.io.IOException;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Service;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.network.NetworkListener;
import xyz.noark.core.network.Session;
import xyz.noark.core.network.SessionManager;

/**
 * Netty链接默认功能处理器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Service
@Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
	/** 心跳功能，默认值为0，则不生效 */
	@Value(NetworkConstant.HEARTBEAT)
	protected int heartbeat = 0;
	@Autowired(required = false)
	private NetworkListener networkListener;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("发现客户端链接，channel={}", ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("客户端断开链接，channel={}", ctx.channel());
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