/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 *        http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.util;

import xyz.noark.core.exception.HttpAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static xyz.noark.log.LogHelper.logger;

/**
 * HTTP工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
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
     * @throws IOException HTTP过程中可能会出现IO异常
     */
    public static String get(String url) throws IOException {
        return get(url, DEFAULT_TIMEOUT, Collections.emptyMap());
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url             发送请求的URL
     * @param responseCharset 响应编码（默认UTF-8）
     * @return URL 所代表远程资源的响应结果
     * @throws IOException HTTP过程中可能会出现IO异常
     */
    public static String get(String url, Charset responseCharset) throws IOException {
        return get(url, DEFAULT_TIMEOUT, Collections.emptyMap(), responseCharset);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url     发送请求的URL
     * @param timeout 请求超时（单位：毫秒）
     * @return URL 所代表远程资源的响应结果
     * @throws IOException HTTP过程中可能会出现IO异常
     */
    public static String get(String url, int timeout) throws IOException {
        return get(url, timeout, Collections.emptyMap());
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url             发送请求的URL
     * @param requestProperty 请求属性
     * @return URL 所代表远程资源的响应结果
     * @throws IOException HTTP过程中可能会出现IO异常
     */
    public static String get(String url, Map<String, String> requestProperty) throws IOException {
        return get(url, DEFAULT_TIMEOUT, requestProperty);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url             发送请求的URL
     * @param timeout         请求超时（单位：毫秒）
     * @param requestProperty 请求属性
     * @return URL 所代表远程资源的响应结果
     * @throws IOException HTTP过程中可能会出现IO异常
     */
    public static String get(String url, int timeout, Map<String, String> requestProperty) throws IOException {
        return get(url, timeout, requestProperty, CharsetUtils.CHARSET_UTF_8);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url             发送请求的URL
     * @param timeout         请求超时（单位：毫秒）
     * @param requestProperty 请求属性
     * @param responseCharset 响应编码（默认UTF-8）
     * @return URL 所代表远程资源的响应结果
     * @throws IOException HTTP过程中可能会出现IO异常
     */
    public static String get(String url, int timeout, Map<String, String> requestProperty, Charset responseCharset) throws IOException {

        // 打开和URL之间的连接
        URLConnection connection = new URL(url).openConnection();
        connection.setReadTimeout(timeout);
        connection.setConnectTimeout(timeout);
        requestProperty.forEach(connection::setRequestProperty);

        // 建立实际的连接
        connection.connect();

        return handleResponseText(connection, responseCharset);

    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param params 请求参数
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String params) {
        return post(url, params, DEFAULT_TIMEOUT, Collections.emptyMap());
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url             发送请求的 URL
     * @param params          请求参数
     * @param responseCharset 响应编码（默认UTF-8）
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String params, Charset responseCharset) {
        return post(url, params, DEFAULT_TIMEOUT, Collections.emptyMap(), responseCharset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url     发送请求的 URL
     * @param params  请求参数
     * @param timeout 请求超时（单位：毫秒）
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String params, int timeout) {
        return post(url, params, timeout, Collections.emptyMap());
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url             发送请求的 URL
     * @param params          请求参数
     * @param requestProperty 请求属性
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String params, Map<String, String> requestProperty) {
        return post(url, params, DEFAULT_TIMEOUT, requestProperty);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url             发送请求的 URL
     * @param params          请求参数
     * @param timeout         请求超时（单位：毫秒）
     * @param requestProperty 请求属性
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String params, int timeout, Map<String, String> requestProperty) {
        return post(url, params, timeout, requestProperty, CharsetUtils.CHARSET_UTF_8);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url             发送请求的 URL
     * @param params          请求参数
     * @param timeout         请求超时（单位：毫秒）
     * @param requestProperty 请求属性
     * @param responseCharset 响应编码（默认UTF-8）
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String params, int timeout, Map<String, String> requestProperty, Charset responseCharset) {
        logger.info("POST: url={}, param={}", url, params);
        try {
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");

            // 设置通用的请求属性
            connection.setReadTimeout(timeout);
            connection.setConnectTimeout(timeout);
            requestProperty.forEach(connection::setRequestProperty);

            // 构建Post参数并发送...
            buildPostParamsAndSend(connection, params, requestProperty);

            return handleResponseText(connection, responseCharset);
        } catch (Exception e) {
            throw new HttpAccessException(e);
        }
    }

    static void buildPostParamsAndSend(URLConnection connection, String params, Map<String, String> requestProperty) throws IOException {
        // 发送POST请求必须设置如下两行
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.connect();

        if (StringUtils.isNotEmpty(params)) {
            String encoding = requestProperty.get("Content-Encoding");
            if (GzipUtils.ENCODING_GZIP.equalsIgnoreCase(encoding)) {
                try (OutputStream out = connection.getOutputStream()) {
                    byte[] bytes = params.getBytes(StandardCharsets.UTF_8);
                    // 压缩后发送请求参数
                    out.write(GzipUtils.compress(bytes));
                    // flush输出流的缓冲
                    out.flush();
                }
            }
            // 获取URLConnection对象对应的输出流
            else {
                try (PrintWriter out = new PrintWriter(connection.getOutputStream())) {
                    // 发送请求参数
                    out.print(params);
                    // flush输出流的缓冲
                    out.flush();
                }
            }
        }
    }

    /**
     * 向指定 URL 发送POST方法的请求, 参数格式为Json
     *
     * @param url  发送请求的 URL
     * @param json 请求参数
     * @return 所代表远程资源的响应结果
     */
    public static String postJson(String url, String json) {
        return postJson(url, json, DEFAULT_TIMEOUT);
    }

    /**
     * 向指定 URL 发送POST方法的请求, 参数格式为Json
     *
     * @param url     发送请求的 URL
     * @param json    请求参数
     * @param timeout 超时时间（单位：毫秒）
     * @return 所代表远程资源的响应结果
     */
    public static String postJson(String url, String json, int timeout) {
        return post(url, json, timeout, MapUtils.of("Content-Type", "application/json"));
    }

    /**
     * 向指定 URL 发送POST方法的请求, 参数格式为Json, 但发送数据会被Gzip压缩
     * <p>用于大数据上报接口</p>
     *
     * @param url     发送请求的 URL
     * @param json    请求参数
     * @param timeout 超时时间（单位：毫秒）
     * @return 所代表远程资源的响应结果
     */
    public static String postGzipJson(String url, String json, int timeout) {
        HashMap<String, String> requestProperty = MapUtils.newHashMap(2);
        requestProperty.put("Accept-Encoding", "gzip");
        requestProperty.put("Content-Encoding", "gzip");
        requestProperty.put("Content-Type", "application/json");
        return post(url, json, timeout, requestProperty);
    }

    /**
     * 处理响应文本.
     *
     * @param connection      HTTP链接
     * @param responseCharset 响应编码（默认UTF-8）
     * @return 响应文本
     * @throws IOException if an I/O error occurs whilecreating the input stream.
     */
    static String handleResponseText(URLConnection connection, Charset responseCharset) throws IOException {
        try (InputStream inputStream = connection.getInputStream()) {
            // Content-Encoding:gzip
            String encoding = connection.getContentEncoding();
            if (GzipUtils.ENCODING_GZIP.equalsIgnoreCase(encoding)) {
                try (GZIPInputStream gzip = new GZIPInputStream(inputStream)) {
                    return readResponseText(gzip, responseCharset);
                }
            }

            // 常规读取
            return readResponseText(inputStream, responseCharset);
        }
    }

    private static String readResponseText(InputStream inputStream, Charset responseCharset) throws IOException {
        return StringUtils.readString(inputStream, responseCharset);
    }
}