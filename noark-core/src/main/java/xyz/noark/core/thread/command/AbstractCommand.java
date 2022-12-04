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
package xyz.noark.core.thread.command;

import xyz.noark.core.ioc.wrap.method.ExceptionMethodWrapper;
import xyz.noark.core.thread.ThreadCommand;

/**
 * 抽象的线程处理指令.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public abstract class AbstractCommand implements ThreadCommand {

    @Override
    public boolean catchExecException(Throwable e) {
        // 尝试查找到此异常的最优处理器
        ExceptionMethodWrapper exceptionHandler = this.lookupExceptionHandler(e);

        // 没有就是没处理方案，返回false
        if (exceptionHandler == null) {
            return false;
        }

        // 在反射调用异常处理器之前
        this.invokeExceptionHandlerBefore(e);

        // 分析并解析出调用此方法的参数列表
        Object[] args = this.analysisExceptionHandlerParam(exceptionHandler, e);

        // 调用异常处理器
        Object result = exceptionHandler.invoke(args);

        // 在反射调用异常处理器之后
        this.invokeExceptionHandlerAfter(result);

        // 正常结束了，给调用一个状态
        return true;
    }

    /**
     * 在反射调用异常处理器之后
     *
     * @param result 执行异常处理器的返回结果
     */
    protected void invokeExceptionHandlerAfter(Object result) {
        // 留给子类去扩展实现
    }

    /**
     * 在反射调用异常处理器之前
     *
     * @param e 异常对象
     */
    protected void invokeExceptionHandlerBefore(Throwable e) {
        // 留给子类去扩展实现
    }

    /**
     * 分析并解析出调用此方法的参数列表
     *
     * @param exceptionHandler 异常处理器
     * @param e                当前正在处理的异常对象
     * @return 参数列表
     */
    protected Object[] analysisExceptionHandlerParam(ExceptionMethodWrapper exceptionHandler, Throwable e) {
        return exceptionHandler.analysisParam(e);
    }

    /**
     * 查找指定类型的异常处理器.
     * <p>查找一个最优解
     *
     * @param e 指定类型的异常类
     * @return 异常处理器
     */
    protected abstract ExceptionMethodWrapper lookupExceptionHandler(Throwable e);
}