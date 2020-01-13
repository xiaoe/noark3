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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 * GZip测试用例
 *
 * @since 3.4
 * @author 小流氓(176543888@qq.com)
 */
public class GzipUtilsTest {

	private final String data = "1234567890~!@#$%^&*()_+ABCDefghijk中文";
	private final String base64 = "H4sIAAAAAAAAADM0MjYxNTO3sDSoU3RQVlGNU9PS0IzXdnRydklNS8/IzMp+smPts2ntAKeOV94oAAAA";

	@Test
	public void testCompressByteArray() throws IOException {
		byte[] result = GzipUtils.compress(data.getBytes(CharsetUtils.CHARSET_UTF_8));
		assertTrue(base64.equals(new String(Base64Utils.encode(result), CharsetUtils.CHARSET_UTF_8)));
	}

	@Test
	public void testUncompressByteArray() throws IOException {
		byte[] result = GzipUtils.uncompress(Base64Utils.decode(base64));
		assertTrue(data.equals(new String(result, CharsetUtils.CHARSET_UTF_8)));
	}
}