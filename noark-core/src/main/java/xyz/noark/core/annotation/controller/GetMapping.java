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
package xyz.noark.core.annotation.controller;

import xyz.noark.core.util.StringUtils;

import java.lang.annotation.*;

/**
 * 注解GetMapping用于将Get请求映射到指定处理方法.
 * <p>
 * 这可以理解为一个组合注解，是@RequestMapping(method = RequestMethod.GET)的缩写
 * </p>
 *
 * @author 小流氓[176543888@qq.com]
 * @see RequestMapping
 * @since 3.4
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {

    /**
     * 映射路径.
     *
     * @return 映射路径
     */
    String path();

    /**
     * 指定串行执行的队列ID参数的名称.
     * <p>
     * 这里的队列ID值是指HTTP请求参数中名称，如果找到名称就执行串行化，没有找到则放弃串行执行
     * </p>
     *
     * @return 串行执行的队列ID参数的名称
     */
    String queueId() default StringUtils.EMPTY;

    /**
     * 是否需要打印协议相关的日志.
     *
     * @return 默认为输出日志
     */
    boolean printLog() default true;
}