/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.network.http;

import java.util.Enumeration;
import java.util.Map;

/**
 * 一个请求接口.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public interface HttpServletRequest {

    /**
     * 获取请求的URI
     *
     * @return 请求的URI
     */
    String getUri();

    /**
     * 获取请求的IP地址.
     *
     * @return IP地址
     */
    String getRemoteAddr();

    /**
     * 获取请求的方式.
     * <p>比如GET,POST等等</p>
     *
     * @return 请求的方式
     * @see xyz.noark.core.annotation.controller.RequestMethod
     */
    String getMethod();

    /**
     * 获取Request指定名称的参数.
     *
     * @param name 指定名称
     * @return 指定名称的参数值，参数不存在返回null
     */
    String getParameter(String name);

    /**
     * 获取所有参数名称.
     *
     * @return 所有参数名称
     */
    Enumeration<String> getParameterNames();

    /**
     * 根据参数名称获取对应的值数组.
     *
     * @param name 参数名称
     * @return 对应的值数组
     */
    String[] getParameterValues(String name);

    /**
     * 获取所有参数映射关系.
     *
     * @return 参数映射关系
     */
    Map<String, String[]> getParameterMap();
}