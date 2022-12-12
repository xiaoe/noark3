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
package xyz.noark.network.rpc.stub;

import xyz.noark.core.exception.RpcTimeoutException;
import xyz.noark.network.codec.rpc.RpcPacket;
import xyz.noark.network.util.CodecUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * RPC同步请求的存根.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class RpcSyncStub<T> extends RpcStub {
    private static final ConcurrentHashMap<Class<?>, Method> CACHES = new ConcurrentHashMap<>(1024);
    private final Integer reqId;
    private final Class<T> ackClass;
    private final ArrayBlockingQueue<RpcPacket> awaitQueue;

    public RpcSyncStub(Integer reqId, Class<T> ackClass) {
        this.reqId = reqId;
        this.ackClass = ackClass;
        this.awaitQueue = new ArrayBlockingQueue<>(1);
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public void submit(RpcPacket packet) {
        awaitQueue.add(packet);
    }

    public T waitTillResult() {
        try {
            RpcPacket packet = awaitQueue.poll(3, TimeUnit.SECONDS);
            if (packet == null) {
                throw new RpcTimeoutException("Rpc超时 reqId=" + reqId);
            }

            return CodecUtils.deserialize(packet.getByteArray().array(), ackClass);
        } catch (Exception e) {
            throw new RpcTimeoutException("Rpc超时 reqId=" + reqId, e);
        }
    }
}
