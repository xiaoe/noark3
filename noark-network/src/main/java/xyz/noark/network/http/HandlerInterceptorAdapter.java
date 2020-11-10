package xyz.noark.network.http;

import xyz.noark.core.network.HandlerMethod;

/**
 * 拦截器的适配器
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class HandlerInterceptorAdapter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {

    }
}