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

import java.lang.management.ManagementFactory;

/**
 * 系统相关工具类.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public class SystemUtils {

	/**
	 * The {@code os.name} System Property. Operating system name.
	 * 
	 * @since Java 1.1
	 */
	public static final String OS_NAME = System.getProperty("os.name");

	/**
	 * 如果当前系统为Linux则返回{@code true}.
	 */
	public static final boolean IS_OS_LINUX = getOsMatchesName("linux");

	/**
	 * 如果当前系统为Windows则返回{@code true}.
	 */
	public static final boolean IS_OS_WINDOWS = getOsMatchesName("win");

	/**
	 * 对系统名称匹配结果.
	 * <p>
	 * 忽略大小写噢...
	 *
	 * @param osNamePrefix 匹配前缀
	 * @return 如果匹配返回true,否则返回false.
	 */
	private static boolean getOsMatchesName(final String osNamePrefix) {
		return isOsNameMatch(OS_NAME, osNamePrefix);
	}

	/**
	 * 对系统名称匹配结果.
	 * <p>
	 * 忽略大小写噢...
	 *
	 * @param osName 系统名称
	 * @param osNamePrefix 匹配前缀
	 * @return 如果匹配返回true,否则返回false.
	 */
	private static boolean isOsNameMatch(final String osName, final String osNamePrefix) {
		if (StringUtils.isEmpty(osName)) {
			return false;
		}
		return osName.toLowerCase().startsWith(osNamePrefix);
	}

	/**
	 * 获取当前进程的PID.
	 * 
	 * @return 当前进程的PID
	 */
	public static long getPid() {
		return Long.parseLong(getPidStr());
	}

	/**
	 * 获取当前进程的PID字符串.
	 * 
	 * @return 当前进程的PID字符串
	 */
	public static String getPidStr() {
		return ManagementFactory.getRuntimeMXBean().getName().split("@", 2)[0];
	}
}