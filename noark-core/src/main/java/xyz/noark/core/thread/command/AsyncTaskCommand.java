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

import xyz.noark.core.ioc.wrap.exception.ExceptionHandlerSelector;
import xyz.noark.core.ioc.wrap.method.ExceptionMethodWrapper;
import xyz.noark.core.thread.TraceIdFactory;
import xyz.noark.core.thread.task.TaskCallback;

/**
 * 异步任务处理指令.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class AsyncTaskCommand extends AbstractCommand {
    private final TaskCallback callback;

    public AsyncTaskCommand(TaskCallback callback) {
        // 异步任务都是由其他业务执行产生的
        super(TraceIdFactory.getMdcTraceId());
        this.callback = callback;
    }

    @Override
    public void exec() {
        callback.doSomething();
    }

    @Override
    public ExceptionMethodWrapper lookupExceptionHandler(Throwable e) {
        // 这个是没有Controller入口的，所以直接走全局
        return ExceptionHandlerSelector.selectExceptionHandler(e.getClass());
    }

    @Override
    public String code() {
        return "async";
    }

    @Override
    public boolean isPrintLog() {
        return true;
    }
}