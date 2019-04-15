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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志消息的一个抽象类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
abstract class AbstractMessage implements Message {
	// 单线程不需要ThreadLocal来保护...
	/** 拼接日志所用的缓存区 */
	private static final StringBuilder DEFAULT_LOG_BUILDER = new StringBuilder(512);
	/** 日志中输出的时间格式 */
	private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private final Level level;
	protected final String msg;
	protected final LocalDateTime date;
	private final String threadName;
	private String fileName;
	private int lineNumber;

	AbstractMessage(Level level, String msg) {
		this.level = level;
		this.msg = msg;
		this.date = LocalDateTime.now();

		final Thread thread = Thread.currentThread();
		this.threadName = thread.getName();

		if (LogConfigurator.DEFAULT_LEVEL == Level.DEBUG) {
			StackTraceElement[] elements = thread.getStackTrace();
			for (int i = 7; i < elements.length; i++) {
				StackTraceElement stackTraceElement = elements[i];
				fileName = stackTraceElement.getFileName();
				lineNumber = stackTraceElement.getLineNumber();
				if (fileName != null && !fileName.endsWith("Logger.java")) {
					break;
				}
			}
		}
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public LocalDateTime getDate() {
		return date;
	}

	@Override
	public String build() {
		DEFAULT_LOG_BUILDER.setLength(0);

		// 2017-11-11 19:59:42.538 [main] INFO Test.java:18 - test
		DEFAULT_LOG_BUILDER.append(DEFAULT_DATE_FORMATTER.format(date));
		// 线程名称+输出级别
		DEFAULT_LOG_BUILDER.append(" [").append(threadName).append("] ").append(level);
		// Debug状态，输出线程等细节信息
		if (LogConfigurator.DEFAULT_LEVEL == Level.DEBUG) {
			DEFAULT_LOG_BUILDER.append(" ").append(fileName).append(":").append(lineNumber);
		}
		DEFAULT_LOG_BUILDER.append(" - ");

		this.onBuildMessage(DEFAULT_LOG_BUILDER);

		DEFAULT_LOG_BUILDER.append("\n");
		return DEFAULT_LOG_BUILDER.toString();
	}

	/**
	 * 构建日志内容
	 * 
	 * @param sb StringBuilder对象.
	 */
	protected abstract void onBuildMessage(StringBuilder sb);
}