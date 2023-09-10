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

import xyz.noark.core.exception.ExceptionHelper;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.manager.ValueFieldManager;
import xyz.noark.core.lang.PairHashMap;
import xyz.noark.core.util.HttpUtils;
import xyz.noark.core.util.MapUtils;
import xyz.noark.core.util.Md5Utils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.log.Logger;
import xyz.noark.log.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Nacos配置管理类
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.8
 */
public class NacosConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(NacosConfigManager.class);
    private static final NacosConfigManager instance = new NacosConfigManager();
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    /**
     * Nacos服务器信息配置
     */
    private NacosServerInfo serverInfo;
    /**
     * 监听线程
     */
    private NacosListenerThread thread;
    /**
     * 缓存目标配置的Md5值，用于监听配置变化, KeyL = dataId, KeyR = group, Value = Md5
     */
    private final PairHashMap<String, String, String> cacheConfigMd5Map = new PairHashMap<>(2);

    private NacosConfigManager() {
    }

    public static NacosConfigManager getInstance() {
        return instance;
    }

    public static void bindServerInfo(NacosServerInfo serverInfo) {
        instance.serverInfo = serverInfo;
    }

    // -----------------------------载入配置-----------------------------

    /**
     * 加载配置
     *
     * @param dataId 文件名称
     * @param group  分组编号
     * @return 返回文件对应的配置信息
     */
    Map<String, String> loadConfig(String dataId, String group) {
        String serverAddr = serverInfo.randomServerAddr();
        String url;
        if (StringUtils.isNotEmpty(serverInfo.getUsername())) {
            url = StringUtils.join("http://", serverAddr, "/nacos/v1/cs/configs?dataId=", dataId, "&group=", group, "&tenant=", serverInfo.getTenant(), "&username=", serverInfo.getUsername(), "&password=", serverInfo.getPassword());
        } else {
            url = StringUtils.join("http://", serverAddr, "/nacos/v1/cs/configs?dataId=", dataId, "&group=", group, "&tenant=", serverInfo.getTenant());
        }

        try {
            String config = HttpUtils.get(url);

            // 缓存配置并尝试启动监听线程
            this.cacheConfigAndStartListener(dataId, group, config);

            return toMap(config);
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

    /**
     * 缓存配置并尝试启动监听线程
     *
     * @param dataId Nacos之dataId
     * @param group  Nacos之group
     * @param config 配置内容
     */
    public void cacheConfigAndStartListener(String dataId, String group, String config) {
        String md5 = Md5Utils.encrypt(config).toLowerCase();
        cacheConfigMd5Map.put(dataId, group, md5);
        logger.debug("config md5 cache dataId={}, group={}, md5={}", dataId, group, md5);

        // 载入配置后要启动监听线程
        if (thread == null) {
            this.startConfigListenerThread();
        }
    }

    private void startConfigListenerThread() {
        this.thread = new NacosListenerThread();
        this.thread.start();
        logger.info("nacos config listener thread start.");
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

    // -----------------------------监听配置-----------------------------
    /**
     * 一次监听超时时间，单位：毫秒，默认值：30秒
     */
    private static final int timeout = 30000;
    /**
     * 监听头部参数
     */
    private static final Map<String, String> listenerHeader = MapUtils.of("Long-Pulling-Timeout", String.valueOf(timeout));
    /**
     * 字段分隔符
     */
    private static final String FIELD_SEPARATOR = "%02";
    /**
     * 配置分隔符
     */
    private static final String CONFIG_SEPARATOR = "%01";

    /**
     * 监听业务逻辑的过程
     */
    long processListener() {
        logger.debug("config start listener ...");
        try {
            return doProcessListener();
        }
        // 发生了意外情况
        catch (Throwable e) {
            // 记录并上报...
            logger.debug("config listener exception={}", e);
            ExceptionHelper.monitor(e);
            return timeout;
        }
    }

    private long doProcessListener() {
        String url = StringUtils.join("http://", serverInfo.randomServerAddr(), "/nacos/v1/cs/configs/listener");
        // 有配置账号密码
        if (StringUtils.isNotEmpty(serverInfo.getUsername())) {
            url = StringUtils.join(url, "?username=", serverInfo.getUsername(), "&password=", serverInfo.getPassword());
        }

        // Listening-Configs=dataId^2Group^2contentMD5^2tenant^1
        // Listening-Configs=dataId%02group%02contentMD5%02tenant%01
        StringBuilder sb = new StringBuilder(128);
        sb.append("Listening-Configs=");
        cacheConfigMd5Map.forEach((k, v) -> {
            sb.append(k.getLeft()).append(FIELD_SEPARATOR);
            sb.append(k.getRight()).append(FIELD_SEPARATOR);
            sb.append(v).append(FIELD_SEPARATOR);
            sb.append(serverInfo.getTenant()).append(CONFIG_SEPARATOR);
        });

        String result = HttpUtils.post(url, sb.toString(), timeout, listenerHeader);
        if (StringUtils.isNotEmpty(result)) {
            logger.debug("config changes, start refreshing...");
            ValueFieldManager.refresh();
        }

        // 正常结束的，不延迟
        return 0;
    }
}
