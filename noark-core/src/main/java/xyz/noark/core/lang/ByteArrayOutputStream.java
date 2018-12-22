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
package xyz.noark.core.lang;

import java.io.OutputStream;

/**
 * 包装ByteArray操作的一种实现
 * <p>
 * 方便操作字节数组的一种包装而已，已实现输出流 <br>
 * 写入时，如果ByteArray接口实现自动扩容，那这个输出流就能自动扩容
 *
 * @since 3.2.3
 * @author 小流氓(176543888@qq.com)
 */
public class ByteArrayOutputStream extends OutputStream {
	protected final ByteArray byteArray;
	private int writeIndex = 0;

	public ByteArrayOutputStream(ByteArray byteArray) {
		this.byteArray = byteArray;
	}

	@Override
	public void write(int value) {
		byteArray.setByte(writeIndex++, (byte) value);
	}

	@Override
	public void write(byte[] bytes, int off, int len) {
		for (int i = off; i < len; i++) {
			this.write(bytes[i]);
		}
	}

	public void writeByte(final byte value) {
		this.write(value);
	}

	public void writeShort(final int value) {
		this.writeByte((byte) (value >> 8));
		this.writeByte((byte) value);
	}

	public void writeShortLE(final int value) {
		this.writeByte((byte) value);
		this.writeByte((byte) (value >> 8));
	}

	public void writeInt(final int value) {
		this.writeByte((byte) (value >> 24));
		this.writeByte((byte) (value >> 16));
		this.writeByte((byte) (value >> 8));
		this.writeByte((byte) value);
	}

	public void writeIntLE(final int value) {
		this.writeByte((byte) value);
		this.writeByte((byte) (value >> 8));
		this.writeByte((byte) (value >> 16));
		this.writeByte((byte) (value >> 24));
	}

	public void writeBytes(byte[] bytes) {
		this.write(bytes, 0, bytes.length);
	}

	@Override
	public void close() {}
}