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

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Service;
import xyz.noark.core.ioc.manager.PacketMethodManager;
import xyz.noark.core.ioc.wrap.method.PacketMethodWrapper;
import xyz.noark.core.network.NetworkListener;
import xyz.noark.core.network.Session;
import xyz.noark.core.network.SessionManager;
import xyz.noark.core.thread.ThreadDispatcher;

/**
 * Netty接到封包后的处理器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Service
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<NetworkPacket> {

	@Autowired
	private ThreadDispatcher threadDispatcher;
	@Autowired(required = false)
	private NetworkListener networkListener;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("发现客户端链接，channel={}", ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("客户端断开链接，channel={}", ctx.channel());
		Session session = SessionManager.getSession(ctx.channel().id().asLongText());
		if (session != null) {
			try {
				networkListener.channelInactive(session);
			} finally {
				SessionManager.removeSession(session);
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.debug("异常，cause={}", cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, NetworkPacket msg) throws Exception {
		Session session = SessionManager.getSession(ctx.channel().id().asLongText());

		PacketMethodWrapper pmw = PacketMethodManager.getInstance().getPacketMethodWrapper(msg.getOpcode());

		if (pmw == null) {
			logger.warn("undefined protocol, opcode={}", msg.getOpcode());
			return;
		}

		// 是否已废弃使用.
		if (pmw.isDeprecated()) {
			logger.warn("deprecated protocol. opcode={}, playerId={}", msg.getOpcode(), session.getPlayerId());
			return;
		}

		// 客户端发来的封包，是不可以调用内部处理器的.
		if (pmw.isInner()) {
			logger.warn(" ^0^ inner protocol. opcode={}, playerId={}", msg.getOpcode(), session.getPlayerId());
			return;
		}

		// 增加协议计数.
		pmw.incrCount();

		this.localDispatch(session, pmw, msg);
	}

	private void localDispatch(Session session, PacketMethodWrapper pmw, NetworkPacket msg) {
		// 参数列表.
		Object[] args = pmw.analysisParam(session, msg.getBytes());

		threadDispatcher.dispatchPacket(session, pmw, args);

	}

}
