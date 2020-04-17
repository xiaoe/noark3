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
package xyz.noark.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

/**
 * 消息辅助类的测试用例.
 *
 * @since 3.4
 * @author 小流氓[176543888@qq.com]
 */
@ThreadSafe
public class MessageHelperTest {
	private RuntimeException exception = new RuntimeException("测试异常");
	private Date now = new Date();

	@Before
	public void setUp() throws Exception {}

	@Test
	public void testPreprocessingEnteringLogThreadBefore() {
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(null), null);
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(1), Integer.valueOf(1));

		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(now), now);
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore("123"), "123");

		Character character = new Character('!');
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(character), character);

		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(exception), exception);

		MessageHelperTest test = new MessageHelperTest();
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(test), test);

		byte[] array = new byte[] { 1, 2, 3 };
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(array), "[1, 2, 3]");

		short[] shortArray = new short[] { 1, 2, 3 };
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(shortArray), "[1, 2, 3]");

		int[] intArray = new int[] { 1, 2, 3 };
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(intArray), "[1, 2, 3]");

		long[] longArray = new long[] { 1, 2, 3 };
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(longArray), "[1, 2, 3]");

		float[] floatArray = new float[] { 1, 2, 3 };
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(floatArray), "[1.0, 2.0, 3.0]");

		double[] doubleArray = new double[] { 1, 2, 3 };
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(doubleArray), "[1.0, 2.0, 3.0]");

		String[] stringArray = new String[] { "1", "2", "3" };
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(stringArray), "[1, 2, 3]");

		Object[] objectArray = new Object[] { "1", 2, "3" };
		assertEquals(MessageHelper.preprocessingEnteringLogThreadBefore(objectArray), "[1, 2, 3]");
	}

	@Test
	public void testAppend() {
		StringBuilder sb = new StringBuilder();
		MessageHelper.append(sb, null);
		assertEquals(sb.toString(), "null");
		sb.setLength(0);

		MessageHelper.append(sb, exception);
		assertNotEquals(sb.toString(), "null");
		sb.setLength(0);

		SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		MessageHelper.append(sb, now);
		assertEquals(sb.toString(), pattern.format(now));
		sb.setLength(0);

		MessageHelper.append(sb, "123");
		assertEquals(sb.toString(), "123");
		sb.setLength(0);

		RuntimeException exception = new RuntimeException("测试异常") {
			private static final long serialVersionUID = 1L;

			@Override
			public void printStackTrace(PrintWriter s) {
				super.printStackTrace(s);
				throw new RuntimeException("假装他会出异常");
			}
		};
		MessageHelper.append(sb, exception);
		// xyz.noark.log.MessageHelperTest$1: 测试异常
		assertNotEquals(sb.toString(), "null");
		sb.setLength(0);
	}
}
