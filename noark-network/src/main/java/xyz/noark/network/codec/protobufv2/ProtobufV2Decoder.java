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

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

/**
 * ProtobufV2封包解码器.
 * <p>
 * 
 * <pre>
 * BEFORE DECODE (302 bytes)       AFTER DECODE (300 bytes)
 * +--------+---------------+      +---------------+
 * | Length | Protobuf Data |----->| Protobuf Data |
 * | 0xAC02 |  (300 bytes)  |      |  (300 bytes)  |
 * +--------+---------------+      +---------------+
 * </pre>
 * 
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class ProtobufV2Decoder extends ByteToMessageDecoder {
	private final static int max_packet_length = 65535;// 最大封包长度

	public ProtobufV2Decoder() {}

	// 封包长度 + 自增位 + Opcode + 协议内容 + 校验位
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		// 封包长度
		in.markReaderIndex();
		int preIndex = in.readerIndex();
		int length = this.readRawVarint32(in);// 包长
		if (preIndex == in.readerIndex()) {
			return;
		}

		// 不正常的封包，干掉这个人.
		if (length <= 0 || length > max_packet_length) {
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
		packet.setIncode(this.readRawVarint32(in));

		// 内容=长度-2
		byte[] content = new byte[length - 2];
		in.readBytes(content);
		packet.setBytes(content);

		packet.setIncode(in.readByte());
		packet.setChecksum(in.readByte());

		out.add(packet);
	}

	// Reads variable length 32bit int from buffer
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