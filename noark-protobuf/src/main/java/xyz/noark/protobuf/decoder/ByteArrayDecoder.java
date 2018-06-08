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
package xyz.noark.protobuf.decoder;

import xyz.noark.protobuf.CodedOutputStream;

/**
 * 字节数组实现的解码器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class ByteArrayDecoder extends CodedOutputStream {
	private byte[] buffer;
	private int limit;
	private int position;

	private MakeNode makePosition;

	public ByteArrayDecoder() {
		this.limit = 1024;
		this.buffer = new byte[limit];
	}

	@Override
	protected void flushIfNotAvailable(int minCapacity) {
		if (limit - position <= minCapacity) {
			limit += Math.max(minCapacity, 512);
			byte[] bytes = new byte[limit];
			System.arraycopy(buffer, 0, bytes, 0, position);
			this.buffer = bytes;
		}
	}

	@Override
	protected void bufferMakeLengthIndex() {
		this.makePosition = new MakeNode(makePosition, position++);
	}

	@Override
	protected void bufferWriteLength() {
		final int length = position - makePosition.getIndex() - 1;
		int size = computeInt32SizeNoTag(length);// 判定一下 长度占几个字节

		if (size > 1) {// 默认是让出了一个位置，大于一个位置，需要移动数据
			System.arraycopy(buffer, makePosition.getIndex() + 1, buffer, makePosition.getIndex() + size, length);
		}

		int nowPosition = position + size - 1;
		position = makePosition.getIndex();
		this.bufferUInt32NoTag(length);
		this.position = nowPosition;

		this.makePosition = makePosition.getPreNode();
	}

	/**
	 * This method does not perform bounds checking on the array. Checking array
	 * bounds is the responsibility of the caller.
	 */
	protected void buffer(byte value) {
		buffer[position++] = value;
	}

	/**
	 * This method does not perform bounds checking on the array. Checking array
	 * bounds is the responsibility of the caller.
	 */
	protected void bufferInt32NoTag(final int value) {
		if (value >= 0) {
			bufferUInt32NoTag(value);
		} else {
			// Must sign-extend.
			bufferUInt64NoTag(value);
		}
	}

	/**
	 * This method does not perform bounds checking on the array. Checking array
	 * bounds is the responsibility of the caller.
	 */
	protected void bufferUInt32NoTag(int value) {
		while (true) {
			if ((value & ~0x7F) == 0) {
				buffer[position++] = (byte) value;
				return;
			} else {
				buffer[position++] = (byte) ((value & 0x7F) | 0x80);
				value >>>= 7;
			}
		}
	}

	/**
	 * This method does not perform bounds checking on the array. Checking array
	 * bounds is the responsibility of the caller.
	 */
	protected final void bufferUInt64NoTag(long value) {
		while (true) {
			if ((value & ~0x7FL) == 0) {
				buffer[position++] = (byte) value;
				return;
			} else {
				buffer[position++] = (byte) (((int) value & 0x7F) | 0x80);
				value >>>= 7;
			}
		}
	}

	/**
	 * This method does not perform bounds checking on the array. Checking array
	 * bounds is the responsibility of the caller.
	 */
	protected final void bufferFixed32NoTag(int value) {
		buffer[position++] = (byte) (value & 0xFF);
		buffer[position++] = (byte) ((value >> 8) & 0xFF);
		buffer[position++] = (byte) ((value >> 16) & 0xFF);
		buffer[position++] = (byte) ((value >> 24) & 0xFF);
	}

	/**
	 * This method does not perform bounds checking on the array. Checking array
	 * bounds is the responsibility of the caller.
	 */
	protected final void bufferFixed64NoTag(long value) {
		buffer[position++] = (byte) (value & 0xFF);
		buffer[position++] = (byte) ((value >> 8) & 0xFF);
		buffer[position++] = (byte) ((value >> 16) & 0xFF);
		buffer[position++] = (byte) ((value >> 24) & 0xFF);
		buffer[position++] = (byte) ((int) (value >> 32) & 0xFF);
		buffer[position++] = (byte) ((int) (value >> 40) & 0xFF);
		buffer[position++] = (byte) ((int) (value >> 48) & 0xFF);
		buffer[position++] = (byte) ((int) (value >> 56) & 0xFF);
	}

	@Override
	protected void bufferByteArrayNoTag(byte[] bytes) {
		final int length = bytes.length;
		this.bufferUInt32NoTag(length);
		System.arraycopy(bytes, 0, buffer, position, length);
		position += length;
	}

	@Override
	public void close() {
		this.position = 0;
	}

	@Override
	public byte[] getByteArray() {
		if (this.position == 0) {
			return new byte[0];
		} else {
			byte[] retBytes = new byte[this.position];
			System.arraycopy(this.buffer, 0, retBytes, 0, this.position);
			return retBytes;
		}
	}

	private static class MakeNode {
		private int index;
		private MakeNode preNode;

		public MakeNode(MakeNode makePosition, int i) {
			this.preNode = makePosition;
			this.index = i;
		}

		public int getIndex() {
			return index;
		}

		public MakeNode getPreNode() {
			return preNode;
		}
	}
}