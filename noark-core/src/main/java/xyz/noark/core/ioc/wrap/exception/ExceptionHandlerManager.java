package xyz.noark.core.ioc.wrap.exception;

import xyz.noark.core.ioc.wrap.method.BaseMethodWrapper;
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
    private final Map<Class<? extends Throwable>, BaseMethodWrapper> exceptionHandlerMap;
    /**
     * 查询缓存
     */
    private final Map<Class<? extends Throwable>, BaseMethodWrapper> exceptionLookupCache;

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
    public BaseMethodWrapper lookupExceptionHandler(Class<? extends Throwable> exceptionClass) {
        return exceptionLookupCache.computeIfAbsent(exceptionClass, key -> getMappedMethod(exceptionClass));
    }

    private BaseMethodWrapper getMappedMethod(Class<? extends Throwable> exceptionClass) {
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

    public void addExceptionMapping(Class<? extends Throwable> exceptionType, BaseMethodWrapper method) {
        BaseMethodWrapper oldMethod = exceptionHandlerMap.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException("Ambiguous @ExceptionHandler method mapped for [" + exceptionType + "]: {" + oldMethod + ", " + method + "}");
        }
    }
}
