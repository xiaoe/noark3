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
package xyz.noark.core.annotation.controller;

import xyz.noark.core.event.Event;
import xyz.noark.core.event.FixedTimeEvent;

import java.lang.annotation.*;

/**
 * 事件监听器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

    /**
     * 监听的事件源类型.
     * <p>
     * 处理方法可以是此事件源的父类或接口，此参数优先级高于参数<br>
     * 如果没有配置，默认使用事件参数的类型作为事件源类型判定<br>
     * 如果事件处理方法参数为空，此处申明就是很有必需的
     *
     * @return 事件对象的Class类型
     */
    Class<? extends Event> value() default Event.class;

    /**
     * 是否异步执行
     * <p>
     * 默认就是异步执行的，当需要同步时可以设计false<br>
     * 当事件源为{@link FixedTimeEvent}类型时，同步无效
     *
     * @return 如果为true异步执行, 否则同步执行
     */
    boolean async() default true;

    /**
     * 是否需要打印协议相关的日志.
     *
     * @return 默认为输出日志
     */
    boolean printLog() default true;
}