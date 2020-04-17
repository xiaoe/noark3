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
package xyz.noark.network.codec;

import io.netty.buffer.ByteBuf;
import xyz.noark.core.lang.ByteArray;

/**
 * 基于ByteBuf实现的字节数组接口实现.
 * <p>
 * ByteBuf的计数器加一拿出来的，使用时一定要调用关闭方法来减一，不然就会死人了噢，不了解NETTY请不要使用此类
 *
 * @since 3.1
 * @author 小流氓[176543888@qq.com]
 */
public class ByteBufWrapper implements ByteArray {
	private final ByteBuf byteBuf;
	private byte[] array = null;

	public ByteBufWrapper(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	@Override
	public byte[] array() {
		if (array == null) {
			array = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(array);
		}
		return array;
	}

	@Override
	public void close() {
		byteBuf.release();// 拿出一个带引用的ByteBuf，这里--
	}

	@Override
	public int length() {
		return array == null ? byteBuf.readableBytes() : array.length;
	}

	@Override
	public byte getByte(int index) {
		return array == null ? byteBuf.getByte(index) : array[index];
	}

	@Override
	public void setByte(int index, byte value) {
		if (array == null) {
			byteBuf.setByte(index, value);
		} else {
			array[index] = value;
		}
	}
}