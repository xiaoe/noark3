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

import xyz.noark.core.ioc.wrap.method.AbstractControllerMethodWrapper;
import xyz.noark.core.ioc.wrap.method.ExceptionMethodWrapper;

/**
 * 默认实现的一种无特需求的通用指令.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class DefaultCommand extends AbstractCommand {

    private final AbstractControllerMethodWrapper method;
    private final Object[] args;

    public DefaultCommand(AbstractControllerMethodWrapper method, Object... args) {
        this.method = method;
        this.args = args;
    }

    @Override
    public final void exec() {
        // 执行业务逻辑
        Object result = method.invoke(args);

        // 有返回值，交给子类去扩展
        if (result != null) {
            this.handleExecResult(result);
        }
    }

    /**
     * 处理执行结果
     *
     * @param result 执行结果
     */
    protected void handleExecResult(Object result) {
        // 留给有需要的子类扩展
    }

    @Override
    protected ExceptionMethodWrapper lookupExceptionHandler(Throwable e) {
        // 先查询Controller上的异常处理器，再查询全局处理器
        return method.lookupExceptionHandler(e.getClass());
    }

    @Override
    public String code() {
        return method.logCode();
    }

    @Override
    public boolean isPrintLog() {
        return method.isPrintLog();
    }
}
