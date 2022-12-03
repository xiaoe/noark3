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
