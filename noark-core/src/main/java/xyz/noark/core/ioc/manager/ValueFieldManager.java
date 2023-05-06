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
package xyz.noark.core.ioc.manager;

import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.ioc.wrap.field.ValueFieldWrapper;
import xyz.noark.core.util.MapUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * &#064;Value注入的配置属性管理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.8
 */
public class ValueFieldManager {
    private static final Map<String, List<ValueFieldWrapper>> fieldMap = MapUtils.newConcurrentHashMap(256);

    public static void register(String key, ValueFieldWrapper wrapper) {
        fieldMap.computeIfAbsent(key, k -> new LinkedList<>()).add(wrapper);
    }

    /**
     * 重新刷新&#064;Value的属性注入
     */
    public static void refresh() {
        // 重载配置
        EnvConfigHolder.reload();

        // 开始刷新
        for (List<ValueFieldWrapper> wrappers : fieldMap.values()) {
            for (ValueFieldWrapper wrapper : wrappers) {
                // 标识了要刷新，才会刷会，其他的忽略
                if (wrapper.isAutoRefreshed()) {
                    wrapper.refresh();
                }
            }
        }
    }

    /**
     * 重置Value属性要不要响应自动刷新.
     * <p>
     * 留给脚本救火的，哪怕你忘了加自动刷新，也可以在线补救
     *
     * @param key             &#064;Value中的Key
     * @param isAutoRefreshed 是否自动刷新的状态
     */
    public static void resetAutoRefreshed(String key, boolean isAutoRefreshed) {
        for (ValueFieldWrapper wrapper : fieldMap.getOrDefault(key, Collections.emptyList())) {
            wrapper.setAutoRefreshed(isAutoRefreshed);
        }
    }
}
