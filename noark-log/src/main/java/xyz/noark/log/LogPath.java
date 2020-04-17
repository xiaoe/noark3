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
 * 日志存储路径.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
class LogPath {
	private final String path;
	private final boolean activate;
	private DateTimeFormatter formatter = null;
	private String prefix;
	private String suffix;

	LogPath() {
		this(null);
	}

	LogPath(String path) {
		this.path = path;
		this.activate = (path == null || "".equals(path)) ? false : true;

		if (activate) {
			int start = path.indexOf("{");
			int end = path.lastIndexOf("}");

			// 有配置日期格式后缀
			if (start > 0 && end > start) {
				String dateFormat = path.substring(start + 1, end);
				this.formatter = DateTimeFormatter.ofPattern(dateFormat);

				this.prefix = path.substring(0, start);
				this.suffix = path.substring(end + 1);
			}
		}
	}

	/**
	 * 计算当前日志时间所对应的文件路径.
	 * 
	 * @param date 日志时间
	 * @return 日志文件路径
	 */
	public String getPath(LocalDateTime date) {
		if (formatter == null) {
			return path;
		}
		return new StringBuilder(path.length()).append(prefix).append(date.format(formatter)).append(suffix).toString();
	}

	/**
	 * 日志存储文件功能是否激活.
	 * 
	 * @return 日志存储文件功能是否激活
	 */
	public boolean isActivate() {
		return activate;
	}
}