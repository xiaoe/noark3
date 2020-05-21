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

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class NoarkHttpServletResponse implements HttpServletResponse {
    private final ChannelHandlerContext ctx;

    private HttpResponseStatus status = HttpResponseStatus.OK;
    // 响应的内容
    private ByteBuf content;
    private String charset = CharsetUtils.UTF_8;
    private String contentType = "application/json";

    public NoarkHttpServletResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
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
    public void send(int status, String msg) {
        this.setStatus(status);
        this.send(msg);
    }

    @Override
    public void send(String msg) {
        this.content = Unpooled.copiedBuffer(msg, Charset.forName(charset));
        this.sendAndClose();
    }

    @Override
    public void writeString(String str) {
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
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 填充头信息
     *
     * @param httpHeaders Http头
     */
    private void fillResponseHeaderInfo(HttpHeaders httpHeaders) {
        httpHeaders.set(HttpHeaderNames.CONTENT_TYPE.toString(), StringUtils.join(contentType, ";charset=", charset));
        httpHeaders.set(HttpHeaderNames.CONTENT_ENCODING.toString(), charset);
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
