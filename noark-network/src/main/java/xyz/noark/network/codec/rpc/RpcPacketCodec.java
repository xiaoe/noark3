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
package xyz.noark.network.codec.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.ByteToMessageDecoder;
import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.lang.ImmutableByteArray;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.NetworkProtocol;
import xyz.noark.core.util.GzipUtils;
import xyz.noark.network.codec.AbstractPacketCodec;
import xyz.noark.network.codec.ByteBufWrapper;
import xyz.noark.network.rpc.RpcReqProtocol;
import xyz.noark.network.util.ByteBufUtils;
import xyz.noark.network.util.CodecUtils;

import java.io.IOException;

/**
 * Json封包解码器.
 * <p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class RpcPacketCodec extends AbstractPacketCodec {

    @Override
    public <T> T decodeProtocol(ByteArray bytes, Class<T> klass) {
        return CodecUtils.deserialize(bytes.array(), klass);
    }

    @Override
    public ByteArray encodePacket(NetworkProtocol networkProtocol) {
        byte[] bytes = CodecUtils.serialize(networkProtocol.getProtocol());

        // 压缩
        int compressFlag = 0;
        if (bytes.length > 1024) {
            try {
                bytes = GzipUtils.compress(bytes);
                compressFlag = 1;
            } catch (IOException ignored) {
                // 压缩失败，那就不压缩了...
            }
        }
        ByteBuf data = Unpooled.wrappedBuffer(bytes);

        // 包长|ReqFlag|ReqId|Opcode|CompressFlag|Data
        int reqFlag = 0;
        int reqId = 0;
        // RPC之请求协议
        if (networkProtocol instanceof RpcReqProtocol) {
            reqFlag = 1;// 标识为请求
            reqId = ((RpcReqProtocol) networkProtocol).getReqId();
        }
        // RPC之响应协议
        else if (networkProtocol.getPacket() != null && networkProtocol.getPacket() instanceof RpcPacket) {
            reqId = ((RpcPacket) networkProtocol.getPacket()).getReqId();
        }

        // 包长|ReqFlag|ReqId|Opcode|CompressFlag|Data
        ByteBuf head = Unpooled.buffer();
        // ReqFlag
        ByteBufUtils.writeRawVarint32(head, reqFlag);
        // ReqId
        ByteBufUtils.writeRawVarint32(head, reqId);
        // Opcode
        ByteBufUtils.writeRawVarint32(head, (Integer) networkProtocol.getOpcode());
        // CompressFlag
        ByteBufUtils.writeRawVarint32(head, compressFlag);
        return new ByteBufWrapper(Unpooled.wrappedBuffer(head, data));
    }

    @Override
    public RpcPacketLengthEncoder lengthEncoder() {
        return new RpcPacketLengthEncoder();
    }

    @Override
    public ByteToMessageDecoder lengthDecoder() {
        return new RpcPacketLengthDecoder(this);
    }

    @Override
    public NetworkPacket decodePacket(ByteBuf byteBuf) {
        // 包长|ReqFlag|ReqId|Opcode|CompressFlag|Data
        RpcPacket packet = new RpcPacket();
        packet.setLength(byteBuf.readableBytes());
        packet.setReqFlag(ByteBufUtils.readRawVarint32(byteBuf) == 1);
        packet.setReqId(ByteBufUtils.readRawVarint32(byteBuf));
        packet.setOpcode(ByteBufUtils.readRawVarint32(byteBuf));
        int compressFlag = ByteBufUtils.readRawVarint32(byteBuf);
        byte[] bytes = ByteBufUtils.readBytes(byteBuf);

        // 压缩标识，0=不压缩，1=GZip压缩,2=XX压缩
        if (compressFlag == 1) {
            try {
                bytes = GzipUtils.uncompress(bytes);
            } catch (IOException ignored) {
                // 出错了就不解了
            }
        }

        packet.setBytes(new ImmutableByteArray(bytes));
        return packet;
    }
}