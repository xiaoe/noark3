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
package xyz.noark.redis;

import xyz.noark.core.util.StringUtils;
import xyz.noark.game.config.AbstractConfigCentre;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于Redis实现的配置中心.
 * <p>
 * 为什么有了ZK，还有一个Redis的实现版本，配置简单嘛，依赖少，上手难度低
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class RedisConfigCentre extends AbstractConfigCentre {
    private final Redis redis;
    protected final String configPathPrefix;
    protected final String configPathDefault;

    public RedisConfigCentre(HashMap<String, String> basicConfig) {
        this.configPathPrefix = basicConfig.getOrDefault("NoarkConstant.CONFIG_PATH", "/noark/config/game/");
        this.configPathDefault = StringUtils.pathJoin(configPathPrefix, "default");

        String host = basicConfig.getOrDefault(RedisConstant.CONFIG_REDIS_HOST, "127.0.0.1");
        int port = Integer.parseInt(basicConfig.getOrDefault(RedisConstant.CONFIG_REDIS_PORT, "6379"));
        String password = basicConfig.get(RedisConstant.CONFIG_REDIS_PASSWORD);
        if (StringUtils.isBlank(password)) {
            password = null;
        }
        int index = Integer.parseInt(basicConfig.getOrDefault(RedisConstant.CONFIG_REDIS_INDEX, "0"));
        // 初始化一个Redis实例，等会去拉配置回来
        this.redis = new Redis(host, port, password, index).ping();
    }

    @Override
    protected Map<String, String> doLoadConfig(String sid) {
        return redis.hgetAll(StringUtils.pathJoin(configPathPrefix, sid));
    }

    @Override
    protected Map<String, String> doLoadConfig() {
        return redis.hgetAll(configPathDefault);
    }

    @Override
    public void listenerConfig() {
        // TODO Redis的简单版本的配置中心日后再实现
    }
}