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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * 消息分析器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
class MessageAnalyzer {
	private static final char DELIM_START = '{';
	private static final char DELIM_STOP = '}';
	// private static final char ESCAPE_CHAR = '\\';

	private ArrayList<Placeholder> caches = new ArrayList<>();
	private int count = 0;// 占位符个数

	MessageAnalyzer(String key) {
		init(key);
		this.caches.trimToSize();
	}

	private void init(String messagePattern) {
		int beginIndex = 0;
		int i = 0;
		int index = 0;
		final int len = messagePattern.length();
		for (; i < len - 1; i++) { // 最后的字符被排除在循环之外
			final char curChar = messagePattern.charAt(i);
			if (isDelimPair(curChar, messagePattern, i)) {

				if (beginIndex != i) {
					caches.add(new StrPlaceholder(messagePattern.substring(beginIndex, i)));
				}

				count++;
				caches.add(new ObjPlaceholder(index++));

				beginIndex = i + 2;
				i++;
			}
		}
		if (beginIndex <= i && i < len) {
			caches.add(new StrPlaceholder(messagePattern.substring(beginIndex, i + 1)));
		}
	}

	/**
	 * Returns {@code true} if the specified char and the char at
	 * {@code curCharIndex + 1} in the specified message pattern together form a
	 * "{}" delimiter pair, returns {@code false} otherwise.
	 */
	private static boolean isDelimPair(final char curChar, final String messagePattern, final int curCharIndex) {
		return curChar == DELIM_START && messagePattern.charAt(curCharIndex + 1) == DELIM_STOP;
	}

	public void build(StringBuilder sb, Object[] args) {
		for (Placeholder object : caches) {
			object.build(sb, args);
		}

		// 如果参数比占位符多的话，也要输出.
		if (args.length > count) {
			for (int i = count; i < args.length; i++) {
				append(sb, ",{");
				append(sb, i);
				append(sb, "}=");
				append(sb, args[i]);
			}
		}
	}

	@Override
	public String toString() {
		return "MessageAnalyzer [caches=" + caches + "]";
	}

	interface Placeholder {
		void build(StringBuilder sb, Object[] args);
	}

	class StrPlaceholder implements Placeholder {
		private String str;

		public StrPlaceholder(String str) {
			this.str = str;
		}

		@Override
		public void build(StringBuilder sb, Object[] args) {
			sb.append(str);
		}
	}

	class ObjPlaceholder implements Placeholder {
		private int index;

		public ObjPlaceholder(int index) {
			this.index = index;
		}

		@Override
		public void build(StringBuilder sb, Object[] args) {
			if (args.length > index) {
				append(sb, args[index]);
			} else {
				sb.append("{}");
			}
		}
	}

	private void append(StringBuilder sb, Object object) {
		if (object instanceof Throwable) {
			try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
				((Throwable) object).printStackTrace(pw);
				sb.append("\n").append(sw.toString());
			} catch (Exception e) {
				sb.append(object);
			}
		} else {
			sb.append(object);
		}
	}
}