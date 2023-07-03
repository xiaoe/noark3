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
package xyz.noark.game.config;

import xyz.noark.core.util.StringUtils;
import xyz.noark.game.NoarkConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于Nacos实现的配置中心.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class NacosConfigCentre extends AbstractConfigCentre {
    /**
     * Nacos中dataId参数，对应游戏里就是配置文件名称
     */
    private static final String DEFAULT_DATA_ID = "application.properties";
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    public NacosConfigCentre(HashMap<String, String> basicConfig) {
        String addr = basicConfig.getOrDefault(NoarkConstant.NACOS_SERVER_ADDR, "127.0.0.1:8848");
        String[] array = StringUtils.split(addr, ";");
        List<String> serverAddrList = new ArrayList<>(array.length);
        for (String s : array) {
            if (StringUtils.isNotBlank(s)) {
                serverAddrList.add(s);
            }
        }
        String tenant = basicConfig.getOrDefault(NoarkConstant.NACOS_NAMESPACES, "public");
        String username = basicConfig.get(NoarkConstant.NACOS_USERNAME);
        String password = basicConfig.get(NoarkConstant.NACOS_PWD);
        NacosConfigManager.bindServerInfo(new NacosServerInfo(serverAddrList, username, password, tenant));
    }

    @Override
    protected Map<String, String> doLoadConfig(String sid) {
        return NacosConfigManager.getInstance().loadConfig(DEFAULT_DATA_ID, sid);
    }

    @Override
    protected Map<String, String> doLoadConfig() {
        return NacosConfigManager.getInstance().loadConfig(DEFAULT_DATA_ID, DEFAULT_GROUP);
    }
}