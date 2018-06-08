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
package xyz.noark.protobuf;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ProtoBuf编码输入流.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class CodedInputStream {
	// 默认的字符串编码
	private static final Charset DEFAULT_STRING_CHARSET = Charset.forName("UTF-8");
	private final byte[] byteArray;
	private int bufferSize;
	private int pos;

	public CodedInputStream(byte[] byteArray) {
		this.byteArray = byteArray;
		this.bufferSize = byteArray.length;
	}

	public int readTag() {
		if (isAtEnd()) {
			return 0;
		}
		return readInt32();
	}

	private boolean isAtEnd() {
		return pos == bufferSize;
	}

	private static final int TAG_TYPE_BITS = 3;
	private static final int TAG_TYPE_MASK = (1 << TAG_TYPE_BITS) - 1;
	private static final int FIXED_32_SIZE = 4;
	private static final int FIXED_64_SIZE = 8;
	private static final int MAX_VARINT_SIZE = 10;// 一个Varint128的编码方式最大长度

	public boolean skipField(int tag) {
		switch (tag & TAG_TYPE_MASK) {
		case 0:// WIRETYPE_VARINT
			skipRawVarint();
			return true;
		case 1:// WIRETYPE_FIXED64
			skipRawBytes(FIXED_64_SIZE);
			return true;
		case 2:// WIRETYPE_LENGTH_DELIMITED
			skipRawBytes(readInt32());
			return true;
		case 5:// WIRETYPE_FIXED32
			skipRawBytes(FIXED_32_SIZE);
			return true;
		default:
			return true;
		}
	}

	private void skipRawVarint() {
		for (int i = 0; i < MAX_VARINT_SIZE; i++) {
			if (byteArray[pos++] >= 0) {
				return;
			}
		}
	}

	public void skipRawBytes(final int length) {
		this.pos += length;
	}

	public boolean readBool() {
		return byteArray[pos++] != 0;
	}

	public int readInt32() {
		// See implementation notes for readRawVarint64
		fastpath: {
			int tempPos = pos;
			if (bufferSize == tempPos) {
				break fastpath;
			}

			final byte[] buffer = this.byteArray;
			int x;
			if ((x = buffer[tempPos++]) >= 0) {
				pos = tempPos;
				return x;
			} else if (bufferSize - tempPos < 9) {
				break fastpath;
			} else if ((x ^= (buffer[tempPos++] << 7)) < 0) {
				x ^= (~0 << 7);
			} else if ((x ^= (buffer[tempPos++] << 14)) >= 0) {
				x ^= (~0 << 7) ^ (~0 << 14);
			} else if ((x ^= (buffer[tempPos++] << 21)) < 0) {
				x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21);
			} else {
				int y = buffer[tempPos++];
				x ^= y << 28;
				x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21) ^ (~0 << 28);
				if (y < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0) {
					break fastpath; // Will throw malformedVarint()
				}
			}
			pos = tempPos;
			return x;
		}
		return (int) readRawVarint64SlowPath();
	}

	private long readRawVarint64SlowPath() {
		long result = 0;
		for (int shift = 0; shift < 64; shift += 7) {
			final byte b = byteArray[pos++];
			result |= (long) (b & 0x7F) << shift;
			if ((b & 0x80) == 0) {
				return result;
			}
		}
		return result;
	}

	public int readSInt32() {
		return decodeZigZag32(readInt32());
	}

	public int readFixed32() {
		return (((byteArray[pos++] & 0xff)) | //
				((byteArray[pos++] & 0xff) << 8) | //
				((byteArray[pos++] & 0xff) << 16) | //
				((byteArray[pos++] & 0xff) << 24));
	}

	public long readInt64() {
		long result = 0;
		for (int shift = 0; shift < 64; shift += 7) {
			final byte b = byteArray[pos++];
			result |= (long) (b & 0x7F) << shift;
			if ((b & 0x80) == 0) {
				return result;
			}
		}
		return result;
	}

	public long readSInt64() {
		return decodeZigZag64(readInt64());
	}

	public long readFixed64() {
		return (((byteArray[pos++] & 0xffL))//
				| ((byteArray[pos++] & 0xffL) << 8)//
				| ((byteArray[pos++] & 0xffL) << 16)//
				| ((byteArray[pos++] & 0xffL) << 24)//
				| ((byteArray[pos++] & 0xffL) << 32)//
				| ((byteArray[pos++] & 0xffL) << 40)//
				| ((byteArray[pos++] & 0xffL) << 48)//
				| ((byteArray[pos++] & 0xffL) << 56));//
	}

	public String readString() {
		final int size = readInt32();
		if (size == 0) {
			return "";
		} else {
			final String result = new String(byteArray, pos, size, DEFAULT_STRING_CHARSET);
			pos += size;
			return result;
		}
	}

	public double readDouble() {
		return Double.longBitsToDouble(readFixed64());
	}

	public float readFloat() {
		return Float.intBitsToFloat(readFixed32());
	}

	public <T extends ProtobufSerializable> T readMessage(T message) {
		final int size = readInt32();
		final int makeEnd = bufferSize;
		this.bufferSize = pos + size;
		message.readFrom(this);
		this.bufferSize = makeEnd;
		return message;
	}

	public Collection<? extends Boolean> readBoolList() {
		final int limit = pos + readInt32();
		List<Boolean> result = new ArrayList<>();
		while (limit >= pos) {
			result.add(readBool());
		}
		return result;
	}

	public Collection<? extends Integer> readInt32List() {
		final int limit = pos + readInt32();
		List<Integer> result = new ArrayList<>();
		while (limit >= pos) {
			result.add(readInt32());
		}
		return result;
	}

	public Collection<? extends Integer> readSInt32List() {
		final int limit = pos + readInt32();
		List<Integer> result = new ArrayList<>();
		while (limit >= pos) {
			result.add(readSInt32());
		}
		return result;
	}

	public Collection<? extends Integer> readFixed32List() {
		final int limit = pos + readInt32();
		List<Integer> result = new ArrayList<>();
		while (limit >= pos) {
			result.add(readFixed32());
		}
		return result;
	}

	public Collection<? extends Long> readInt64List() {
		final int limit = pos + readInt32();
		List<Long> result = new ArrayList<>();
		while (limit >= pos) {
			result.add(readInt64());
		}
		return result;
	}

	public Collection<? extends Long> readFixed64List() {
		final int limit = pos + readInt32();
		List<Long> result = new ArrayList<>();
		while (limit >= pos) {
			result.add(readFixed64());
		}
		return result;
	}

	public Collection<? extends Long> readSInt64List() {
		final int limit = pos + readInt32();
		List<Long> result = new ArrayList<>();
		while (limit >= pos) {
			result.add(readSInt64());
		}
		return result;
	}

	/**
	 * Decode a ZigZag-encoded 32-bit value. ZigZag encodes signed integers into
	 * values that can be efficiently encoded with varint. (Otherwise, negative
	 * values must be sign-extended to 64 bits to be varint encoded, thus always
	 * taking 10 bytes on the wire.)
	 *
	 * @param n An unsigned 32-bit integer, stored in a signed int because Java
	 *            has no explicit unsigned support.
	 * @return A signed 32-bit integer.
	 */
	public static int decodeZigZag32(final int n) {
		return (n >>> 1) ^ -(n & 1);
	}

	/**
	 * Decode a ZigZag-encoded 64-bit value. ZigZag encodes signed integers into
	 * values that can be efficiently encoded with varint. (Otherwise, negative
	 * values must be sign-extended to 64 bits to be varint encoded, thus always
	 * taking 10 bytes on the wire.)
	 *
	 * @param n An unsigned 64-bit integer, stored in a signed int because Java
	 *            has no explicit unsigned support.
	 * @return A signed 64-bit integer.
	 */
	public static long decodeZigZag64(final long n) {
		return (n >>> 1) ^ -(n & 1);
	}

}