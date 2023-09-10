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
package com.company.game;

import com.company.game.bean.HelloService;
import com.company.game.bean.HelloServiceImpl;
import xyz.noark.core.annotation.ConditionalOnMissingBean;
import xyz.noark.core.annotation.Configuration;
import xyz.noark.core.annotation.Primary;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.annotation.configuration.Bean;
import xyz.noark.network.rpc.DefaultRpcClient;
import xyz.noark.network.rpc.RpcClient;

import java.net.SocketAddress;
import java.util.List;

/**
 * 启动配置文件.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2
 */
@Configuration
public class GameServerConfiguration {

    @Value("rpc.server.address")
    private List<SocketAddress> addressList;

    @Bean(name = "test")
    @ConditionalOnMissingBean
    public TestService2 test() {
        return new TestService2();
    }

    // 战斗服的RPC
    @Bean
    public RpcClient defaultRpcClient() {
        return new DefaultRpcClient(addressList);
    }

    /**
     * HelloService的默认实现，如果有新的实现，这个默认实现就丢失
     */
    @Bean
    @ConditionalOnMissingBean
    public HelloService helloService() {
        return new HelloServiceImpl();
    }
}
