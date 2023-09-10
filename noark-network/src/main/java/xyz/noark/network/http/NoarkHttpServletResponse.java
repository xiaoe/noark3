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

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import xyz.noark.core.util.CharsetUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.network.util.ByteBufUtils;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

/**
 * Noark实现的HTTP响应对象.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
class NoarkHttpServletResponse implements HttpServletResponse {
    private final ChannelHandlerContext ctx;
    private final boolean keepAlive;
    private final boolean logEnabled;

    private HttpResponseStatus status = HttpResponseStatus.OK;
    /**
     * 响应的内容
     */
    private ByteBuf content;
    private String charset = CharsetUtils.UTF_8;
    private String contentType = "application/json";
    /**
     * 缓存一个返回内容，用于日志记录
     */
    private String cacheContent = StringUtils.EMPTY;

    NoarkHttpServletResponse(ChannelHandlerContext ctx, boolean keepAlive, boolean logEnabled) {
        this.ctx = ctx;
        this.keepAlive = keepAlive;
        this.logEnabled = logEnabled;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public void setStatus(int status) {
        this.status = HttpResponseStatus.valueOf(status);
    }

    @Override
    public void writeString(String str) {
        this.cacheContent = str;
        this.content = Unpooled.copiedBuffer(str, Charset.forName(charset));
    }

    @Override
    public void writeObject(Object o) {
        this.writeString(JSON.toJSONString(o));
    }

    @Override
    public void flush() {
        this.sendAndClose();
    }

    private void sendAndClose() {
        FullHttpResponse response = this.createResponse();
        this.fillResponseHeaderInfo(response.headers());

        if (keepAlive) {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response);
        } else {
            response.headers().set(CONNECTION, CLOSE);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        if (logEnabled) {
            HttpOutputManager.logResponse(response, cacheContent);
        }
    }

    /**
     * 填充头信息
     *
     * @param httpHeaders Http头
     */
    private void fillResponseHeaderInfo(HttpHeaders httpHeaders) {
        httpHeaders.set(HttpHeaderNames.CONTENT_TYPE.toString(), StringUtils.join(contentType, ";charset=", charset));
        httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH.toString(), ByteBufUtils.size(content));
    }

    private FullHttpResponse createResponse() {
        HttpVersion version = HttpVersion.HTTP_1_1;
        ByteBuf buf = content == null ? Unpooled.EMPTY_BUFFER : content;
        return new DefaultFullHttpResponse(version, status, buf);
    }

    public void setContent(ByteBuf content) {
        this.content = content;
    }
}
