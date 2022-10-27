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
package xyz.noark.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 抽象的封包长度解码器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public abstract class AbstractLengthDecoder extends ByteToMessageDecoder {
    /**
     * 最大封包长度
     */
    private final static int MAX_PACKET_LENGTH = 65535;
    private final AbstractPacketCodec packetCodec;

    public AbstractLengthDecoder(AbstractPacketCodec packetCodec) {
        this.packetCodec = packetCodec;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int preIndex = in.readerIndex();
        // 封包长度
        int length = readLength(in);

        if (preIndex == in.readerIndex()) {
            return;
        }

        // 不正常的封包，干掉这个人.
        if (length <= 0 || length > this.getMaxPacketLength()) {
            ctx.close();
            return;
        }

        // 封包不全，忽略本次处理.
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        // 满足一个封包
        out.add(packetCodec.decodePacket(this.readSlice(in, length)));
    }

    /**
     * 获取一个包最大的长度
     *
     * @return 一个包最大的长度
     */
    protected int getMaxPacketLength() {
        return MAX_PACKET_LENGTH;
    }

    /**
     * 读取一段切片，默认是保留一次引用，下一层调用者手动清除
     *
     * @param in     ByteBuf缓冲区
     * @param length 要切出来的长度
     * @return 返回切出来的数据
     */
    protected ByteBuf readSlice(ByteBuf in, int length) {
        return in.readRetainedSlice(length);
    }

    /**
     * 从ByteBuf中取出长度.
     *
     * @param in ByteBuf对象
     * @return 封包长度
     */
    protected abstract int readLength(ByteBuf in);
}
