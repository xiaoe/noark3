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
package xyz.noark.network.codec.json;

import java.util.List;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import xyz.noark.core.util.ByteArrayUtils;
import xyz.noark.network.codec.AbstractPacketCodec;

/**
 * Json封包解码器.
 * <p>
 * 包长（short）+ 协议编号（int） + 内容（Json）
 * 
 * <pre>
 * BEFORE DECODE (306 bytes)                     AFTER DECODE (306 bytes)
 * +--------+------------+---------------+      +--------+------------+---------------+
 * | length |   opcode   |   Json Data   |----->| length |   opcode   |   Json Data   |
 * | 0xFFFF | 0xFFFFFFFF |  (300 bytes)  |      | 0xFFFF | 0xFFFFFFFF |  (300 bytes)  |
 * +--------+------------+---------------+      +--------+------------+---------------+
 * </pre>
 * 
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class SimpleJsonCodec extends AbstractPacketCodec {
	/** 最大封包长度 */
	private final static int MAX_PACKET_LENGTH = 65535;
	private final static int PACKET_BYTE_LENGTH = 2;

	public SimpleJsonCodec() {}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 包长不够...
		if (in.readableBytes() < PACKET_BYTE_LENGTH) {
			return;
		}

		// 封包长度
		in.markReaderIndex();
		int preIndex = in.readerIndex();
		// 包长
		int length = in.readShort();
		if (preIndex == in.readerIndex()) {
			return;
		}

		// 不正常的封包，干掉这个人.
		if (length <= 0 || length > MAX_PACKET_LENGTH) {
			ctx.close();
			return;
		}

		// 封包不全，忽略本次处理.
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}

		// 满足一个封包
		SimpleJsonPacket packet = new SimpleJsonPacket();
		packet.setOpcode(in.readInt());

		// 内容=长度-4
		byte[] content = new byte[length - 4];
		in.readBytes(content);
		packet.setBytes(content);

		out.add(packet);
	}

	@Override
	public <T> T decodeProtocal(byte[] bytes, Class<T> klass) {
		return JSON.parseObject(bytes, klass);
	}

	@Override
	public byte[] encodePacket(Integer opcode, Object protocal) {
		byte[] bytes = JSON.toJSONBytes(protocal);
		byte[] data2 = ByteArrayUtils.toByteArray(opcode);
		byte[] data3 = new byte[bytes.length + data2.length];
		System.arraycopy(data2, 0, data3, 0, data2.length);
		System.arraycopy(bytes, 0, data3, data2.length, bytes.length);
		return data3;
	}
}