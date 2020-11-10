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
package xyz.noark.core.ioc.definition.method;

import xyz.noark.core.annotation.Order;
import xyz.noark.core.ioc.MethodDefinition;
import xyz.noark.reflectasm.MethodAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 一个简单的方法定义.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class SimpleMethodDefinition implements MethodDefinition {
    protected final Method method;
    protected final Parameter[] parameters;
    protected final MethodAccess methodAccess;

    public SimpleMethodDefinition(MethodAccess methodAccess, Method method) {
        this.method = method;
        this.methodAccess = methodAccess;
        this.parameters = method.getParameters();
    }

    @Override
    public MethodAccess getMethodAccess() {
        return methodAccess;
    }

    @Override
    public int getMethodIndex() {
        return methodAccess.getIndex(method.getName(), method.getParameterTypes());
    }

    @Override
    public Parameter[] getParameters() {
        return parameters;
    }

    @Override
    public Order getOrder() {
        return method.getAnnotation(Order.class);
    }

    /**
     * 返回当前方法是否为过期的
     *
     * @return 如果标识已过期则返回true.
     */
    public boolean isDeprecated() {
        return method.isAnnotationPresent(Deprecated.class);
    }

    /**
     * 获取这个JDK方法引用.
     *
     * @return 获取方法引用
     */
    public Method getMethod() {
        return method;
    }
}