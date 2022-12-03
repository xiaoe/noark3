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
