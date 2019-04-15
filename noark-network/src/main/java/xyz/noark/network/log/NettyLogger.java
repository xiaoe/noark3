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
package xyz.noark.network.log;

import static xyz.noark.log.LogHelper.logger;

import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;

/**
 * Netty的日志实现.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
class NettyLogger implements InternalLogger {
	NettyLogger() {}

	@Override
	public String name() {
		return "netty-logger";
	}

	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	@Override
	public void trace(String msg) {
		this.debug(msg);
	}

	@Override
	public void trace(String format, Object arg) {
		this.debug(format, arg);
	}

	@Override
	public void trace(String format, Object argA, Object argB) {
		this.debug(format, argA, argB);
	}

	@Override
	public void trace(String format, Object... arguments) {
		this.debug(format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		this.debug(msg, t);
	}

	@Override
	public void trace(Throwable t) {
		this.debug(t);
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void debug(String msg) {
		logger.debug(msg);
	}

	@Override
	public void debug(String format, Object arg) {
		logger.debug(format, arg);
	}

	@Override
	public void debug(String format, Object argA, Object argB) {
		logger.debug(format, argA, argB);
	}

	@Override
	public void debug(String format, Object... arguments) {
		logger.debug(format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		logger.debug(msg, t);
	}

	@Override
	public void debug(Throwable t) {
		logger.debug("{}", t);
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(String msg) {
		logger.info(msg);
	}

	@Override
	public void info(String format, Object arg) {
		logger.info(format, arg);
	}

	@Override
	public void info(String format, Object argA, Object argB) {
		logger.info(format, argA, argB);
	}

	@Override
	public void info(String format, Object... arguments) {
		logger.info(format, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		logger.info(msg, t);
	}

	@Override
	public void info(Throwable t) {
		logger.info("{}", t);
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(String msg) {
		logger.warn(msg);
	}

	@Override
	public void warn(String format, Object arg) {
		logger.warn(format, arg);
	}

	@Override
	public void warn(String format, Object... arguments) {
		logger.warn(format, arguments);
	}

	@Override
	public void warn(String format, Object argA, Object argB) {
		logger.warn(format, argA, argB);
	}

	@Override
	public void warn(String msg, Throwable t) {
		logger.warn(msg, t);
	}

	@Override
	public void warn(Throwable t) {
		logger.warn("{}", t);
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public void error(String format, Object arg) {
		logger.error(format, arg);
	}

	@Override
	public void error(String format, Object argA, Object argB) {
		logger.error(format, argA, argB);
	}

	@Override
	public void error(String format, Object... arguments) {
		logger.error(format, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		logger.error(msg, t);
	}

	@Override
	public void error(Throwable t) {
		logger.error("{}", t);
	}

	@Override
	public boolean isEnabled(InternalLogLevel level) {
		return true;
	}

	@Override
	public void log(InternalLogLevel level, String msg) {
		switch (level) {
		case TRACE:
		case DEBUG:
			this.debug(msg);
			break;
		case INFO:
			this.info(msg);
			break;
		case WARN:
			this.warn(msg);
			break;
		case ERROR:
		default:
			this.error(msg);
			break;
		}
	}

	@Override
	public void log(InternalLogLevel level, String format, Object arg) {
		switch (level) {
		case TRACE:
		case DEBUG:
			this.debug(format, arg);
			break;
		case INFO:
			this.info(format, arg);
			break;
		case WARN:
			this.warn(format, arg);
			break;
		case ERROR:
		default:
			this.error(format, arg);
			break;
		}
	}

	@Override
	public void log(InternalLogLevel level, String format, Object argA, Object argB) {
		switch (level) {
		case TRACE:
		case DEBUG:
			this.debug(format, argA, argB);
			break;
		case INFO:
			this.info(format, argA, argB);
			break;
		case WARN:
			this.warn(format, argA, argB);
			break;
		case ERROR:
		default:
			this.error(format, argA, argB);
			break;
		}
	}

	@Override
	public void log(InternalLogLevel level, String format, Object... arguments) {
		switch (level) {
		case TRACE:
		case DEBUG:
			this.debug(format, arguments);
			break;
		case INFO:
			this.info(format, arguments);
			break;
		case WARN:
			this.warn(format, arguments);
			break;
		case ERROR:
		default:
			this.error(format, arguments);
			break;
		}
	}

	@Override
	public void log(InternalLogLevel level, String msg, Throwable t) {
		switch (level) {
		case TRACE:
		case DEBUG:
			this.debug(msg, t);
			break;
		case INFO:
			this.info(msg, t);
			break;
		case WARN:
			this.warn(msg, t);
			break;
		case ERROR:
		default:
			this.error(msg, t);
			break;
		}
	}

	@Override
	public void log(InternalLogLevel level, Throwable t) {
		switch (level) {
		case TRACE:
		case DEBUG:
			this.debug(t);
			break;
		case INFO:
			this.info(t);
			break;
		case WARN:
			this.warn(t);
			break;
		case ERROR:
		default:
			this.error(t);
			break;
		}
	}
}