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
import xyz.noark.core.lang.TimeoutHashMap;
import xyz.noark.core.thread.AsyncHelper;
import xyz.noark.core.thread.TraceIdFactory;
import xyz.noark.core.thread.task.TaskCallback;

import java.util.concurrent.TimeUnit;

/**
 * 异步任务处理指令.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class AsyncTaskCommand extends AbstractCommand {
    /**
     * 缓存lambda表达式的异步对应的入口，Key=Lambda的实现类名，Value=对应的父方法与行号<br>
     * 注：这里的缓存保留1天，如果1天还没有人调用，那就清了吧...
     */
    private static final TimeoutHashMap<String, String> cacheLogCodeMap = new TimeoutHashMap<>(24, TimeUnit.HOURS, AsyncTaskCommand::createLogCode);

    /**
     * lambda表达式包裹的回调方法
     */
    private final TaskCallback callback;
    /**
     * 用于记录日志的Log编码
     */
    private final String logCode;

    public AsyncTaskCommand(TaskCallback callback) {
        // 异步任务都是由其他业务执行产生的
        super(TraceIdFactory.getMdcTraceId());
        this.callback = callback;
        // LogCode增加一层缓存
        this.logCode = cacheLogCodeMap.get(callback.getClass().getName());
    }

    private static String createLogCode(String callbackClassName) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        // 从第13步开始向下找，跳过AsyncHelper类的后一个就是我们想要的
        for (int i = 13; i < elements.length; i++) {
            StackTraceElement stackTraceElement = elements[i];
            String className = stackTraceElement.getClassName();
            if (AsyncHelper.class.getName().equals(className)) {
                continue;
            }

            // 对className进行修正，去掉包名
            int index = className.lastIndexOf('.');
            if (index >= 0) {
                className = className.substring(index + 1);
            }

            String methodName = stackTraceElement.getMethodName();
            int lineNumber = stackTraceElement.getLineNumber();
            return "lambda[" + className + "#" + methodName + ":" + lineNumber + "]";
        }
        // 未命中也要给个相对合理的输出
        return callbackClassName;
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
        return logCode;
    }

    @Override
    public boolean isPrintLog() {
        return true;
    }
}
