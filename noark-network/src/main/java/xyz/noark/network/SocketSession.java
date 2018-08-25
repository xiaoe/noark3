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

import java.io.Serializable;
import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.network.AbstractSession;
import xyz.noark.core.network.PacketCodecHolder;

/**
 * 基于Netty的Channel实现的Session.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class SocketSession extends AbstractSession {
	protected final Channel channel;
	private String uid;
	private Serializable playerId;

	public SocketSession(Channel channel) {
		super(channel.id(), ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress());
		this.channel = channel;
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public String getUid() {
		return uid;
	}

	/**
	 * 设置玩家UID.
	 * 
	 * @param uid 玩家UID
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public Serializable getPlayerId() {
		return playerId;
	}

	/**
	 * 设置玩家ID.
	 * <p>
	 * 这个值只能在设置玩家UId后调用.
	 * 
	 * @param playerId 玩家ID
	 */
	public void setPlayerId(Serializable playerId) {
		this.playerId = playerId;
	}

	@Override
	public void send(Integer opcode, Object protocal) {
		this.send(buildPacket(opcode, protocal));
	}

	@Override
	public void send(ByteArray packet) {
		// 链接已关闭了...
		if (!channel.isActive()) {
			logger.warn("send packet fail isActive=false. channel={}, playerId={}", channel, playerId);
			return;
		}

		// 不可写，未发送的数据已达最高水位了...
		if (!channel.isWritable()) {
			logger.warn("send packet fail isWritable=false. channel={}, playerId={}", channel, playerId);
			return;
		}

		this.writeAndFlush(packet);
	}

	/**
	 * 发送封包逻辑.
	 * 
	 * @param packet 封包逻辑
	 */
	protected void writeAndFlush(ByteArray packet) {
		channel.writeAndFlush(packet, channel.voidPromise());
	}

	@Override
	public void sendAndClose(Integer opcode, Object protocal) {
		channel.writeAndFlush(buildPacket(opcode, protocal)).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * 构建发送的封包对象.
	 * 
	 * @param opcode 协议编号
	 * @param protocal 协议内容
	 * @return 封包对象
	 */
	protected ByteArray buildPacket(Integer opcode, Object protocal) {
		return PacketCodecHolder.getPacketCodec().encodePacket(opcode, protocal);
	}
}