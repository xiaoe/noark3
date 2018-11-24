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
package xyz.noark.network.handler;

import static xyz.noark.log.LogHelper.logger;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.network.NetworkListener;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;
import xyz.noark.core.network.SessionManager;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.network.IncodeSession;
import xyz.noark.network.NetworkConstant;
import xyz.noark.network.filter.PacketCheckFilter;

/**
 * 抽象的服务器处理类.
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public abstract class AbstractServerHandler<T> extends SimpleChannelInboundHandler<T> {
	@Autowired
	private ThreadDispatcher threadDispatcher;
	@Autowired(required = false)
	private NetworkListener networkListener;
	@Autowired(required = false)
	private PacketCheckFilter packetCheckFilter;

	/** 接收封包统计预警功能是否激活 */
	@Value(NetworkConstant.RECEIVE_ACTIVE)
	private boolean receiveActive = false;
	/** 每秒接收封包长度预警值 */
	@Value(NetworkConstant.RECEIVE_THRESHOLD)
	private int receiveThreshold = 65535;
	/** 统计周期为多少秒 */
	@Value(NetworkConstant.RECEIVE_SECOND)
	private int receiveSecond = 5;
	/** 统计周期内可以出现多少次预警 */
	@Value(NetworkConstant.RECEIVE_COUNT)
	private int receiveCount = 3;

	/**
	 * 处理好网络封包后派发逻辑.
	 * 
	 * @param ctx 链接上下文
	 * @param packet 网络封包
	 */
	protected void dispatchPacket(ChannelHandlerContext ctx, NetworkPacket packet) {
		try (ByteArray array = packet.getByteArray()) {
			Session session = SessionManager.getSession(ctx.channel().id());

			// 开启了数据统计功能.
			if (receiveActive) {
				this.statPacket(session, packet);
			}

			// 封包检测
			if (this.checkPacket(session, packet)) {
				threadDispatcher.dispatchPacket(session, packet);
			}
		}
	}

	/** 封包检测 */
	private boolean checkPacket(Session session, NetworkPacket packet) {
		if (packetCheckFilter != null) {
			// 复制封包检测...
			if (session instanceof IncodeSession && !packetCheckFilter.checkIncode((IncodeSession) session, packet)) {
				return false;
			}

			// 篡改封包检测...
			if (!packetCheckFilter.checkChecksum(session, packet)) {
				return false;
			}
		}

		// 解密
		if (session.getPacketEncrypt().isEncrypt()) {
			session.getPacketEncrypt().decode(packet.getByteArray(), packet.getIncode());
		}
		return true;
	}

	/** 流量统计 */
	private void statPacket(Session session, NetworkPacket packet) {
		long second = System.currentTimeMillis() / 1000;
		// 当前秒内累计接受到的封包长度
		long packetLength = session.getStatis().record(second, packet.getLength());
		if (packetLength >= receiveThreshold) {
			// 当前秒已预警次数
			int warnCount = session.getStatis().warning(second, receiveSecond);
			if (warnCount >= receiveCount) {
				logger.warn("网络封包统计预警：在 {} 秒内累计 {} 次超出 {} 预警值", receiveSecond, warnCount, receiveThreshold);

				if (networkListener != null) {
					if (networkListener.handlePacketWarning(session, receiveSecond, warnCount, receiveThreshold)) {
						return;
					}
				}
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (!(cause instanceof IOException)) {
			logger.debug("Netty捕获异常，cause={}", cause);
		}
	}
}