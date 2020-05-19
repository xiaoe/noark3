package xyz.noark.network.http;

import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;

/**
 * 视图解析器
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public interface ViewResolver {

    void resolveView(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler, Object result);

    void resolveException(HttpServletResponse response, Throwable cause);
}
