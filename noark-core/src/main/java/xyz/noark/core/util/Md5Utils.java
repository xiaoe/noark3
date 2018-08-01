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

import java.security.MessageDigest;

/**
 * Md5工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class Md5Utils {
	private static final int STR_LENGTH = 16;
	/** 十六进制的字典 */
	private final static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 以MD5的方式加密一段文本.
	 * 
	 * @param text 一段文本
	 * @return 加密后的结果
	 */
	public static final String encrypt(String text) {
		if (text == null) {
			return "";
		}
		try {
			byte[] source = text.getBytes("UTF-8");
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(source);
			byte[] tmp = messageDigest.digest();
			char str[] = new char[16 * 2];
			for (int i = 0, k = 0; i < STR_LENGTH; i++) {
				byte byte0 = tmp[i];
				str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
				str[k++] = HEX_DIGITS[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {}
		return "";
	}
}