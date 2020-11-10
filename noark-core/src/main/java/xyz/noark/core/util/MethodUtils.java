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
package xyz.noark.core.util;

import xyz.noark.core.exception.NoPublicMethodException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 方法工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class MethodUtils {

    /**
     * 强制调用一个方法{@link Method}.
     *
     * @param target 目标对象
     * @param method 要调用的方法
     * @param args   方法参数
     * @return 返回方法的返回值
     */
    public static Object invoke(final Object target, final Method method, final Object... args) {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("反射调用方式时出现了异常情况...", e);
        }
    }

    /**
     * 获取指定类的所有方法，包含父类的方法.
     *
     * @param klass 指定类
     * @return 指定类的方法集合.
     */
    public static List<Method> getAllMethod(final Class<?> klass) {
        Set<Method> result = new HashSet<>();
        for (Class<?> target = klass; target != Object.class; target = target.getSuperclass()) {
            for (Method method : target.getDeclaredMethods()) {
                result.add(method);
            }
        }
        return new ArrayList<>(result);
    }

    /**
     * 获取指定类中的指定名称和参数的方法
     *
     * @param klass          类
     * @param name           方法名称
     * @param parameterTypes 方法参数
     * @return 方法
     */
    public static Method getMethod(Class<?> klass, String name, Class<?>... parameterTypes) {
        try {
            return klass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new NoPublicMethodException(e.getMessage());
        }
    }

    /**
     * 判定指定类是否存在Set方法.
     *
     * @param klass 指定类
     * @return 如果存在则返回true, 否则返回false.
     */
    public static boolean existSetMethod(Class<?> klass) {
        for (Class<?> target = klass; target != Object.class; target = target.getSuperclass()) {
            for (Method method : target.getDeclaredMethods()) {
                if (method.getName().startsWith("set")) {
                    return true;
                }
            }
        }
        return false;
    }
}