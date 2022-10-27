/*
 * Copyright © 2018 huiyunetwork.com All Rights Reserved.
 *
 * 感谢您加入辉娱网络，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 *
 * 		http://www.huiyunetwork.com/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归辉娱网络所有，我们将保留所有权利;
 * 4、凡侵犯辉娱网络相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.network.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import xyz.noark.network.codec.rpc.RpcPacketCodec;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * RPC连接创建工厂类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class RpcConnectorFactory {
    private final Bootstrap BOOTSTRAP = new Bootstrap();
    private final RpcPacketCodec rpcPacketCodec;

    public RpcConnectorFactory(RpcPacketCodec rpcPacketCodec) {
        this.rpcPacketCodec = rpcPacketCodec;
        // 增加编解码过滤器
        BOOTSTRAP.group(new NioEventLoopGroup());
        BOOTSTRAP.channel(NioSocketChannel.class);
        BOOTSTRAP.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", rpcPacketCodec.lengthDecoder());
                pipeline.addLast("encoder", rpcPacketCodec.lengthEncoder());
            }
        });
    }

    public List<RpcConnector> create(List<SocketAddress> addressList) {
        List<RpcConnector> connectorList = new ArrayList<>(addressList.size());
        for (SocketAddress address : addressList) {
            connectorList.add(new RpcConnector(BOOTSTRAP, rpcPacketCodec, address));
        }
        return connectorList;
    }
}
