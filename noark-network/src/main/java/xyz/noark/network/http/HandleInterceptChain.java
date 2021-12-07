package xyz.noark.network.http;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;
import xyz.noark.core.util.CollectionUtils;

import java.util.List;

/**
 * 这个链的功能待实现.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class HandleInterceptChain {
    @Autowired
    private List<HandlerInterceptor> handlerInterceptorList;

    public boolean triggerPreHandle(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler) throws Exception {
        // 没有拦截器，不拦截
        if (CollectionUtils.isEmpty(handlerInterceptorList)) {
            return false;
        }

        // 拦截器列表，正序执行，有一个为false就拦截，GG
        for (int i = 0, len = handlerInterceptorList.size(); i < len; i++) {
            if (!handlerInterceptorList.get(i).preHandle(request, response, handler)) {
                return true;
            }
        }

        // 全部OK，不拦截
        return false;
    }

    public void triggerPostHandle(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler, Object result) throws Exception {
        // 没有拦截器，不拦截
        if (CollectionUtils.isEmpty(handlerInterceptorList)) {
            return;
        }

        // 拦截器列表，倒序执行，有一个为false就拦截，GG
        for (int i = handlerInterceptorList.size() - 1; i >= 0; i--) {
            handlerInterceptorList.get(i).postHandle(request, response, handler);
        }
    }

    public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler) throws Exception {
        // 没有拦截器，不拦截
        if (CollectionUtils.isEmpty(handlerInterceptorList)) {
            return;
        }

        // 拦截器列表，倒序执行，有一个为false就拦截，GG
        for (int i = handlerInterceptorList.size() - 1; i >= 0; i--) {
            handlerInterceptorList.get(i).afterCompletion(request, response, handler);
        }
    }
}