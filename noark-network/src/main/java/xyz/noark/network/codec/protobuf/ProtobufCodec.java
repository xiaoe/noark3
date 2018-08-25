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
package xyz.noark.network.codec.protobuf;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.MessageLite;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import xyz.noark.core.exception.DataException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.util.ByteBufUtils;
import xyz.noark.core.util.MethodUtils;
import xyz.noark.core.util.ProtobufUtils;
import xyz.noark.network.NetworkPacket;
import xyz.noark.network.codec.AbstractPacketCodec;
import xyz.noark.network.codec.ByteBufWrapper;
import xyz.noark.network.codec.DefaultNetworkPacket;

/**
 * ProtobufV3版本的编码解码器.
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public class ProtobufCodec extends AbstractPacketCodec {
	private static final ConcurrentHashMap<Class<?>, Method> CACHES = new ConcurrentHashMap<>(1024);

	@Override
	@SuppressWarnings("unchecked")
	public <T> T decodeProtocal(ByteArray bytes, Class<T> klass) {
		Method method = CACHES.computeIfAbsent(klass, key -> MethodUtils.getMethod(key, "parseFrom", byte[].class));
		return (T) MethodUtils.invoke(null, method, bytes.array());
	}

	@Override
	public ByteArray encodePacket(Integer opcode, Object protocal) {
		MessageLite message = null;
		if (protocal instanceof MessageLite) {
			message = (MessageLite) protocal;
		} else if (protocal instanceof MessageLite.Builder) {
			message = ((MessageLite.Builder) protocal).build();
		} else {
			throw new UnrealizedException("illegal data type：" + protocal.getClass());
		}

		try {
			final int protocalLength = message.getSerializedSize();
			final int opcodeLength = ProtobufUtils.computeRawVarint32Size(opcode);
			ByteBuf byteBuf = Unpooled.buffer(protocalLength + opcodeLength);
			// 写入Opcode
			ByteBufUtils.writeRawVarint32(byteBuf, opcode);
			// 写入协议内容
			message.writeTo(CodedOutputStream.newInstance(byteBuf.array(), opcodeLength, byteBuf.capacity()));
			return new ByteBufWrapper(byteBuf);
		} catch (IOException e) {
			throw new DataException("数据异常", e);
		}
	}

	@Override
	public MessageToByteEncoder<?> lengthEncoder() {
		return new ProtobufLengthEncoder();
	}

	@Override
	public ByteToMessageDecoder lengthDecoder() {
		return new ProtobufLengthDecoder(this);
	}

	@Override
	public NetworkPacket decodePacket(ByteBuf byteBuf) {
		DefaultNetworkPacket packet = new DefaultNetworkPacket();
		packet.setLength(byteBuf.readableBytes());
		packet.setOpcode(byteBuf.readInt());
		packet.setBytes(new ByteBufWrapper(byteBuf));
		return packet;
	}
}