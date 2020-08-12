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

import java.lang.reflect.Method;

/**
 * Class工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ClassUtils {

    /**
     * 使用当前线程的ClassLoader加载给定的类
     *
     * @param className 类的全称
     * @return 给定的类
     */
    public static Class<?> loadClass(String className) {
        // ClassLoader#loadClass(String)：将.class文件加载到JVM中，不会执行static块,只有在创建实例时才会去执行static块
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
        }

        // Class#forName(String)：将.class文件加载到JVM中，还会对类进行解释，并执行类中的static块
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
        }

        throw new RuntimeException("无法加载指定类名的Class=" + className);
    }

    /**
     * 创建一个指定类的对象,调用默认的构造函数.
     *
     * @param <T>   Class
     * @param klass 类
     * @return 指定类的对象
     */
    public static <T> T newInstance(final Class<T> klass) {
        try {
            return klass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("无法创建实例. Class=" + klass.getName(), e);
        }
    }

    /**
     * 根据ClassName和构造方法的参数列表来创建一个对象
     *
     * @param <T>        Class
     * @param className  指定类全名（包含包名称的那种）
     * @param parameters 参数列表
     * @return 指定ClassName的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className, Object... parameters) {
        Class<?> klass = (Class<?>) loadClass(className);
        try {
            Class<?>[] parameterTypes = new Class<?>[parameters.length];
            for (int i = 0, len = parameters.length; i < len; i++) {
                parameterTypes[i] = parameters[i].getClass();
            }
            return (T) klass.getConstructor(parameterTypes).newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException("无法创建实例. Class=" + klass.getName(), e);
        }
    }

    /**
     * 尝试运行一个带有Main方法的类.
     *
     * @param mainClass 带有Main方法类的名称
     * @param args      启动参数数组
     */
    public static void invokeMain(String mainClass, String[] args) {
        final Class<?> klass = ClassUtils.loadClass(mainClass);
        Method mainMethod = MethodUtils.getMethod(klass, "main", String[].class);
        MethodUtils.invoke(null, mainMethod, new Object[]{args});
    }

}