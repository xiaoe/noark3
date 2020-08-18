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

import xyz.noark.core.annotation.controller.RequestMethod;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;
import xyz.noark.core.util.StringUtils;

import java.util.*;

/**
 * HTTP方法管理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class HttpMethodManager {
    /**
     * 请求方式->path-->处理器
     */
    private static final EnumMap<RequestMethod, Map<String, HttpMethodWrapper>> HANDLER_MAP = new EnumMap<>(RequestMethod.class);

    private HttpMethodManager() {
    }

    /**
     * 注册HTTP请求处理器.
     *
     * @param handler HTTP请求处理器
     */
    public static void registerHandler(HttpMethodWrapper handler) {
        // URI不能有空格...
        if (handler.getPath().indexOf(StringUtils.SPACE) != -1) {
            throw new ServerBootstrapException("URI中发现空格：" + handler.getPath());
        }

        // 获取所有可访问试，如果没有那就全部可以
        Collection<RequestMethod> methodSet = handler.getMethodSet();
        if (methodSet.isEmpty()) {
            methodSet = Arrays.asList(RequestMethod.values());
        }

        // 所有方式都要记录
        for (RequestMethod v : methodSet) {
            Map<String, HttpMethodWrapper> handlers = HANDLER_MAP.computeIfAbsent(v, key -> new HashMap<>(512));
            // 重复定义的URI
            if (handlers.containsKey(handler.getPath())) {
                throw new ServerBootstrapException("重复定义的URI：" + handler.getPath());
            }
            // 存档备用
            handlers.put(handler.getPath(), handler);
        }
    }

    /**
     * 获取指定访问方式的路径处理器.
     *
     * @param method 访问方式，GET，POST
     * @param path   路径
     * @return 获取指定访问方式的路径处理器
     */
    public static HttpMethodWrapper getHttpHandler(String method, String path) {
        RequestMethod requestMethod = RequestMethod.valueOf(method.toUpperCase());
        return HANDLER_MAP.getOrDefault(requestMethod, Collections.emptyMap()).get(path);
    }
}