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

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SVN相关操作工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class SvnUtils {

	/**
	 * 使用SVN更新一个路径.
	 * <p>
	 * 此方法需要提交记录账号密码.
	 * 
	 * @param path 路径
	 * @return 更新结果信息
	 * @throws Exception 如果svn up命令失败会抛出此异常.
	 */
	public static String up(String path) throws Exception {
		return up(path, null, null);
	}

	/**
	 * 使用SVN更新一个路径.
	 * 
	 * @param path 路径
	 * @param username 账号
	 * @param password 密码
	 * @return 更新结果信息
	 * @throws Exception 如果svn up命令失败会抛出此异常.
	 */
	public static String up(String path, String username, String password) throws Exception {
		StringBuilder sb = new StringBuilder(256);
		sb.append("svn up ").append(path);
		return exec(sb.toString(), username, password);
	}

	/**
	 * 使用SVN更新一个路径.
	 * <p>
	 * 此方法需要提交记录账号密码.
	 * 
	 * @param url SVN地址
	 * @param path 路径
	 * @return CheckOut结果
	 * @throws Exception 如果svn up命令失败会抛出此异常.
	 */
	public static String checkout(String url, String path) throws Exception {
		return checkout(url, path, null, null);
	}

	/**
	 * 将文件checkout到指定目录.
	 * 
	 * @param url 源
	 * @param path 路径
	 * @param username 账号
	 * @param password 密码
	 * @return CheckOut结果
	 * @throws Exception 如果svn up命令失败会抛出此异常.
	 */
	public static String checkout(String url, String path, String username, String password) throws Exception {
		StringBuilder sb = new StringBuilder(256);
		sb.append("svn co ").append(url).append(" ").append(path);
		return exec(sb.toString(), username, password);
	}

	/**
	 * 执行SVN命令.
	 * 
	 * @param cmd 命令
	 * @param username 账号
	 * @param password 密码
	 * @return 命令结果
	 * @throws Exception 如果svn命令失败会抛出此异常.
	 */
	private static String exec(String cmd, String username, String password) throws Exception {
		StringBuilder sb = new StringBuilder(256);
		sb.append(cmd);
		if (StringUtils.isNotEmpty(username)) {
			sb.append(" --username ").append(username);
		}
		if (StringUtils.isNotEmpty(password)) {
			sb.append(" --password ").append(password);
		}

		List<String> envp = new ArrayList<>(4);
		if (SystemUtils.IS_OS_LINUX) {
			envp.addAll(Arrays.asList("sh", "-c", sb.toString()));
		} else {
			envp.addAll(Arrays.asList(sb.toString()));
		}
		envp.add("LANG=UTF-8");

		Process process = Runtime.getRuntime().exec(envp.toArray(new String[] {}));
		try (InputStreamReader isr = new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")); LineNumberReader in = new LineNumberReader(isr)) {
			String tmp = "";
			sb.setLength(0);
			while ((tmp = in.readLine()) != null) {
				sb.append(tmp).append("\n");
			}
		}
		return sb.toString();
	}
}