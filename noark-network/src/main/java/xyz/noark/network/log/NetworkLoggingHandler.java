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
package xyz.noark.network.log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LoggingHandler;
import xyz.noark.core.util.StringUtils;
import xyz.noark.log.Logger;
import xyz.noark.log.LoggerFactory;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * 网络封包日志类.
 * <p>
 * 区别于Netty的 {@link LoggingHandler}，这个处理器只处理输入与输出封包的日志，且常驻管道，使用配置来控制是否打印
 * </p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.5
 */
@Sharable
public class NetworkLoggingHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(NetworkLoggingHandler.class);
    /**
     * 日志输出是否激活
     */
    private boolean outputActive = true;

    public NetworkLoggingHandler() {
    }

    public NetworkLoggingHandler(boolean outputActive) {
        this.outputActive = outputActive;
    }

    public void setOutputActive(boolean outputActive) {
        this.outputActive = outputActive;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (outputActive && logger.isInfoEnabled()) {
            logger.info(format(ctx, "READ", msg));
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (outputActive && logger.isInfoEnabled()) {
            logger.info(format(ctx, "WRITE", msg));
        }
        ctx.write(msg, promise);
    }

    /**
     * 格式化事件并返回格式化后的消息。
     *
     * @param ctx       上下文
     * @param eventName 事件名称
     * @param arg       事件参数
     * @return 封包文本
     */
    protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {
        if (arg instanceof ByteBuf) {
            return formatByteBuf(ctx, eventName, (ByteBuf) arg);
        } else if (arg instanceof ByteBufHolder) {
            return formatByteBufHolder(ctx, eventName, (ByteBufHolder) arg);
        } else {
            return formatSimple(ctx, eventName, arg);
        }
    }

    /**
     * 生成参数为{@link ByteBuf}的指定事件的默认日志消息。
     */
    private String formatByteBuf(ChannelHandlerContext ctx, String eventName, ByteBuf msg) {
        String chStr = ctx.channel().toString();
        int length = msg.readableBytes();
        if (length == 0) {
            return StringUtils.join(chStr, " ", eventName, ": 0B");
        } else {
            int outputLength = chStr.length() + 1 + eventName.length() + 2 + 10 + 1;
            outputLength += calHexDumpLength(length);

            StringBuilder sb = new StringBuilder(outputLength);
            sb.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B');
            sb.append(NEWLINE);
            appendPrettyHexDump(sb, msg);
            return sb.toString();
        }
    }

    private int calHexDumpLength(int length) {
        return 2 + (length / 16 + (length % 15 == 0 ? 0 : 1) + 4) * 80;
    }

    /**
     * 生成指定事件的默认日志消息，该事件的参数为{@link ByteBufHolder}。
     */
    private String formatByteBufHolder(ChannelHandlerContext ctx, String eventName, ByteBufHolder msg) {
        String chStr = ctx.channel().toString();
        String msgStr = msg.toString();
        ByteBuf content = msg.content();
        int length = content.readableBytes();
        if (length == 0) {
            return StringUtils.join(chStr, " ", eventName, ", ", msgStr, ", 0B");
        } else {
            int outputLength = chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 2 + 10 + 1;
            outputLength += calHexDumpLength(length);

            StringBuilder sb = new StringBuilder(outputLength);
            sb.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).append(", ").append(length).append('B');
            sb.append(NEWLINE);
            appendPrettyHexDump(sb, content);
            return sb.toString();
        }
    }

    /**
     * 生成参数为任意对象的指定事件的默认日志消息。
     */
    private static String formatSimple(ChannelHandlerContext ctx, String eventName, Object msg) {
        String chStr = ctx.channel().toString();
        String msgStr = String.valueOf(msg);
        return StringUtils.join(chStr, " ", eventName, ": ", msgStr);
    }
}