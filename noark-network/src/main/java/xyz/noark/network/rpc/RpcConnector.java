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
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.network.PacketCodec;
import xyz.noark.core.util.DateUtils;
import xyz.noark.core.util.MapUtils;
import xyz.noark.network.rpc.stub.RpcStub;
import xyz.noark.network.rpc.stub.RpcSyncStub;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static xyz.noark.log.LogHelper.logger;

/**
 * RPC连接
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class RpcConnector {
    private static final AtomicInteger reqId = new AtomicInteger(0);
    private final Bootstrap bootstrap;
    private final PacketCodec packetCodec;
    private final SocketAddress address;

    private Channel channel;

    public RpcConnector(Bootstrap bootstrap, PacketCodec packetCodec, SocketAddress address) {
        this.bootstrap = bootstrap;
        this.packetCodec = packetCodec;
        this.address = address;
    }

    private int nextReqId() {
        return reqId.incrementAndGet();
    }

    private final ConcurrentMap<Integer, RpcSyncStub<?>> stubMap = MapUtils.newConcurrentHashMap(512);

    public RpcStub getRpcStub(Integer reqId) {
        return stubMap.get(reqId);
    }

    public <T> T syncCall(Serializable opcode, Object req, Class<T> ackClass) {
        long startTime = System.nanoTime();
        // 1.生成请求唯一ID
        final Integer reqId = this.nextReqId();
        RpcSyncStub<T> stub = new RpcSyncStub<>(reqId, ackClass);
        try {
            // 2.绑定存根
            stubMap.put(reqId, stub);

            // 3.发消息
            this.send(new RpcReqProtocol(reqId, opcode, req));

            // 4.等待直到拿到结果
            return stub.waitTillResult();
        }
        // 5.解绑回调
        finally {
            stubMap.remove(reqId);
            float exec = DateUtils.formatNanoTime(System.nanoTime() - startTime);
            logger.debug("rpc sync call. reqId={}, exec={} ms", reqId, exec);
        }
    }

    private void send(RpcReqProtocol protocol) {
        // 使用封包编码器去编码
        ByteArray packet = packetCodec.encodePacket(protocol);
        channel.writeAndFlush(packet, channel.voidPromise());
    }

    public synchronized void connect() {
        this.lastConnected = System.currentTimeMillis();
        try {
            logger.info("trying reconnect to {}", address);
            ChannelFuture cf = bootstrap.connect(address).sync();

            // 链接通道
            this.channel = cf.channel();
            // 把RPC处理器给挂上去
            this.channel.pipeline().addLast(new RpcConnectorHandler(this));
            // 重置失败计次
            this.fails = 0;
        } catch (Throwable e) {
            logger.warn("reconnect fail. address={}", address);

            // 失败计次++
            this.fails++;
        }
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    /**
     * 连接失败次数
     */
    private int fails = 0;
    /**
     * 上次连接时间
     */
    private long lastConnected;

    private int max_fails = 2;
    private int fail_timeout = 5;

    /**
     * 判定当前链接是否可用，如果不可用就进行重连，失败计次加一
     *
     * @return 返回连接可用了则返回true
     */
    public boolean isActiveOrTryConnect() {
        // 已连接并可用
        if (this.isConnected()) {
            return true;
        }

        // 失败次数已达上限且在CD期内...
        if (fails >= max_fails && lastConnected + fail_timeout * 1000L >= System.currentTimeMillis()) {
            return false;
        }

        // 再次尝试连接
        this.connect();
        return this.isConnected();
    }
}
