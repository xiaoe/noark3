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
import java.util.LinkedList;
import java.util.List;

import xyz.noark.protobuf.decoder.ByteArrayDecoder;

/**
 * ProtoBuf编码输出流.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public abstract class CodedOutputStream implements AutoCloseable {
	private static final int FIXED_32_SIZE = 4;
	private static final int FIXED_64_SIZE = 8;
	private static final int MAX_VARINT_SIZE = 10;// 一个Varint128的编码方式最大长度
	// 默认的字符串编码
	private static final Charset DEFAULT_STRING_CHARSET = Charset.forName("UTF-8");

	public static CodedOutputStream newInstance() {
		return new ByteArrayDecoder();
	}

	/**
	 * Encode a ZigZag-encoded 32-bit value. ZigZag encodes signed integers into
	 * values that can be efficiently encoded with varint. (Otherwise, negative
	 * values must be sign-extended to 64 bits to be varint encoded, thus always
	 * taking 10 bytes on the wire.)
	 *
	 * @param n A signed 32-bit integer.
	 * @return An unsigned 32-bit integer, stored in a signed int because Java
	 *         has no explicit unsigned support.
	 */
	public static int encodeZigZag32(final int n) {
		return (n << 1) ^ (n >> 31);
	}

	/**
	 * Encode a ZigZag-encoded 64-bit value. ZigZag encodes signed integers into
	 * values that can be efficiently encoded with varint. (Otherwise, negative
	 * values must be sign-extended to 64 bits to be varint encoded, thus always
	 * taking 10 bytes on the wire.)
	 *
	 * @param n A signed 64-bit integer.
	 * @return An unsigned 64-bit integer, stored in a signed int because Java
	 *         has no explicit unsigned support.
	 */
	public static long encodeZigZag64(final long n) {
		return (n << 1) ^ (n >> 63);
	}

	/**
	 * 写入一个Bool值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value Bool值.
	 */
	public void writeBool(final int tag, final boolean value) {
		flushIfNotAvailable(MAX_VARINT_SIZE + 1);// 扩容判定
		bufferUInt32NoTag(tag);
		buffer((byte) (value ? 1 : 0));
	}

	/**
	 * 写入一个int32值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value int32值.
	 */
	public void writeInt32(final int tag, final int value) {
		flushIfNotAvailable(MAX_VARINT_SIZE * 2);// 扩容判定
		bufferUInt32NoTag(tag);
		bufferInt32NoTag(value);
	}

	/**
	 * 写入一个sint32值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value sint32值.
	 */
	public void writeSInt32(final int tag, final int value) {
		this.writeInt32(tag, encodeZigZag32(value));
	}

	/**
	 * 写入一个fixed32值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value fixed32值.
	 */
	public void writeFixed32(final int tag, final int value) {
		flushIfNotAvailable(MAX_VARINT_SIZE + FIXED_32_SIZE);
		bufferUInt32NoTag(tag);
		bufferFixed32NoTag(value);
	}

	/**
	 * 写入一个int64值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value int64值.
	 */
	public void writeInt64(final int tag, final long value) {
		flushIfNotAvailable(MAX_VARINT_SIZE * 2);
		bufferUInt32NoTag(tag);
		bufferUInt64NoTag(value);
	}

	/**
	 * 写入一个sint64值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value sint64值.
	 */
	public void writeSInt64(final int tag, final long value) {
		this.writeInt64(tag, encodeZigZag64(value));
	}

	/**
	 * 写入一个fixed64值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value fixed64值.
	 */
	public void writeFixed64(final int tag, final long value) {
		flushIfNotAvailable(MAX_VARINT_SIZE + FIXED_64_SIZE);
		bufferUInt32NoTag(tag);
		bufferFixed64NoTag(value);
	}

	/**
	 * 写入一个string值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value string值.
	 */
	public void writeString(final int tag, final String value) {
		final byte[] bytes = value.getBytes(DEFAULT_STRING_CHARSET);
		// Tag + len + size
		flushIfNotAvailable(MAX_VARINT_SIZE + MAX_VARINT_SIZE + bytes.length);
		bufferUInt32NoTag(tag);
		bufferByteArrayNoTag(bytes);
	}

	/**
	 * 写入一个float值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value float值
	 */
	public void writeFloat(final int tag, final float value) {
		writeFixed32(tag, Float.floatToRawIntBits(value));
	}

	/**
	 * 写入一个double值.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value double值
	 */
	public void writeDouble(final int tag, final double value) {
		writeFixed64(tag, Double.doubleToRawLongBits(value));
	}

	/**
	 * 写入一个PB对象.
	 * 
	 * @param tag 计算好的Tag值
	 * @param value PB对象.
	 */
	public <T extends ProtobufSerializable> void writeMessage(final int tag, final T value) {
		flushIfNotAvailable(MAX_VARINT_SIZE);
		bufferUInt32NoTag(tag);
		bufferMakeLengthIndex();
		value.writeTo(this);
		bufferWriteLength();
	}

	// 写入数组区-----数组是写入字节长度，不是数量...
	/**
	 * 写入一个Boolean数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeBoolList(final int tag, final List<Boolean> values, final boolean packet) {
		if (packet) {
			flushIfNotAvailable(MAX_VARINT_SIZE + MAX_VARINT_SIZE + values.size());
			bufferUInt32NoTag(tag);
			bufferUInt32NoTag(values.size());// 一个字节
			for (boolean value : values) {
				buffer((byte) (value ? 1 : 0));
			}
		} else {
			flushIfNotAvailable((MAX_VARINT_SIZE + 1) * values.size());
			for (boolean value : values) {
				bufferUInt32NoTag(tag);
				buffer((byte) (value ? 1 : 0));
			}
		}
	}

	/**
	 * 写入一个int32数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeInt32List(final int tag, final List<Integer> values, final boolean packet) {
		if (packet) {
			flushIfNotAvailable(MAX_VARINT_SIZE * (2 + values.size()));
			bufferUInt32NoTag(tag);
			bufferUInt32NoTag(computeInt32Size(values));// 计算长度
			for (Integer value : values) {
				bufferInt32NoTag(value);
			}
		} else {
			flushIfNotAvailable(MAX_VARINT_SIZE * 2 * values.size());
			for (Integer value : values) {
				bufferUInt32NoTag(tag);
				bufferInt32NoTag(value);
			}
		}
	}

	/**
	 * 写入一个sint32数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeSInt32List(final int tag, final List<Integer> values, final boolean packet) {
		if (packet) {
			flushIfNotAvailable(MAX_VARINT_SIZE * (2 + values.size()));
			bufferUInt32NoTag(tag);
			bufferUInt32NoTag(computeSInt32Size(values));// 计算长度
			for (Integer value : values) {
				bufferInt32NoTag(encodeZigZag32(value));
			}
		} else {
			flushIfNotAvailable(MAX_VARINT_SIZE * (2 * values.size()));
			for (Integer value : values) {
				bufferUInt32NoTag(tag);
				bufferInt32NoTag(encodeZigZag32(value));
			}
		}
	}

	/**
	 * 写入一个fixed32数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeFixed32List(final int tag, final List<Integer> values, final boolean packet) {
		if (packet) {
			flushIfNotAvailable(MAX_VARINT_SIZE * 2 + FIXED_32_SIZE * values.size());
			bufferUInt32NoTag(tag);
			bufferUInt32NoTag(FIXED_32_SIZE * values.size());// 计算长度
			for (Integer value : values) {
				bufferFixed32NoTag(value);
			}
		} else {
			flushIfNotAvailable((MAX_VARINT_SIZE + FIXED_32_SIZE) * values.size());
			for (Integer value : values) {
				bufferUInt32NoTag(tag);
				bufferFixed32NoTag(value);
			}
		}
	}

	/**
	 * 写入一个int64数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeInt64List(final int tag, final List<Long> values, final boolean packet) {
		// 自压缩的方式写入
		if (packet) {
			flushIfNotAvailable(MAX_VARINT_SIZE * (2 + values.size()));
			bufferUInt32NoTag(tag);
			bufferUInt32NoTag(computeInt64Size(values));// 计算长度
			for (Long value : values) {
				bufferUInt64NoTag(value);
			}
		} else {
			flushIfNotAvailable(MAX_VARINT_SIZE * (2 * values.size()));
			for (Long value : values) {
				bufferUInt32NoTag(tag);
				bufferUInt64NoTag(value);
			}
		}
	}

	/**
	 * 写入一个sint64数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeSInt64List(final int tag, final List<Long> values, final boolean packet) {
		if (packet) {
			flushIfNotAvailable(MAX_VARINT_SIZE * (2 + values.size()));
			bufferUInt32NoTag(tag);
			bufferUInt32NoTag(computeSInt64Size(values));// 计算长度
			for (Long value : values) {
				bufferUInt64NoTag(encodeZigZag64(value));
			}
		} else {
			flushIfNotAvailable(MAX_VARINT_SIZE * (2 * values.size()));
			for (Long value : values) {
				bufferUInt32NoTag(tag);
				bufferUInt64NoTag(encodeZigZag64(value));
			}
		}
	}

	/**
	 * 写入一个fixed64数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeFixed64List(final int tag, final List<Long> values, final boolean packet) {
		if (packet) {
			flushIfNotAvailable(MAX_VARINT_SIZE * 2 + FIXED_64_SIZE * values.size());
			bufferUInt32NoTag(tag);
			bufferUInt32NoTag(FIXED_64_SIZE * values.size()); // 计算长度
			for (Long value : values) {
				bufferFixed64NoTag(value);
			}
		} else {
			flushIfNotAvailable((MAX_VARINT_SIZE + FIXED_64_SIZE) * values.size());
			for (Long value : values) {
				bufferUInt32NoTag(tag);
				bufferFixed64NoTag(value);
			}
		}
	}

	/**
	 * 写入一个string数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeStringList(int tag, List<String> values, boolean packet) {
		if (packet) {
			int lenght = 0;
			final List<byte[]> vs = new LinkedList<>();
			for (String value : values) {
				final byte[] bytes = value.getBytes(DEFAULT_STRING_CHARSET);
				lenght += bytes.length;
				vs.add(bytes);
			}

			flushIfNotAvailable(MAX_VARINT_SIZE + MAX_VARINT_SIZE);
			bufferUInt32NoTag(tag);
			bufferUInt32NoTag(lenght);// 全部长度

			for (byte[] value : vs) {
				flushIfNotAvailable(MAX_VARINT_SIZE + value.length);
				bufferByteArrayNoTag(value);
			}
		} else {
			for (String value : values) {
				this.writeString(tag, value);
			}
		}
	}

	/**
	 * 写入一个PB对象数组.
	 * 
	 * @param tag 计算好的Tag值
	 * @param values 数组
	 * @param packet 是否压缩编码
	 */
	public void writeMessageList(int tag, List<? extends ProtobufSerializable> values, boolean packet) {
		if (packet) {
			flushIfNotAvailable(MAX_VARINT_SIZE * 2);
			bufferUInt32NoTag(tag);

			bufferMakeLengthIndex();// 标识时已让出一个位置啦...
			for (ProtobufSerializable value : values) {
				value.writeTo(this);
			}
			bufferWriteLength();// 真实长度写进去.

		} else {
			for (ProtobufSerializable value : values) {
				this.writeMessage(tag, value);
			}
		}
	}

	// 计算int32编码的大小.
	private int computeInt32Size(List<Integer> values) {
		int sum = 0;
		for (Integer value : values) {
			sum += computeInt32SizeNoTag(value.intValue());
		}
		return sum;
	}

	// 计算sint32编码的大小.
	private int computeSInt32Size(List<Integer> values) {
		int sum = 0;
		for (Integer value : values) {
			sum += computeInt32SizeNoTag(encodeZigZag32(value.intValue()));
		}
		return sum;
	}

	/**
	 * Compute the number of bytes that would be needed to encode an
	 * {@code int32} field, including tag.
	 */
	protected static int computeInt32SizeNoTag(final int value) {
		if (value >= 0) {
			return computeUInt32SizeNoTag(value);
		} else {
			// Must sign-extend.
			return MAX_VARINT_SIZE;
		}
	}

	/**
	 * Compute the number of bytes that would be needed to encode a
	 * {@code uint32} field.
	 */
	private static int computeUInt32SizeNoTag(final int value) {
		if ((value & (~0 << 7)) == 0) {
			return 1;
		}
		if ((value & (~0 << 14)) == 0) {
			return 2;
		}
		if ((value & (~0 << 21)) == 0) {
			return 3;
		}
		if ((value & (~0 << 28)) == 0) {
			return 4;
		}
		return 5;
	}

	// 计算int64编码的大小.
	private int computeInt64Size(List<Long> values) {
		int sum = 0;
		for (Long value : values) {
			sum += computeInt64SizeNoTag(value.longValue());
		}
		return sum;
	}

	// 计算sint64编码的大小.
	private int computeSInt64Size(List<Long> values) {
		int sum = 0;
		for (Long value : values) {
			sum += computeInt64SizeNoTag(encodeZigZag64(value.longValue()));
		}
		return sum;
	}

	/**
	 * Compute the number of bytes that would be needed to encode an
	 * {@code int64} field, including tag.
	 */
	private static int computeInt64SizeNoTag(final long value) {
		return computeUInt64SizeNoTag(value);
	}

	/**
	 * Compute the number of bytes that would be needed to encode a
	 * {@code uint64} field, including tag.
	 */
	private static int computeUInt64SizeNoTag(long value) {
		// handle two popular special cases up front ...
		if ((value & (~0L << 7)) == 0L) {
			return 1;
		}
		if (value < 0L) {
			return 10;
		}
		// ... leaving us with 8 remaining, which we can divide and conquer
		int n = 2;
		if ((value & (~0L << 35)) != 0L) {
			n += 4;
			value >>>= 28;
		}
		if ((value & (~0L << 21)) != 0L) {
			n += 2;
			value >>>= 14;
		}
		if ((value & (~0L << 14)) != 0L) {
			n += 1;
		}
		return n;
	}

	public abstract byte[] getByteArray();

	/**
	 * 标记当前长度写入位置.
	 */
	protected abstract void bufferMakeLengthIndex();

	protected abstract void bufferWriteLength();

	protected abstract void flushIfNotAvailable(int minCapacity);

	// 原生态...
	protected abstract void buffer(final byte b);

	protected abstract void bufferInt32NoTag(final int value);

	protected abstract void bufferUInt64NoTag(final long value);

	protected abstract void bufferUInt32NoTag(final int tag);

	protected abstract void bufferByteArrayNoTag(final byte[] bytes);

	protected abstract void bufferFixed32NoTag(final int value);

	protected abstract void bufferFixed64NoTag(final long value);

	public abstract void close();
}