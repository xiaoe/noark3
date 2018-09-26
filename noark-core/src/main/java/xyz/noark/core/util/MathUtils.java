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
 * 数学计算相关的工具类库.
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
public class MathUtils {

	/**
	 * 计算两个参数的和，如果相加出现溢出那就返回{@code int}的最大值.
	 * <p>
	 * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
	 * 
	 * @param x 第一个参数
	 * @param y 第二个参数
	 * @return 两个参数的和
	 * @see Math#addExact(int, int)
	 */
	public static int addExact(int x, int y) {
		try {
			return Math.addExact(x, y);
		} catch (ArithmeticException e) {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * 计算两个参数的和，如果相加出现溢出那就返回{@code long}的最大值.
	 * <p>
	 * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
	 * 
	 * @param x 第一个参数
	 * @param y 第二个参数
	 * @return 两个参数的和
	 * @see Math#addExact(long, long)
	 */
	public static long addExact(long x, long y) {
		try {
			return Math.addExact(x, y);
		} catch (ArithmeticException e) {
			return Long.MAX_VALUE;
		}
	}

	/**
	 * 计算两个参数的乘积，如果相乘出现溢出那就返回{@code int}的最大值.
	 * <p>
	 * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
	 * 
	 * @param x 第一个参数
	 * @param y 第二个参数
	 * @return 两个参数的乘积
	 * @see Math#multiplyExact(int, int)
	 */
	public static int multiplyExact(int x, int y) {
		try {
			return Math.multiplyExact(x, y);
		} catch (ArithmeticException e) {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * 计算两个参数的乘积，如果相乘出现溢出那就返回{@code long}的最大值.
	 * <p>
	 * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
	 * 
	 * @param x 第一个参数
	 * @param y 第二个参数
	 * @return 两个参数的乘积
	 * @see Math#multiplyExact(long, long)
	 */
	public static long multiplyExact(long x, long y) {
		try {
			return Math.multiplyExact(x, y);
		} catch (ArithmeticException e) {
			return Long.MAX_VALUE;
		}
	}
}