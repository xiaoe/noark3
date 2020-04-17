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
 * 无符号类型计算工具类.
 * <p>
 * 在Java中，不存在Unsigned无符号数据类型，但我们有工具类帮我们做一个简单的计算...
 *
 * @since 3.2
 * @author 小流氓[176543888@qq.com]
 */
public class UnsignedUtils {
	/**
	 * 将byte类型的数字转化为无符号的数字.
	 * <p>
	 * 区间[0~255]
	 * 
	 * @param num 数字
	 * @return 转化后所对应无符号数字
	 */
	public static int toUnsigned(byte num) {
		return num & 0xFF;
	}

	/**
	 * 将short类型的数字转化为无符号的数字.
	 * <p>
	 * 区间[0~65535]
	 * 
	 * @param num 数字
	 * @return 转化后所对应无符号数字
	 */
	public static int toUnsigned(short num) {
		return num & 0xFFFF;
	}

	/**
	 * 将int类型的数字转化为无符号的数字.
	 * <p>
	 * 区间[0~4294967295]
	 * 
	 * @param num 数字
	 * @return 转化后所对应无符号数字
	 */
	public static long toUnsigned(int num) {
		return num & 0xFFFFFFFFL;
	}
}