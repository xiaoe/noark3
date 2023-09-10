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

import xyz.noark.core.annotation.ExceptionHandler;
import xyz.noark.reflectasm.MethodAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 异常处理入口的定义.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class ExceptionMethodDefinition extends SimpleMethodDefinition {
    private final List<Class<? extends Throwable>> exceptionClassList;

    @SuppressWarnings("unchecked")
    public ExceptionMethodDefinition(MethodAccess methodAccess, Method method, ExceptionHandler exceptionHandler) {
        super(methodAccess, method);

        Class<? extends Throwable>[] classArray = exceptionHandler.value();
        // 如果注解里没有配置，那就尝试分析参数里中的事件对象类型
        if (classArray.length == 0) {
            List<Class<? extends Throwable>> classList = new ArrayList<>();
            // 遍历去找异常配置的类型
            for (Parameter parameter : parameters) {
                if (Throwable.class.isAssignableFrom(parameter.getType())) {
                    classList.add((Class<? extends Throwable>) parameter.getType());
                }
            }
            this.exceptionClassList = classList;
        }
        //直接使用注解里配置项
        else {
            this.exceptionClassList = Arrays.asList(classArray);
        }
    }

    public List<Class<? extends Throwable>> getExceptionClassList() {
        return exceptionClassList;
    }
}