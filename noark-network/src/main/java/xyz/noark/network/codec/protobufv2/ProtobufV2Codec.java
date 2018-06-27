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
package xyz.noark.network.codec.protobufv2;

import static xyz.noark.log.LogHelper.logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.GeneratedMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import xyz.noark.network.codec.AbstractPacketCodec;
import xyz.noark.util.MethodUtils;
import xyz.noark.util.ProtobufUtils;

/**
 * Json封包解码器.
 * <p>
 * 包长（short）+ 协议编号（varint128） + 内容（PB）+ 自增校验位(byte) + Checksum(byte)
 * 
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class ProtobufV2Codec extends AbstractPacketCodec {
	/** 最大封包长度 */
	private static final int MAX_PACKET_LENGTH = 65535;
	private static final ConcurrentHashMap<Class<?>, Method> CACHES = new ConcurrentHashMap<>(1024);

	public ProtobufV2Codec() {}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (!in.isReadable()) {
			return;
		}

		// 封包长度
		in.markReaderIndex();
		int preIndex = in.readerIndex();
		// 包长
		int length = this.readRawVarint32(in);
		if (preIndex == in.readerIndex()) {
			return;
		}

		// 不正常的封包，干掉这个人.
		if (length <= 0 || length > MAX_PACKET_LENGTH) {
			logger.warn("发现不正常的包长. session={}, length={}", ctx.channel(), length);
			ctx.close();
			return;
		}

		// 封包不全，忽略本次处理.
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}

		// 满足一个封包
		ProtobufV2Packet packet = new ProtobufV2Packet();
		final int opcode = this.readRawVarint32(in);
		packet.setOpcode(opcode);

		// 内容=长度-2
		byte[] content = new byte[length - 2 - ProtobufUtils.computeRawVarint32Size(opcode)];
		in.readBytes(content);
		packet.setBytes(content);

		packet.setIncode(in.readByte());
		packet.setChecksum(in.readByte());

		out.add(packet);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T decodeProtocal(byte[] bytes, Class<T> klass) {
		Method method = CACHES.computeIfAbsent(klass, key -> MethodUtils.getMethod(key, "parseFrom", byte[].class));
		return (T) MethodUtils.invoke(null, method, bytes);
	}

	@Override
	public byte[] encodePacket(Integer opcode, Object protocal) {
		byte[] bytes = ((GeneratedMessage) protocal).toByteArray();
		byte[] data2 = ProtobufUtils.encodeInt32(opcode);
		byte[] data3 = new byte[bytes.length + data2.length];
		System.arraycopy(data2, 0, data3, 0, data2.length);
		System.arraycopy(bytes, 0, data3, data2.length, bytes.length);
		return data3;
	}

	/** Reads variable length 32bit int from buffer */
	private int readRawVarint32(ByteBuf buffer) {
		if (!buffer.isReadable()) {
			return 0;
		}
		byte tmp = buffer.readByte();
		if (tmp >= 0) {
			return tmp;
		} else {
			int result = tmp & 127;
			if (!buffer.isReadable()) {
				buffer.resetReaderIndex();
				return 0;
			}
			if ((tmp = buffer.readByte()) >= 0) {
				result |= tmp << 7;
			} else {
				result |= (tmp & 127) << 7;
				if (!buffer.isReadable()) {
					buffer.resetReaderIndex();
					return 0;
				}
				if ((tmp = buffer.readByte()) >= 0) {
					result |= tmp << 14;
				} else {
					result |= (tmp & 127) << 14;
					if (!buffer.isReadable()) {
						buffer.resetReaderIndex();
						return 0;
					}
					if ((tmp = buffer.readByte()) >= 0) {
						result |= tmp << 21;
					} else {
						result |= (tmp & 127) << 21;
						if (!buffer.isReadable()) {
							buffer.resetReaderIndex();
							return 0;
						}
						result |= (tmp = buffer.readByte()) << 28;
						if (tmp < 0) {
							throw new CorruptedFrameException("malformed varint.");
						}
					}
				}
			}
			return result;
		}
	}
}