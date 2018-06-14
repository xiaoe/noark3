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

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import xyz.noark.core.network.AbstractSession;
import xyz.noark.core.network.ProtocalCodec;

/**
 * 基于Netty的Channel实现的Session.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class NettySession extends AbstractSession {
	private final Channel channel;
	private Serializable playerId;

	// 是否为websocket链接.
	private boolean websocket = false;

	public NettySession(Channel channel) {
		super(channel.id().asLongText(), ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress());
		this.channel = channel;
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public Serializable getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Serializable playerId) {
		this.playerId = playerId;
	}

	public void setProtocalCodec(ProtocalCodec protocalCodec) {
		this.protocalCodec = protocalCodec;
	}

	public void setWebsocket(boolean websocket) {
		this.websocket = websocket;
	}

	@Override
	public void send(Integer opcode, Object protocal) {
		// 链接已关闭了...
		if (!channel.isActive()) {
			logger.warn("send packet fail isActive=false. session={},playerId={},opcode={}", id, playerId, opcode);
			return;
		}

		// 不可写，未发送的数据已达最高水位了...
		if (!channel.isWritable()) {
			logger.warn("send packet fail isWritable=false. session={},playerId={},opcode={}", id, playerId, opcode);
			return;
		}

		byte[] bytes = protocalCodec.encode(protocal);

	}

	/**
	 * 给这个Session发送一个封包.
	 * 
	 * @param packet 封包是已处理过的加密压缩等功能后的包.
	 */
	void sendPacket(byte[] packet) {

		if (websocket) {
			channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(packet)), channel.voidPromise());
		} else {
			channel.writeAndFlush(packet, channel.voidPromise());
		}
	}
}