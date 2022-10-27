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

import xyz.noark.network.codec.rpc.RpcPacketCodec;
import xyz.noark.network.rpc.balanced.RpcLoadBalanced;
import xyz.noark.network.rpc.balanced.RpcRoundLoadBalanced;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.List;

/**
 * 默认的RPC实现.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class DefaultRpcClient implements RpcClient {
    private RpcLoadBalanced loadBalanced;

    public DefaultRpcClient(List<SocketAddress> addressList) {
        this(new RpcConnectorFactory(new RpcPacketCodec()), addressList);
    }

    public DefaultRpcClient(RpcConnectorFactory connectorFactory, List<SocketAddress> addressList) {
        this(new RpcRoundLoadBalanced(connectorFactory.create(addressList)));
    }

    public DefaultRpcClient(RpcLoadBalanced loadBalanced) {
        this.loadBalanced = loadBalanced;
    }

    /**
     * 重置RPC负载均衡算法.
     *
     * @param loadBalanced 负载均衡算法
     */
    public void setLoadBalanced(RpcLoadBalanced loadBalanced) {
        this.loadBalanced = loadBalanced;
    }

    @Override
    public <T> T syncCall(Serializable opcode, Object req, Class<T> ackClass) {
        return loadBalanced.take().syncCall(opcode, req, ackClass);
    }
}
