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
package xyz.noark.core.thread.task;

import xyz.noark.core.util.DateUtils;
import xyz.noark.log.Logger;
import xyz.noark.log.LoggerFactory;

/**
 * 抽象的异步任务.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public abstract class AbstractAsyncTask implements Runnable {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractAsyncTask.class);
    /**
     * 任务创建时间
     */
    protected final long createTime;
    /**
     * 开始执行时间
     */
    protected long startExecTime;

    public AbstractAsyncTask() {
        this.createTime = System.nanoTime();
    }

    @Override
    public void run() {
        // 记录开始执行时间
        startExecTime = System.nanoTime();

        // 做某事之前的处理
        this.doSomethingBefore();

        // 开始做某事
        try {
            this.doSomething();
        }
        // 如果发生了异常情况
        catch (Throwable e) {
            this.doSomethingException(e);
        }
        // 做某事之后的处理
        finally {
            this.doSomethingAfter();
        }
    }

    /**
     * 执行业务逻辑之前的扩展接口.
     */
    protected void doSomethingBefore() {
    }

    /**
     * 异步任务做具体的业务逻辑
     */
    protected abstract void doSomething();

    /**
     * 执行业务逻辑中发生了异常情况
     *
     * @param e 异常信息
     */
    protected void doSomethingException(Throwable e) {
    }

    /**
     * 执行之后做一个逻辑.
     */
    protected void doSomethingAfter() {
        // 判定要不要输出记录执行日志
        if (this.isPrintLog()) {
            float delay = DateUtils.formatNanoTime(startExecTime - createTime);
            float exec = DateUtils.formatNanoTime(System.nanoTime() - startExecTime);
            logger.info("handle {},delay={} ms,exec={} ms", logCode(), delay, exec);
        }
    }


    /**
     * 记录日志这个异步任务的编号.
     *
     * @return 异步任务的编号
     */
    protected abstract String logCode();

    /**
     * 是否记录相关执行日志
     *
     * @return 是否记录相关执行日志
     */
    protected abstract boolean isPrintLog();
}
