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
package xyz.noark.network.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import xyz.noark.core.util.StringUtils;
import xyz.noark.log.Logger;
import xyz.noark.log.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * HTTP报文输出辅助类
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.8
 */
public class HttpOutputManager {
    private static final Logger logger = LoggerFactory.getLogger(HttpOutputManager.class);

    /**
     * 记录请求报文.
     *
     * @param req 一次请求
     * @param ip  客户端IP
     */
    public static void logRequest(FullHttpRequest req, String ip) {
        logger.info("================== request ==================");
        logInitialLine(req);
        logIp(ip);
        logHeaders(req.headers());
        logHeaders(req.trailingHeaders());
    }

    /**
     * 记录请求参数
     *
     * @param req 一次请求
     * @throws IOException 处理请求体时可能会发生IO异常
     */
    public static void logParameter(NoarkHttpServletRequest req) throws IOException {
        logger.info("================== parameter ==================");
        for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            logger.info("{}: {}", e.getKey(), e.getValue());
        }
        // request body
        if (req.getInputStream() != null) {
            try (InputStream inputStream = req.getInputStream()) {
                logger.info("request body: {}", StringUtils.readString(inputStream));
            }
        }
    }

    /**
     * 记录响应报文
     *
     * @param res          一次响应
     * @param cacheContent 响应正文内容
     */
    public static void logResponse(FullHttpResponse res, String cacheContent) {
        logger.info("================== response ==================");
        logger.info("{}: {}", res.protocolVersion(), res.status());
        logHeaders(res.headers());
        logHeaders(res.trailingHeaders());
        logger.info("response body: {}", cacheContent);
    }


    private static void logHeaders(HttpHeaders headers) {
        for (Map.Entry<String, String> e : headers) {
            logger.info("{}: {}", e.getKey(), e.getValue());
        }
    }

    private static void logIp(String ip) {
        logger.info("IP: {}", ip);
    }

    private static void logInitialLine(HttpRequest req) {
        logger.info("{} {} {}", req.method(), req.uri(), req.protocolVersion());
    }
}
