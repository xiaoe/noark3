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
package xyz.noark.core.ioc.wrap.exception;

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.wrap.method.BaseMethodWrapper;
import xyz.noark.core.ioc.wrap.method.ExceptionMethodWrapper;
import xyz.noark.core.util.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异常处理管理器
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class ExceptionHandlerManager {
    /**
     * 异常处理器
     */
    private final Map<Class<? extends Throwable>, ExceptionMethodWrapper> exceptionHandlerMap;
    /**
     * 查询缓存
     */
    private final Map<Class<? extends Throwable>, ExceptionMethodWrapper> exceptionLookupCache;

    public ExceptionHandlerManager() {
        this.exceptionHandlerMap = MapUtils.newHashMap(16);
        this.exceptionLookupCache = new ConcurrentHashMap<>();
    }

    /**
     * 查找最优处理器
     *
     * @param exceptionClass 异常类型
     * @return 最优处理器
     */
    public ExceptionMethodWrapper lookupExceptionHandler(Class<? extends Throwable> exceptionClass) {
        return exceptionLookupCache.computeIfAbsent(exceptionClass, key -> getMappedMethod(exceptionClass));
    }

    private ExceptionMethodWrapper getMappedMethod(Class<? extends Throwable> exceptionClass) {
        // 找到所有能处理此异常的选项
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : exceptionHandlerMap.keySet()) {
            if (mappedException.isAssignableFrom(exceptionClass)) {
                matches.add(mappedException);
            }
        }

        // 有匹配处理项，那要找一个最优的
        if (!matches.isEmpty()) {
            matches.sort(new ExceptionDepthComparator(exceptionClass));
            return exceptionHandlerMap.get(matches.get(0));
        }

        // 没有就算了
        return null;
    }

    public void addExceptionMapping(Class<? extends Throwable> exceptionType, ExceptionMethodWrapper method) {
        BaseMethodWrapper oldMethod = exceptionHandlerMap.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new ServerBootstrapException("Ambiguous @ExceptionHandler method mapped for [" + exceptionType + "]: {" + oldMethod + ", " + method + "}");
        }
    }
}
