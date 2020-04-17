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
package xyz.noark.core.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

/**
 * Netty的ByteBuf工具类.
 *
 * @since 3.1.3
 * @author 小流氓[176543888@qq.com]
 */
public class ByteBufUtils {

	/**
	 * 向ByteBuf中写入一个Int32值
	 * 
	 * @param out ByteBuf对象
	 * @param value Int32值
	 */
	public static void writeRawVarint32(ByteBuf out, int value) {
		while (true) {
			if ((value & ~0x7F) == 0) {
				out.writeByte(value);
				return;
			} else {
				// 取最后7位 前面再加1
				out.writeByte((value & 0x7F) | 0x80);
				value >>>= 7;
			}
		}
	}

	/**
	 * 从ByteBuf中读出一个Int32值.
	 * 
	 * @param in ByteBuf对象
	 * @return Int32值
	 */
	public static int readRawVarint32(ByteBuf in) {
		byte tmp = in.readByte();
		if (tmp >= 0) {
			return tmp;
		} else {
			int result = tmp & 127;
			if ((tmp = in.readByte()) >= 0) {
				result |= tmp << 7;
			} else {
				result |= (tmp & 127) << 7;
				if ((tmp = in.readByte()) >= 0) {
					result |= tmp << 14;
				} else {
					result |= (tmp & 127) << 14;
					if ((tmp = in.readByte()) >= 0) {
						result |= tmp << 21;
					} else {
						result |= (tmp & 127) << 21;
						result |= (tmp = in.readByte()) << 28;
						if (tmp < 0) {
							throw new CorruptedFrameException("malformed varint.");
						}
					}
				}
			}
			return result;
		}
	}

	/**
	 * 从ByteBuf中读出一个指定长度的字符串.
	 * 
	 * @param in ByteBuf对象
	 * @param length 字符串的长度
	 * @return 一个指定长度的字符串
	 */
	public static String readString(ByteBuf in, int length) {
		byte[] content = new byte[length];
		in.readBytes(content);
		return new String(content);
	}

	/**
	 * 从ByteBuf中读出所以可读内容
	 * 
	 * @param in ByteBuf对象
	 * @return 可读内容
	 */
	public static byte[] readBytes(ByteBuf in) {
		byte[] content = new byte[in.readableBytes()];
		in.readBytes(content);
		return content;
	}
}