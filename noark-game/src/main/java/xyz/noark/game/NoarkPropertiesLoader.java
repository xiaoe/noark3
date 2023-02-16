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
package xyz.noark.game;

import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.lang.UnicodeInputStream;
import xyz.noark.core.util.BooleanUtils;
import xyz.noark.core.util.ClassUtils;
import xyz.noark.core.util.MapUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.game.config.ConfigCentre;
import xyz.noark.game.config.NacosConfigCentre;
import xyz.noark.game.crypto.StringEncryptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import static xyz.noark.log.LogHelper.logger;

/**
 * 属性文件加载器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
class NoarkPropertiesLoader {
    private static final String BOOTSTRAP_PREFIX = "bootstrap";
    private static final String APPLICATION_PREFIX = "application";
    private static final String PROPERTIES_SUFFIX = ".properties";

    private final ClassLoader loader;
    /**
     * 所有配置
     */
    private final HashMap<String, String> properties;

    NoarkPropertiesLoader() {
        this.loader = this.getClass().getClassLoader();
        this.properties = MapUtils.newHashMap(128);
        // 系统配置
        properties.put(NoarkConstant.NOARK_VERSION, Noark.getVersion());
    }

    /**
     * 加载启动命令行的参数.
     * <p>命令行参数优先级 大于 配置文件优先级</p>
     * Noark在启动时命令行参数需要使用双杠--指定，参数即为配置文件中的参数配置相同！
     *
     * @param args 启动命令行的参数
     */
    public void loadingArgs(String... args) {
        for (String arg : args) {
            if (arg.startsWith("--")) {
                int index = arg.indexOf('=', 2);
                if (index > 1) {
                    String optionName = arg.substring(2, index);
                    String optionValue = arg.substring(index + 1);
                    if (StringUtils.isBlank(optionName) || StringUtils.isBlank(optionValue)) {
                        throw new IllegalArgumentException("Invalid argument syntax: " + arg);
                    }
                    this.properties.put(optionName, optionValue);
                }
            }
        }
    }


    /**
     * 加载系统配置文件中的内容.
     * <p>
     * application-test.properties中的内容会覆盖application.properties中的配置
     */
    void loadingProperties() {
        // --noark.profiles.active=dev/prod/test     # 指定运行环境
        String profile = properties.getOrDefault(NoarkConstant.NOARK_PROFILES_ACTIVE, "test");

        // 优先载入bootstrap.properties
        properties.putAll(loadingFile(BOOTSTRAP_PREFIX, profile));

        // 然后再载入application.properties
        properties.putAll(loadingFile(APPLICATION_PREFIX, profile));

        // 加载配置完成后
        this.loadingConfigAfter(properties);
    }

    /**
     * 加载完配置之后的逻辑，主要是对一些解密与表达式的处理
     *
     * @param result 配置
     */
    private void loadingConfigAfter(HashMap<String, String> result) {
        // 密文解密
        final StringEncryptor encryptor = new StringEncryptor(result);
        for (Map.Entry<String, String> e : result.entrySet()) {
            e.setValue(encryptor.decrypt(e.getValue()));
        }

        // 表达式引用...
        for (Map.Entry<String, String> e : result.entrySet()) {
            e.setValue(EnvConfigHolder.fillExpression(e.getValue(), result, true));
        }
    }

    private HashMap<String, String> loadingFile(String filename, String profile) {
        HashMap<String, String> config = MapUtils.newHashMap(128);
        this.loadingFile(filename + PROPERTIES_SUFFIX, config);

        // 加载指定的Profile
        if (StringUtils.isNotEmpty(profile)) {
            loadingFile(filename + "-" + profile + PROPERTIES_SUFFIX, config);
        }
        return config;
    }

    /**
     * 加载配置文件.
     * Jar包同级目录的配置优先级应该高于Jar内的配置文件
     *
     * @param filename 配置文件名称
     * @param config   收集配置项Map
     */
    private void loadingFile(String filename, Map<String, String> config) {
        // 1. Jar包同级目录的配置优先
        File file = new File(System.getProperty("user.dir"), filename);
        if (file.exists()) {
            try (InputStream in = Files.newInputStream(file.toPath())) {
                this.loadingFileByInputStream(in, config);
            } catch (IOException e) {
                throw new ServerBootstrapException("配置文件格式异常... filename=" + filename);
            }
        }
        // 2. Jar的同级目录中不存在此配置文件，再尝试到类路径下找找
        else {
            try (InputStream in = loader.getResourceAsStream(filename)) {
                if (in == null) {
                    return;
                }
                this.loadingFileByInputStream(in, config);
            } catch (IOException e) {
                throw new ServerBootstrapException("配置文件格式异常... filename=" + filename);
            }
        }
    }

    private void loadingFileByInputStream(InputStream in, Map<String, String> config) throws IOException {
        // 使用UnicodeInputStream处理带有BOM的配置
        try (UnicodeInputStream uis = new UnicodeInputStream(in, "UTF-8"); InputStreamReader isr = new InputStreamReader(uis, uis.getEncoding())) {
            Properties props = new Properties();
            props.load(isr);

            for (Entry<Object, Object> e : props.entrySet()) {
                String key = e.getKey().toString().trim();

                // 有更高优化级的配置，忽略这个配置
                if (properties.containsKey(key)) {
                    continue;
                }

                // 收录这个新的配置
                String value = e.getValue().toString().trim();
                if (config.put(key, value) != null) {
                    System.err.println("覆盖配置 >>" + key + "=" + value);
                }
            }
        }
    }

    /**
     * 加载配置中心的配置，本地配置会覆盖远程配置
     */
    public void loadingConfigCentre() {
        // Noark的配置中心
        if (BooleanUtils.toBoolean(properties.get(NoarkConstant.CONFIG_CENTRE_ENABLED))) {
            this.loadNoarkConfigCentre(properties);
            this.loadingConfigAfter(properties);
        }
        // Nacos配置中心(老版本，日后要移除)
        else if (BooleanUtils.toBoolean(properties.get(NoarkConstant.NACOS_ENABLED))) {
            this.loadNacosConfigCentre(properties);
            this.loadingConfigAfter(properties);
        }
    }

    /**
     * 加载配置中心的配置(本地配置会覆盖远程配置)
     *
     * @param result 本地配置
     */
    private void loadNoarkConfigCentre(HashMap<String, String> result) {
        // 开启了配置中心，那就拿着区别ID，去取配置中心的所有配置
        String sid = result.get(NoarkConstant.SERVER_ID);
        if (StringUtils.isEmpty(sid)) {
            throw new ServerBootstrapException("application.properties文件中必需要配置区服ID," + NoarkConstant.SERVER_ID + "=XXX");
        }
        String className = result.getOrDefault(NoarkConstant.CONFIG_CENTRE_CLASS, "xyz.noark.game.config.NacosConfigCentre");
        logger.info("正在启动配置中心模式 sid={}, className={}", sid, className);
        // 尝试创建Redis的配置中心读取配置
        ConfigCentre cc = ClassUtils.newInstance(className, result);
        // 本地配置会覆盖远程配置
        cc.loadConfig(sid).forEach(result::putIfAbsent);
    }

    /**
     * 加载配置中心的配置(本地配置会覆盖远程配置)
     *
     * @param result 本地配置
     */
    private void loadNacosConfigCentre(HashMap<String, String> result) {
        // 开启了配置中心，那就拿着区别ID，去取配置中心的所有配置
        String sid = result.get(NoarkConstant.SERVER_ID);
        if (StringUtils.isEmpty(sid)) {
            throw new ServerBootstrapException("application.properties文件中必需要配置区服ID," + NoarkConstant.SERVER_ID + "=XXX");
        }
        logger.info("正在启动配置中心模式 sid={}", sid);
        // 尝试创建Redis的配置中心读取配置
        ConfigCentre cc = new NacosConfigCentre(result);
        // 本地配置会覆盖远程配置
        cc.loadConfig(sid).forEach(result::putIfAbsent);
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}