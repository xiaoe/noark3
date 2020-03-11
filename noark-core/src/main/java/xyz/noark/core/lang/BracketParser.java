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
package xyz.noark.core.lang;

import xyz.noark.core.exception.IllegalExpressionException;

/**
 * 括号解析器.
 * <p>
 * 一种字符串表达式辅助解析工具类.<br>
 * 用于括号类分隔的情况.
 *
 * @since 3.4
 * @author 小流氓(176543888@qq.com)
 */
public class BracketParser {
	/** 表达式 */
	private final String expression;
	/** 访问游标 */
	private int index = 0;
	private final StringBuilder sb;

	public BracketParser(String expression) {
		this.expression = expression;
		this.sb = new StringBuilder(expression.length() / 2);
	}

	/**
	 * 读出一个括号内的值,
	 * 
	 * @return 括号内的值
	 */
	public String readString() {
		// 先要读出一个左中括号
		char cur = expression.charAt(index++);
		if (cur != CharUtils.LBRACKET) {
			throw new IllegalExpressionException("....");
		}

		// 缓存清0
		sb.setLength(0);
		do {
			cur = expression.charAt(index++);
			// 结束啦
			if (cur == CharUtils.RBRACKET) {
				break;
			}

			sb.append(cur);
		} while (expression.length() > index);
		return sb.toString();
	}
}