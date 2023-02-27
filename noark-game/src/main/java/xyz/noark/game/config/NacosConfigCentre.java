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

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.util.HttpUtils;
import xyz.noark.core.util.MapUtils;
import xyz.noark.core.util.RandomUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.game.NoarkConstant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * 基于Nacos实现的配置中心.
 *
 * @author 小流氓[176543888@qq.com]
 */
public class NacosConfigCentre extends AbstractConfigCentre {
    /**
     * Nacos中dataId参数，对应游戏里就是配置文件名称
     */
    private static final String DEFAULT_DATA_ID = "application.properties";
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private final List<String> serverAddrList;
    private final String username;
    private final String password;
    private final String tenant;

    public NacosConfigCentre(HashMap<String, String> basicConfig) {
        String addr = basicConfig.getOrDefault(NoarkConstant.NACOS_SERVER_ADDR, "127.0.0.1:8848");
        String[] array = StringUtils.split(addr, ";");
        List<String> serverAddr = new ArrayList<>(array.length);
        for (String s : array) {
            if (StringUtils.isNotBlank(s)) {
                serverAddr.add(s);
            }
        }
        this.serverAddrList = serverAddr;

        this.tenant = basicConfig.getOrDefault(NoarkConstant.NACOS_NAMESPACES, "public");
        this.username = basicConfig.get(NoarkConstant.NACOS_USERNAME);
        this.password = basicConfig.get(NoarkConstant.NACOS_PASSWORD);
    }

    @Override
    protected Map<String, String> doLoadConfig(String sid) {
        return this.nacosLoadConfig(DEFAULT_DATA_ID, sid);
    }

    @Override
    protected Map<String, String> doLoadConfig() {
        return this.nacosLoadConfig(DEFAULT_DATA_ID, DEFAULT_GROUP);
    }

    private Map<String, String> nacosLoadConfig(String dataId, String group) {
        String serverAddr = RandomUtils.randomList(serverAddrList);
        String url;
        if (StringUtils.isNotEmpty(username)) {
            url = StringUtils.join("http://", serverAddr, "/nacos/v1/cs/configs?dataId=", dataId, "&group=", DEFAULT_GROUP, "&tenant=", tenant, "&username=", username, "&password=", password);
        } else {
            url = StringUtils.join("http://", serverAddr, "/nacos/v1/cs/configs?dataId=", dataId, "&group=", DEFAULT_GROUP, "&tenant=", tenant);
        }

        try {
            return toMap(HttpUtils.get(url));
        }
        // 未配置指定dataId的配置文件
        catch (FileNotFoundException e) {
            // 通用配置那是必需要存在
            if (DEFAULT_GROUP.equalsIgnoreCase(group)) {
                throw new ServerBootstrapException("加载Nacos配置中心配置时文件不存在", e);
            }
            // 本服私服可能可以为空的
            return Collections.emptyMap();
        }
        // 未知情况
        catch (IOException e) {
            throw new ServerBootstrapException("加载Nacos配置中心配置时发生了异常情况", e);
        }
    }

    public Map<String, String> toMap(String result) {
        String[] allLine = StringUtils.split(result, "\n");
        Map<String, String> configMap = MapUtils.newHashMap(allLine.length);
        for (String line : allLine) {
            // 空行跳过
            if (line == null) {
                continue;
            }

            String lineValue = line.trim();
            // 注释跳过
            if (lineValue.startsWith("#")) {
                continue;
            }

            // 使用=号切割两2份
            String[] array = StringUtils.split(lineValue, "=", 2);
            if (array.length == 1) {
                configMap.put(array[0], "");
            } else if (array.length == 2) {
                configMap.put(array[0], array[1]);
            }
        }
        return configMap;
    }
}