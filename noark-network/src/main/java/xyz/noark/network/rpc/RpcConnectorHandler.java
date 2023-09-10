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
package xyz.noark.network.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xyz.noark.core.ioc.manager.PacketMethodManager;
import xyz.noark.core.ioc.wrap.PacketMethodWrapper;
import xyz.noark.core.ioc.wrap.method.LocalPacketMethodWrapper;
import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.network.codec.rpc.RpcPacket;
import xyz.noark.network.rpc.stub.RpcStub;

/**
 * RPC客户端协议处理器
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class RpcConnectorHandler extends SimpleChannelInboundHandler<RpcPacket> {
    private final RpcConnector client;

    public RpcConnectorHandler(RpcConnector client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcPacket packet) {
        try (ByteArray ignored = packet.getByteArray()) {
            // 请求
            if (packet.isReqFlag()) {
                PacketMethodWrapper pmw = PacketMethodManager.getInstance().getPacketMethodWrapper(packet.getOpcode());
                ThreadDispatcher.getInstance().dispatchClientPacket(client.getSession(), packet, (LocalPacketMethodWrapper) pmw);
            }
            // 响应
            else {
                RpcStub stub = client.getRpcStub(packet.getReqId());
                // 存根已消失，响应迟到，丢了就行
                if (stub == null) {
                    return;
                }
                // 同步执行的RPC
                if (stub.isSync()) {
                    stub.submit(packet);
                }
                // 异步回调
                else {
                    stub.callback(packet);
                }
            }
        }
    }
}