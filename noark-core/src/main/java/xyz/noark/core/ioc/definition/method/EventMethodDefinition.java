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
package xyz.noark.core.ioc.definition.method;

import xyz.noark.core.annotation.PlayerId;
import xyz.noark.core.annotation.controller.EventListener;
import xyz.noark.core.event.Event;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.definition.ControllerBeanDefinition;
import xyz.noark.reflectasm.MethodAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 事件处理入口的定义.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class EventMethodDefinition extends SimpleMethodDefinition {
    private final EventListener eventListener;
    private final Class<? extends Event> eventClass;

    @SuppressWarnings("unchecked")
    public EventMethodDefinition(MethodAccess methodAccess, Method method, EventListener eventListener, ControllerBeanDefinition beanDefinition) {
        super(methodAccess, method);
        this.eventListener = eventListener;

        final String className = beanDefinition.getBeanClass().getName();

        // 如果注解里没有配置，那就尝试分析参数里中的事件对象类型
        Class<? extends Event> eventClass = eventListener.value();
        if (eventClass == Event.class) {
            // 遍历去找事件源的类型
            for (Parameter parameter : parameters) {
                // 事件类型的对象
                if (Event.class.isAssignableFrom(parameter.getType())) {
                    if (eventClass == Event.class) {
                        eventClass = (Class<? extends Event>) parameter.getType();
                    } else {
                        throw new ServerBootstrapException("事件监听处理方法，有且只能有一个事件类型的参数 class=" + className + ", method=" + method.getName());
                    }
                }
                // 玩家ID
                else if (!parameter.isAnnotationPresent(PlayerId.class)) {
                    throw new ServerBootstrapException("事件监听处理方法，出现非@PlayerId非事件的参数 class=" + className + ", method=" + method.getName() + ", parameter=" + parameter.getName());
                }
            }

            // 没配事件类型，还没参数
            if (eventClass == Event.class) {
                throw new ServerBootstrapException("事件监听处理方法，没有申请事件类型，也没有事件参数 class=" + className + ", method=" + method.getName());
            }
        }
        // 指定了事件类型，方法也要验证一下
        else {
            for (Parameter parameter : parameters) {
                if (!Event.class.isAssignableFrom(parameter.getType()) && !parameter.isAnnotationPresent(PlayerId.class)) {
                    throw new ServerBootstrapException("事件监听处理方法，出现非@PlayerId非事件的参数 class=" + className + ", method=" + method.getName() + ", parameter=" + parameter.getName());
                }
            }
        }

        this.eventClass = eventClass;
    }

    public Class<? extends Event> getEventClass() {
        return eventClass;
    }

    public boolean isAsync() {
        return eventListener.async();
    }

    public boolean isPrintLog() {
        return eventListener.printLog();
    }
}