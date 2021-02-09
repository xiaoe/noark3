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
package xyz.noark.core.env;

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.util.BooleanUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 系统配置.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class EnvConfigHolder {

    /**
     * 处理过的系统配置参数...
     */
    private static Map<String, String> properties;

    public static Map<String, String> getProperties() {
        return properties == null ? Collections.emptyMap() : properties;
    }

    /**
     * 根据Key值去配置中获取一个String值
     *
     * @param key Key值
     * @return String值（可能会为null）
     */
    public static String getString(String key) {
        return getProperties().get(key);
    }

    /**
     * 根据Key值去配置中获取一个Boolean值
     *
     * @param key Key值
     * @return Boolean值
     */
    public static boolean getBoolean(String key) {
        return BooleanUtils.toBoolean(getString(key));
    }

    public static void setProperties(Map<String, String> properties) {
        EnvConfigHolder.properties = properties;
    }

    /**
     * 填充EL表达式.
     *
     * @param value 包含表达式的值
     * @return 替换完的值
     */
    public static String fillExpression(String value) {
        return fillExpression(value, properties, false);
    }

    /**
     * 填充EL表达式.
     *
     * @param value    包含表达式的值
     * @param config   表达式替换配置
     * @param required 表达替换必需存在
     * @return 替换完的值
     */
    public static String fillExpression(String value, Map<String, String> config, boolean required) {
        if (value == null) {
            return null;
        }
        int startIndex = value.indexOf("${");
        while (startIndex >= 0) {
            int endIndex = value.indexOf("}", startIndex);
            if (endIndex > 0) {
                String elKey = value.substring(startIndex + 2, endIndex);
                String elValue = config.get(elKey);
                if (elValue == null) {
                    if (required) {
                        throw new ServerBootstrapException(value + "--> 替换参数呢?");
                    }
                } else {
                    value = value.replace("${" + elKey + "}", elValue);
                }
            }
            startIndex = value.indexOf("${", startIndex + 1);
        }
        return value;
    }
}