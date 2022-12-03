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
package xyz.noark.core.ioc.wrap.exception;

import xyz.noark.core.ioc.wrap.MethodWrapper;
import xyz.noark.core.ioc.wrap.method.ExceptionMethodWrapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 异常处理选择器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class ExceptionHandlerSelector {
    private static final ExceptionHandlerSelector instance = new ExceptionHandlerSelector();
    /**
     * 全局异常处理器
     */
    private final ExceptionHandlerManager globalExceptionManager;
    /**
     * Controller所在异常处理器
     */
    private final ConcurrentMap<Class<?>, ExceptionHandlerManager> controllerExceptionManager;

    private ExceptionHandlerSelector() {
        this.globalExceptionManager = new ExceptionHandlerManager();
        this.controllerExceptionManager = new ConcurrentHashMap<>();
    }

    /**
     * 注册Controller类的异常处理器.
     *
     * @param controllerClass Controller类
     * @param emd             异常处理器
     */
    public static void registerExceptionHandler(Class<?> controllerClass, ExceptionMethodWrapper emd) {
        ExceptionHandlerManager manager = instance.controllerExceptionManager.computeIfAbsent(controllerClass, key -> new ExceptionHandlerManager());
        for (Class<? extends Throwable> exceptionClass : emd.getExceptionClassArray()) {
            manager.addExceptionMapping(exceptionClass, emd);
        }
    }

    /**
     * 注册全局的异常处理器.
     *
     * @param emd 异常处理器
     */
    public static void registerExceptionHandler(ExceptionMethodWrapper emd) {
        for (Class<? extends Throwable> exceptionClass : emd.getExceptionClassArray()) {
            instance.globalExceptionManager.addExceptionMapping(exceptionClass, emd);
        }
    }

    /**
     * 选择指定类型的异常处理器.
     *
     * @param controllerClass 指定入口的Controller类
     * @param exceptionClass  指定类型的异常类
     * @return 异常处理器
     */
    public static MethodWrapper selectExceptionHandler(Class<?> controllerClass, Class<? extends Throwable> exceptionClass) {
        MethodWrapper exceptionHandler = null;

        // 查找当前Controller的异常处理器
        ExceptionHandlerManager controllerExceptionManager = instance.getControllerManager(controllerClass);
        if (controllerExceptionManager != null) {
            exceptionHandler = controllerExceptionManager.lookupExceptionHandler(exceptionClass);
        }

        // 查找全局异常处理器
        if (exceptionHandler == null) {
            exceptionHandler = selectExceptionHandler(exceptionClass);
        }
        return exceptionHandler;
    }

    /**
     * 选择全局的异常处理器.
     *
     * @param exceptionClass 指定类型的异常类
     * @return 异常处理器
     */
    public static MethodWrapper selectExceptionHandler(Class<? extends Throwable> exceptionClass) {
        return instance.globalExceptionManager.lookupExceptionHandler(exceptionClass);
    }

    /**
     * 获取指定Controller类的异常管理器.
     *
     * @param controllerClass 指定Controller类
     * @return 异常管理器
     */
    private ExceptionHandlerManager getControllerManager(Class<?> controllerClass) {
        return controllerExceptionManager.get(controllerClass);
    }
}
