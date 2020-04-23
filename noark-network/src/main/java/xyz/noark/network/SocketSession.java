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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.network.AbstractSession;
import xyz.noark.core.network.NetworkProtocol;
import xyz.noark.core.network.PacketCodecHolder;
import xyz.noark.core.network.PacketEncrypt;
import xyz.noark.core.network.SessionAttr;
import xyz.noark.core.network.SessionAttrKey;
import xyz.noark.core.util.IpUtils;

/**
 * 基于Netty的Channel实现的Session.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public class SocketSession extends AbstractSession implements IncodeSession {
	protected final Channel channel;
	/** Session中存储的属性值 */
	protected final Map<SessionAttrKey<?>, SessionAttr<?>> attrs;
	private String uid;
	private Serializable playerId;
	protected PacketEncrypt packetEncrypt;

	/** 自增校验位 */
	protected int incode = -1;

	public SocketSession(Channel channel, boolean encrypt, byte[] secretKey) {
		super(channel.id(), IpUtils.getIp(channel.remoteAddress()));
		this.channel = channel;
		this.attrs = new ConcurrentHashMap<>();
		this.packetEncrypt = new DefaultPacketEncrypt(encrypt, secretKey);
	}

	@Override
	public int getIncode() {
		return incode;
	}

	@Override
	public void setIncode(int incode) {
		this.incode = incode;
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
	public void send(Serializable opcode, Object protocol) {
		this.send(buildPacket(opcode, protocol));
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

	@Override
	public void send(NetworkProtocol networkProtocol) {
		this.send(PacketCodecHolder.getPacketCodec().encodePacket(networkProtocol));
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
	public void sendAndClose(Serializable opcode, Object protocol) {
		channel.writeAndFlush(buildPacket(opcode, protocol)).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * 构建发送的封包对象.
	 * 
	 * @param opcode 协议编号
	 * @param protocol 协议内容
	 * @return 封包对象
	 */
	protected ByteArray buildPacket(Serializable opcode, Object protocol) {
		return PacketCodecHolder.getPacketCodec().encodePacket(new NetworkProtocol(opcode, protocol));
	}

	/**
	 * 设计封包密码方案.
	 * <p>
	 * 当不喜欢默认的方案可以自己实现此接口重置加密方案
	 * 
	 * @param packetEncrypt 封包密码方案
	 */
	public void setPacketEncrypt(PacketEncrypt packetEncrypt) {
		this.packetEncrypt = packetEncrypt;
	}

	@Override
	public PacketEncrypt getPacketEncrypt() {
		return packetEncrypt;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> SessionAttr<T> attr(SessionAttrKey<T> key) {
		return (SessionAttr<T>) attrs.computeIfAbsent(key, k -> new SessionAttr<>());
	}

	@Override
	public void clearUidAndPlayerId() {
		this.uid = null;
		this.playerId = null;
		this.state = State.CONNECTED;
	}
}