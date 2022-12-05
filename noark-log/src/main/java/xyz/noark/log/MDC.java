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
package xyz.noark.log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * MDC（Mapped Diagnostic Context，映射调试上下文）
 *
 * @author 小流氓[176543888@qq.com]
 * @see 3.4.7
 */
public class MDC {
    private static final ThreadLocal<Map<String, Object>> localMap = new ThreadLocal<>();

    /**
     * 存入一个指定Key与对应Value
     *
     * @param key   指定Key
     * @param value 对应的Value
     */
    public static void put(String key, Object value) {
        Map<String, Object> objectMap = localMap.get();
        if (objectMap == null) {
            objectMap = new HashMap<>();
            localMap.set(objectMap);
        }
        // 把值给存上
        objectMap.put(key, value);
    }

    /**
     * 获取指定Key对应的Value值
     *
     * @param key 指定Key
     * @return 对应的Value值
     */
    public static Object get(String key) {
        Map<String, Object> objectMap = localMap.get();
        return objectMap == null ? null : objectMap.get(key);
    }

    /**
     * 清理所有已存储Key和Value
     */
    public static void clear() {
        localMap.remove();
    }

    /**
     * 复制一个当前存储Key和Value
     *
     * @return 当前存储Key和Value
     */
    public static Map<String, Object> getCopyOfContextMap() {
        Map<String, Object> objectMap = localMap.get();
        return objectMap == null ? Collections.emptyMap() : new HashMap<>(objectMap);
    }
}