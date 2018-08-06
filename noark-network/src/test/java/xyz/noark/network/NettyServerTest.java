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
package xyz.noark.network;

import java.net.Socket;

import org.junit.BeforeClass;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xyz.noark.benchmark.Benchmark;

/**
 * Netty服务器测试例.
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public class NettyServerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@Test
	public void test() throws Exception {
		Socket socket = new Socket("127.0.0.1", 9527);
		socket.getOutputStream().write("socket".getBytes());
		socket.getOutputStream().flush();
		Thread.sleep(1000);
		socket.close();
	}

	private final Benchmark benchmark = new Benchmark(1000_0000);

	@Test
	public void testBenchmark() throws Exception {
		final byte[] bytes = new byte[] { 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7,
				8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6,
				7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		benchmark.doSomething("testByteBuf:", () -> testByteBuf(Unpooled.wrappedBuffer(bytes.clone())));
		benchmark.doSomething("testByteArray:", () -> testByteArray(bytes.clone()));
	}

	private void testByteArray(byte[] bytes) {
		for (int i = 0, len = bytes.length; i < len; i++) {
			bytes[i] = (byte) (bytes[i] * 2 - 1);
			bytes[i] = (byte) (bytes[i] * 2 - 2);
			bytes[i] = (byte) (bytes[i] * 2 - 3);
		}
	}

	private void testByteBuf(ByteBuf bytes) {
		for (int i = 0, len = bytes.readableBytes(); i < len; i++) {
			bytes.setByte(i, (byte) (bytes.getByte(i) * 2 - 1));
			bytes.setByte(i, (byte) (bytes.getByte(i) * 2 - 2));
			bytes.setByte(i, (byte) (bytes.getByte(i) * 2 - 3));
		}
	}
}
