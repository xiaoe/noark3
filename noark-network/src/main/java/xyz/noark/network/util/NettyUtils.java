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
package xyz.noark.network.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import xyz.noark.core.util.IpUtils;
import xyz.noark.core.util.StringUtils;

/**
 * Netty相关的工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class NettyUtils {
    private static final String UNKNOWN = "unknown";

    /**
     * 分析HTTP的IP，这里已帮忙处理了Nginx代理的问题
     *
     * @param fhr 完整请求句柄
     * @param ctx 链接上下文
     * @return 返回客户真实的IP
     */
    public static String analyzeIp(FullHttpRequest fhr, ChannelHandlerContext ctx) {
        final HttpHeaders headers = fhr.headers();
        // Nginx 优先使用X-Real-IP，这个配置正常是覆盖
        String ip = headers.get("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        // 这是按组比较容易伪造，需要注意
        ip = headers.get("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        // apache
        ip = headers.get("Proxy-Client-IP");
        if (StringUtils.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        // WebLogic
        ip = headers.get("WL-Proxy-Client-IP");
        if (StringUtils.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        
        // 什么配置都没有，那就使用链接IP
        ip = IpUtils.getIp(ctx.channel().remoteAddress());
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
