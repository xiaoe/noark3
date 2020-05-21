package xyz.noark.network.http;

import xyz.noark.core.network.HandlerMethod;

/**
 * HTTP拦截器接口.
 * <p>这个也跟Spring的差不多，但也不完成一样，全局拦截</p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public interface HandlerInterceptor {

    /**
     * 业务执行前的逻辑，返回值决定是否执行后续逻辑.
     * <p>
     * 执行前提：没有发生404或处理方法上没有过期注解<br>
     * 执行时机：业务方法处理前<br>
     * 执行顺序：所有拦截器按Order声明顺序<b>正序</b>执行<br>
     * 返回值：如果返回true，执行下一个拦截器，如果为false，则中断后续逻辑。注意：中断是不会执行postHandle和afterCompletion<br>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  业务处理器
     * @return 如果返回true表示继续后续逻辑
     * @throws Exception 有可能会抛出未知异常
     */
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception;

    /**
     * 业务执行后的逻辑。
     * <p>
     * 执行前提：preHandle返回true且没有异常（包含业务中也没有异常）<br>
     * 执行时机：业务方法处理后，对结果渲染前<br>
     * 执行顺序：所有拦截器按Order声明顺序<b>倒序</b>执行<br>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  业务处理器
     * @throws Exception 有可能会抛出未知异常
     */
    void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception;

    /**
     * <p>
     * 执行前提：preHandle返回true且没有异常(业务有异常也会执行噢)<br>
     * 执行时机：对结果渲染后<br>
     * 执行顺序：所有拦截器按Order声明顺序<b>倒序</b>执行<br>
     * 备注：主要用于清理一些资源，统计
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  业务处理器
     * @throws Exception 有可能会抛出未知异常
     */
    void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception;
}
