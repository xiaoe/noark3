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
package xyz.noark.network.rpc.balanced;

import xyz.noark.core.exception.RpcTimeoutException;
import xyz.noark.network.rpc.RpcConnector;

/**
 * 抽象的负载均衡实现
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public abstract class AbstractLoadBalanced implements RpcLoadBalanced {

    @Override
    public final RpcConnector take() {
        // 最大尝试次数
        int frequency = this.getTryFrequency();
        for (int i = 0; i < frequency; i++) {
            // Take出一个连接
            RpcConnector connector = this.doTake();
            // 如果可用或尝试连接
            if (connector.isActiveOrTryConnect()) {
                return connector;
            }
        }
        // 抛出无可用异常
        throw new RpcTimeoutException("Could not get a connector from the pool.");
    }

    /**
     * 获取最大尝试次数，正常就是所有都轮一次
     *
     * @return 最大尝试次数
     */
    protected abstract int getTryFrequency();

    /**
     * 根据算法Take出来下一个连接.
     *
     * @return 一个连接
     */
    protected abstract RpcConnector doTake();
}
