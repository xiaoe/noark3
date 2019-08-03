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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import xyz.noark.core.exception.HttpAccessException;

/**
 * HTTP工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class HttpUtils {
	/**
	 * HTTP请求默认超时：默认3秒
	 */
	static final int DEFAULT_TIMEOUT = 3000;

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url 发送请求的URL
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String get(String url) {
		return get(url, DEFAULT_TIMEOUT, Collections.emptyMap());
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url 发送请求的URL
	 * @param timeout 请求超时（单位：毫秒）
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String get(String url, int timeout) {
		return get(url, timeout, Collections.emptyMap());
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url 发送请求的URL
	 * @param requestProperty 请求属性
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String get(String url, Map<String, String> requestProperty) {
		return get(url, DEFAULT_TIMEOUT, requestProperty);
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url 发送请求的URL
	 * @param timeout 请求超时（单位：毫秒）
	 * @param requestProperty 请求属性
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String get(String url, int timeout, Map<String, String> requestProperty) {
		logger.info("GET: url={}", url);
		try {
			// 打开和URL之间的连接
			URLConnection connection = new URL(url).openConnection();
			connection.setReadTimeout(timeout);
			requestProperty.forEach((key, value) -> connection.setRequestProperty(key, value));

			// 建立实际的连接
			connection.connect();

			String result = HttpUtils.readString(connection.getInputStream());
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
		return post(url, params, DEFAULT_TIMEOUT, Collections.emptyMap());
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url 发送请求的 URL
	 * @param params 请求参数
	 * @param timeout 请求超时（单位：毫秒）
	 * @return 所代表远程资源的响应结果
	 */
	public static String post(String url, String params, int timeout) {
		return post(url, params, timeout, Collections.emptyMap());
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url 发送请求的 URL
	 * @param params 请求参数
	 * @param requestProperty 请求属性
	 * @return 所代表远程资源的响应结果
	 */
	public static String post(String url, String params, Map<String, String> requestProperty) {
		return post(url, params, DEFAULT_TIMEOUT, requestProperty);
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url 发送请求的 URL
	 * @param params 请求参数
	 * @param timeout 请求超时（单位：毫秒）
	 * @param requestProperty 请求属性
	 * @return 所代表远程资源的响应结果
	 */
	public static String post(String url, String params, int timeout, Map<String, String> requestProperty) {
		logger.info("POST: url={}, param={}", url, params);
		try {
			// 打开和URL之间的连接
			URLConnection connection = new URL(url).openConnection();
			// 设置通用的请求属性
			connection.setReadTimeout(timeout);
			requestProperty.forEach((key, value) -> connection.setRequestProperty(key, value));

			// 发送POST请求必须设置如下两行
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// 建立实际的连接
			connection.connect();

			if (StringUtils.isNotEmpty(params)) {
				// 获取URLConnection对象对应的输出流
				try (PrintWriter out = new PrintWriter(connection.getOutputStream())) {
					// 发送请求参数
					out.print(params);
					// flush输出流的缓冲
					out.flush();
				}
			}

			String result = HttpUtils.readString(connection.getInputStream());
			logger.info(result);
			return result;
		} catch (Exception e) {
			throw new HttpAccessException(e);
		}
	}

	/**
	 * 从输入流中读出所有文本.
	 * 
	 * @param inputStream 输入流
	 * @return 返回流中的文本
	 * @throws IOException If an I/O error occurs
	 */
	public static String readString(InputStream inputStream) throws IOException {
		return readString(inputStream, CharsetUtils.CHARSET_UTF_8);
	}

	/**
	 * 从输入流中读出所有文本.
	 * 
	 * @param inputStream 输入流
	 * @param charset 文本的编码方式
	 * @return 返回流中的文本
	 * @throws IOException If an I/O error occurs
	 */
	public static String readString(InputStream inputStream, Charset charset) throws IOException {
		final StringBuilder sb = new StringBuilder(256);
		// 这里没有选择BufferedReader就是不想一行一行的读，浪费字符串拼接性能
		try (InputStreamReader isr = new InputStreamReader(inputStream, charset)) {
			// 申明一次读取缓冲区
			final char[] cbuf = new char[64];
			// 这里并没有使用while(true),如果一个文本超过100W，还是放弃后面的算了
			while (sb.length() < MathUtils.MILLION) {
				int n = isr.read(cbuf);
				// 读结束了，就GG了
				if (n < 0) {
					break;
				}
				sb.append(cbuf, 0, n);
			}
		}
		return sb.toString();
	}
}