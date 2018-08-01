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

/**
 * 提供一些简单的PB编码相关方法.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class ProtobufUtils {
	/**
	 * Computes size of protobuf varint32 after encoding.
	 * 
	 * @param value which is to be encoded.
	 * @return size of value encoded as protobuf varint32.
	 */
	public static int computeRawVarint32Size(final int value) {
		if ((value & (0xffffffff << 7)) == 0) {
			return 1;
		}
		if ((value & (0xffffffff << 14)) == 0) {
			return 2;
		}
		if ((value & (0xffffffff << 21)) == 0) {
			return 3;
		}
		if ((value & (0xffffffff << 28)) == 0) {
			return 4;
		}
		return 5;
	}

	/**
	 * 以varint32的方式编码一个数字.
	 * 
	 * @param value 数字
	 * @return 编码后的字节数组.
	 */
	public static byte[] encodeInt32(int value) {
		byte[] result = new byte[computeRawVarint32Size(value)];
		for (int i = 0, len = result.length; i < len; i++) {
			if ((value & ~0x7F) == 0) {
				result[i] = (byte) value;
			} else {
				result[i] = (byte) ((value & 0x7F) | 0x80);
				value >>>= 7;
			}
		}
		return result;
	}
}