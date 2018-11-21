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

import xyz.noark.core.lang.Point;

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

	/**
	 * 计算两点(x1,y1)到(x2,y2)的距离.
	 * <p>
	 * Math.sqrt(|x1-x2|² + |y1-y2|²)
	 * 
	 * @param x1 坐标X1
	 * @param y1 坐标Y1
	 * @param x2 坐标X2
	 * @param y2 坐标Y2
	 * @return 两点的距离
	 */
	public static double distance(int x1, int y1, int x2, int y2) {
		final double x = Math.abs(x1 - x2);
		final double y = Math.abs(y1 - y2);
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * 计算两点(x1,y1)到(x2,y2)的距离.
	 * <p>
	 * Math.sqrt(|x1-x2|² + |y1-y2|²)
	 * 
	 * @param x1 坐标X1
	 * @param y1 坐标Y1
	 * @param x2 坐标X2
	 * @param y2 坐标Y2
	 * @return 两点的距离
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		final double x = Math.abs(x1 - x2);
		final double y = Math.abs(y1 - y2);
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * 计算两点P1(x1,y1)到P2(x2,y2)的距离.
	 * <p>
	 * Math.sqrt(|x1-x2|² + |y1-y2|²)
	 * 
	 * @param p1 坐标1
	 * @param p2 坐标2
	 * @return 两点的距离
	 */
	public static double distance(Point p1, Point p2) {
		return distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	/**
	 * 判定两点(x1,y1)和(x2,y2)是否相邻.
	 * <p>
	 * 可用于两个AOI是否相邻判定
	 * 
	 * @param x1 坐标X1
	 * @param y1 坐标Y1
	 * @param x2 坐标X2
	 * @param y2 坐标Y2
	 * @return 如果两坐标相邻返回true,否则返回false
	 */
	public boolean adjacent(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1;
	}

	/**
	 * 判定两点P1(x1,y1)和P2(x2,y2)是否相邻.
	 * <p>
	 * 可用于两个AOI是否相邻判定
	 * 
	 * @param p1 坐标1
	 * @param p2 坐标2
	 * @return 如果两坐标相邻返回true,否则返回false
	 */
	public boolean adjacent(Point p1, Point p2) {
		return adjacent(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
}