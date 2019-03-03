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

import java.util.Arrays;

/**
 * 数组相关操作工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class ArrayUtils {
	/**
	 * 一个空的字符串数组.
	 */
	public static final String[] EMPTY_STRING_ARRAY = {};

	/**
	 * <p>
	 * Adds all the elements of the given arrays into a new array.
	 * </p>
	 * <p>
	 * The new array contains all of the element of {@code array1} followed by
	 * all of the elements {@code array2}. When an array is returned, it is
	 * always a new array.
	 * </p>
	 *
	 * <pre>
	 * ArrayUtils.addAll([], [])         = []
	 * </pre>
	 *
	 * @param array1 the first array whose elements are added to the new array.
	 * @param array2 the second array whose elements are added to the new array.
	 * @return The new long[] array.
	 */
	public static long[] addAll(final long[] array1, final long... array2) {
		final long[] joinedArray = new long[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}

	/**
	 * 判定Object数组是否为空(null或长度为0).
	 * 
	 * @param array Object数组
	 * @return 如果为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isEmpty(final Object[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定Object数组是否不为空(null或长度为0).
	 * 
	 * @param array Object数组
	 * @return 如果不为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final Object[] array) {
		return !isEmpty(array);
	}

	/**
	 * 判定boolean数组是否为空(null或长度为0).
	 * 
	 * @param array boolean数组
	 * @return 如果为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isEmpty(final boolean[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定boolean数组是否不为空(null或长度为0).
	 * 
	 * @param array boolean数组
	 * @return 如果不为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final boolean[] array) {
		return !isEmpty(array);
	}

	/**
	 * 判定int数组是否为空(null或长度为0).
	 * 
	 * @param array int数组
	 * @return 如果为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isEmpty(final int[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定int数组是否不为空(null或长度为0).
	 * 
	 * @param array int数组
	 * @return 如果不为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final int[] array) {
		return !isEmpty(array);
	}

	/**
	 * 判定long数组是否为空(null或长度为0).
	 * 
	 * @param array long数组
	 * @return 如果为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isEmpty(final long[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定long数组是否不为空(null或长度为0).
	 * 
	 * @param array long数组
	 * @return 如果不为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final long[] array) {
		return !isEmpty(array);
	}

	/**
	 * 判定float数组是否为空(null或长度为0).
	 * 
	 * @param array float数组
	 * @return 如果为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isEmpty(final float[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定float数组是否不为空(null或长度为0).
	 * 
	 * @param array float数组
	 * @return 如果不为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final float[] array) {
		return !isEmpty(array);
	}

	/**
	 * 判定double数组是否为空(null或长度为0).
	 * 
	 * @param array double数组
	 * @return 如果为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isEmpty(final double[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定double数组是否不为空(null或长度为0).
	 * 
	 * @param array double数组
	 * @return 如果不为空(null或长度为0)则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final double[] array) {
		return !isEmpty(array);
	}

	/**
	 * String数组转化为int数组
	 * 
	 * @param array String数组
	 * @return int数组
	 */
	public static int[] toIntArray(String[] array) {
		return Arrays.stream(array).mapToInt(s -> Integer.parseInt(s)).toArray();
	}

	/**
	 * Integer数组转化为int数组
	 * 
	 * @param array Integer数组
	 * @return int数组
	 */
	public static int[] toIntArray(Integer[] array) {
		return Arrays.stream(array).mapToInt(Integer::intValue).toArray();
	}

	/**
	 * String数组转化为long数组
	 * 
	 * @param array String数组
	 * @return long数组
	 */
	public static long[] toLongArray(String[] array) {
		return Arrays.stream(array).mapToLong(s -> Long.parseLong(s)).toArray();
	}

	/**
	 * Long数组转化为long数组
	 * 
	 * @param array Long数组
	 * @return long数组
	 */
	public static long[] toLongArray(Long[] array) {
		return Arrays.stream(array).mapToLong(Long::longValue).toArray();
	}

	/**
	 * 字符串组数转化为字节数组.
	 * <p>
	 * 默认使用10进制解析
	 * 
	 * @param array 字符串组数
	 * @return 转化后的字节数组
	 */
	public static byte[] toByteArray(String[] array) {
		return toByteArray(array, 10);
	}

	/**
	 * 字符串组数转化为字节数组.
	 * 
	 * @param array 字符串组数
	 * @param radix 角色字符串数组{@code array}时所用的进制
	 * @return 转化后的字节数组
	 */
	public static byte[] toByteArray(String[] array, int radix) {
		final byte[] data = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			data[i] = (byte) Integer.parseInt(array[i], radix);
		}
		return data;
	}
}