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

import static xyz.noark.log.LogHelper.logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import xyz.noark.core.exception.HttpAccessException;

/**
 * HTTP工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class HttpUtils {

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url 发送请求的URL
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String get(String url) {
		logger.info("GET: url={}", url);
		try {
			// 打开和URL之间的连接
			URLConnection connection = new URL(url).openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			// 建立实际的连接
			connection.connect();

			StringBuilder sb = new StringBuilder();
			// 定义 BufferedReader输入流来读取URL的响应
			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = in.readLine()) != null) {
					sb.append(line);
				}
			}

			String result = sb.toString();
			logger.info(result);
			return result;
		} catch (Exception e) {
			throw new HttpAccessException(e);
		}
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url 发送请求的 URL
	 * @param params 请求参数
	 * @return 所代表远程资源的响应结果
	 */
	public static String post(String url, String params) {
		logger.info("POST: url={}, param={}", url, params);
		try {
			// 打开和URL之间的连接
			URLConnection connection = new URL(url).openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			// 发送POST请求必须设置如下两行
			connection.setDoOutput(true);
			connection.setDoInput(true);

			if (!params.isEmpty()) {
				// 获取URLConnection对象对应的输出流
				try (PrintWriter out = new PrintWriter(connection.getOutputStream())) {
					// 发送请求参数
					out.print(params);
					// flush输出流的缓冲
					out.flush();
				}
			}

			StringBuilder sb = new StringBuilder();
			// 定义BufferedReader输入流来读取URL的响应
			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = in.readLine()) != null) {
					sb.append(line);
				}
			}

			String result = sb.toString();
			logger.info(result);
			return result;
		} catch (Exception e) {
			throw new HttpAccessException(e);
		}
	}
}