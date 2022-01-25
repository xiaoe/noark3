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

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;

import static xyz.noark.log.LogHelper.logger;

/**
 * HTTPS工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.4
 */
public class HttpsUtils {

    /**
     * 以HTTPS方式向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @return URL所代表远程资源的响应结果
     */
    public static String get(String url) {
        return get(url, HttpUtils.DEFAULT_TIMEOUT, Collections.emptyMap());
    }

    /**
     * 以HTTPS方式向指定URL发送GET方法的请求
     *
     * @param url     发送请求的URL
     * @param timeout 请求超时（单位：毫秒）
     * @return URL所代表远程资源的响应结果
     */
    public static String get(String url, int timeout) {
        return get(url, timeout, Collections.emptyMap());
    }

    /**
     * 以HTTPS方式向指定URL发送GET方法的请求
     *
     * @param url             发送请求的URL
     * @param requestProperty 请求属性
     * @return URL所代表远程资源的响应结果
     */
    public static String get(String url, Map<String, String> requestProperty) {
        return get(url, HttpUtils.DEFAULT_TIMEOUT, requestProperty);
    }

    /**
     * 以HTTPS方式向指定URL发送GET方法的请求
     *
     * @param url             发送请求的URL
     * @param timeout         请求超时（单位：毫秒）
     * @param requestProperty 请求属性
     * @return URL所代表远程资源的响应结果
     */
    public static String get(String url, int timeout, Map<String, String> requestProperty) {
        logger.info("GET: url={}", url);
        try {
            HttpsURLConnection connection = createHttpsUrlConnection(url);
            connection.setRequestMethod("GET");
            connection.setReadTimeout(timeout);
            requestProperty.forEach(connection::setRequestProperty);

            // 取出HTTP响应结果
            String result = StringUtils.readString(connection.getInputStream());
            logger.info(result);
            return result;
        } catch (Exception e) {
            throw new HttpAccessException(e);
        }
    }

    /**
     * 以HTTPS方式向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param params 请求参数
     * @return URL所代表远程资源的响应结果
     */
    public static String post(String url, String params) {
        return post(url, params, HttpUtils.DEFAULT_TIMEOUT, Collections.emptyMap());
    }

    /**
     * 以HTTPS方式向指定 URL 发送POST方法的请求
     *
     * @param url     发送请求的 URL
     * @param params  请求参数
     * @param timeout 请求超时（单位：毫秒）
     * @return URL所代表远程资源的响应结果
     */
    public static String post(String url, String params, int timeout) {
        return post(url, params, timeout, Collections.emptyMap());
    }

    /**
     * 以HTTPS方式向指定 URL 发送POST方法的请求
     *
     * @param url             发送请求的 URL
     * @param params          请求参数
     * @param requestProperty 请求属性
     * @return URL所代表远程资源的响应结果
     */
    public static String post(String url, String params, Map<String, String> requestProperty) {
        return post(url, params, HttpUtils.DEFAULT_TIMEOUT, requestProperty);
    }

    /**
     * 以HTTPS方式向指定 URL 发送POST方法的请求
     *
     * @param url             发送请求的 URL
     * @param params          请求参数
     * @param timeout         请求超时（单位：毫秒）
     * @param requestProperty 请求属性
     * @return URL所代表远程资源的响应结果
     */
    public static String post(String url, String params, int timeout, Map<String, String> requestProperty) {
        return post(url, params, timeout, requestProperty, CharsetUtils.CHARSET_UTF_8);
    }

    /**
     * 以HTTPS方式向指定 URL 发送POST方法的请求
     *
     * @param url             发送请求的 URL
     * @param params          请求参数
     * @param timeout         请求超时（单位：毫秒）
     * @param requestProperty 请求属性
     * @param responseCharset 响应编码（默认UTF-8）
     * @return URL所代表远程资源的响应结果
     */
    public static String post(String url, String params, int timeout, Map<String, String> requestProperty, Charset responseCharset) {
        logger.info("POST: url={}, param={}", url, params);
        try {
            HttpsURLConnection connection = createHttpsUrlConnection(url);
            connection.setRequestMethod("POST");
            connection.setReadTimeout(timeout);
            requestProperty.forEach(connection::setRequestProperty);

            // 构建Post参数并发送...
            HttpUtils.buildPostParamsAndSend(connection, params, requestProperty);

            return HttpUtils.handleResponseText(connection, responseCharset);
        } catch (Exception e) {
            throw new HttpAccessException(e);
        }
    }


    private static HttpsURLConnection createHttpsUrlConnection(String url) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection connection = (HttpsURLConnection) (new URL(url)).openConnection();
        connection.setSSLSocketFactory(initSslSocketFactory(new DefaultTrustManager()));
        connection.setHostnameVerifier(new DefaultHostnameVerifier());
        return connection;
    }

    private static SSLSocketFactory initSslSocketFactory(TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        return sc.getSocketFactory();
    }

    private static class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
}
