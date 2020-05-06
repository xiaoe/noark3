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
package xyz.noark.network.codec.protobuf;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import xyz.noark.core.exception.DataException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.lang.ByteArrayOutputStream;
import xyz.noark.core.lang.ImmutableByteArray;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.NetworkProtocol;
import xyz.noark.core.util.MethodUtils;
import xyz.noark.core.util.UnsignedUtils;
import xyz.noark.network.codec.AbstractPacketCodec;
import xyz.noark.network.codec.ByteBufWrapper;
import xyz.noark.network.codec.DefaultNetworkPacket;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ProtobufV3版本的编码解码器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class ProtobufCodec extends AbstractPacketCodec {
    private static final ConcurrentHashMap<Class<?>, Method> CACHES = new ConcurrentHashMap<>(1024);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decodeProtocol(ByteArray bytes, Class<T> klass) {
        Method method = CACHES.computeIfAbsent(klass, key -> MethodUtils.getMethod(key, "parseFrom", byte[].class));
        return (T) MethodUtils.invoke(null, method, bytes.array());
    }

    @Override
    public ByteArray encodePacket(NetworkProtocol networkProtocol) {
        final int opcode = (Integer) networkProtocol.getOpcode();
        if (opcode > Short.MAX_VALUE) {
            throw new UnrealizedException("illegal opcode=" + opcode + ", max=65535");
        }

        MessageLite message = null;
        if (networkProtocol.getProtocol() instanceof MessageLite) {
            message = (MessageLite) networkProtocol.getProtocol();
        } else if (networkProtocol.getProtocol() instanceof MessageLite.Builder) {
            message = ((MessageLite.Builder) networkProtocol.getProtocol()).build();
        } else {
            throw new UnrealizedException("illegal data type：" + networkProtocol.getProtocol().getClass());
        }

        ImmutableByteArray byteArray = new ImmutableByteArray(message.getSerializedSize() + 2);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(byteArray)) {
            // 写入Opcode
            byteArrayOutputStream.writeShortLE(opcode);
            // 写入协议内容
            try {
                message.writeTo(byteArrayOutputStream);
            } catch (IOException e) {
                throw new DataException("PB writeTo exception", e);
            }
            return byteArray;
        }
    }

    @Override
    public MessageToByteEncoder<?> lengthEncoder() {
        return new ProtobufLengthEncoder();
    }

    @Override
    public ByteToMessageDecoder lengthDecoder() {
        return new ProtobufLengthDecoder(this);
    }

    @Override
    public NetworkPacket decodePacket(ByteBuf byteBuf) {
        DefaultNetworkPacket packet = new DefaultNetworkPacket();
        packet.setLength(byteBuf.readableBytes());
        packet.setIncode(UnsignedUtils.toUnsigned(byteBuf.readShortLE()));
        packet.setChecksum(UnsignedUtils.toUnsigned(byteBuf.readShortLE()));
        packet.setOpcode(UnsignedUtils.toUnsigned(byteBuf.readShortLE()));
        packet.setBytes(new ByteBufWrapper(byteBuf));
        return packet;
    }
}