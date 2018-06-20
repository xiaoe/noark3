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
package xyz.noark.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class StringUtils {

	/**
	 * The empty String {@code ""}.
	 */
	public static final String EMPTY = "";

	/**
	 * 一个空字符串数组.
	 */
	public static final String[] EMPTY_STRING_ARRAY = {};

	/**
	 * <p>
	 * 检测字符串是否为 null或"".
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("test")     = false
	 * StringUtils.isEmpty("  test  ") = false
	 * </pre>
	 *
	 * @param text 被检测字符串
	 * @return 如果字符为null或""则返回true,否则返回false.
	 */
	public static boolean isEmpty(final String text) {
		return text == null || text.length() == 0;
	}

	/**
	 * <p>
	 * 检测字符串是否不为 null且不为"".
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isNotEmpty(null)      = false
	 * StringUtils.isNotEmpty("")        = false
	 * StringUtils.isNotEmpty(" ")       = true
	 * StringUtils.isNotEmpty("test")    = true
	 * StringUtils.isNotEmpty("  test ") = true
	 * </pre>
	 *
	 * @param text 被检测字符串
	 * @return 如果字符不为 null且不为""则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final String text) {
		return !isEmpty(text);
	}

	/**
	 * 检测一个字符串长度.
	 * <p>
	 * 字符串有可能包含中文等其他文字，中文应该算2个长度.
	 * 
	 * @param text 被检测字符串
	 * @return 字符串长度
	 */
	public static int length(final String text) {
		if (text == null) {
			return 0;
		}

		int sum = 0;
		for (int i = 0, len = text.length(); i < len; i++) {
			sum += text.charAt(i) > 127 ? 2 : 1;
		}
		return sum;
	}

	/**
	 * <p>
	 * Splits the provided text into an array, separators specified. This is an
	 * alternative to using StringTokenizer.
	 * </p>
	 *
	 * <p>
	 * The separator is not included in the returned String array. Adjacent
	 * separators are treated as one separator. For more control over the split
	 * use the StrTokenizer class.
	 * </p>
	 *
	 * <p>
	 * A {@code null} input String returns {@code null}. A {@code null}
	 * separatorChars splits on whitespace.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.split(null, *)         = null
	 * StringUtils.split("", *)           = []
	 * StringUtils.split("abc def", null) = ["abc", "def"]
	 * StringUtils.split("abc def", " ")  = ["abc", "def"]
	 * StringUtils.split("abc  def", " ") = ["abc", "def"]
	 * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
	 * </pre>
	 *
	 * @param str the String to parse, may be null
	 * @param separatorChars the characters used as the delimiters, {@code null}
	 *            splits on whitespace
	 * @return an array of parsed Strings, {@code null} if null String input
	 */
	public static String[] split(final String str, final String separatorChars) {
		return splitWorker(str, separatorChars, -1, false);
	}

	/**
	 * Performs the logic for the {@code split} and
	 * {@code splitPreserveAllTokens} methods that return a maximum array
	 * length.
	 *
	 * @param str the String to parse, may be {@code null}
	 * @param separatorChars the separate character
	 * @param max the maximum number of elements to include in the array. A zero
	 *            or negative value implies no limit.
	 * @param preserveAllTokens if {@code true}, adjacent separators are treated
	 *            as empty token separators; if {@code false}, adjacent
	 *            separators are treated as one separator.
	 * @return an array of parsed Strings, {@code null} if null String input
	 */
	private static String[] splitWorker(final String str, final String separatorChars, final int max, final boolean preserveAllTokens) {
		if (str == null) {
			return EMPTY_STRING_ARRAY;
		}
		final int len = str.length();
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		final List<String> list = new ArrayList<String>();
		int sizePlus1 = 1;
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;
		if (separatorChars == null) {
			// Null separator means use whitespace
			while (i < len) {
				if (Character.isWhitespace(str.charAt(i))) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else if (separatorChars.length() == 1) {
			// Optimise 1 character case
			final char sep = separatorChars.charAt(0);
			while (i < len) {
				if (str.charAt(i) == sep) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else {
			// standard case
			while (i < len) {
				if (separatorChars.indexOf(str.charAt(i)) >= 0) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		}
		if (match || preserveAllTokens && lastMatch) {
			list.add(str.substring(start, i));
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * 将一个字符串由驼峰式命名变成分割符分隔单词
	 * 
	 * <pre>
	 *  lowerWord("helloWorld", '_') => "hello_world"
	 * </pre>
	 * 
	 * @param cs 字符串
	 * @param c 分隔符
	 * 
	 * @return 转换后字符串
	 */
	public static String lowerWord(CharSequence cs, char c) {
		int len = cs.length();
		StringBuilder sb = new StringBuilder(len + 5);
		for (int i = 0; i < len; i++) {
			char ch = cs.charAt(i);
			if (Character.isUpperCase(ch)) {
				if (i > 0)
					sb.append(c);
				sb.append(Character.toLowerCase(ch));
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}
}