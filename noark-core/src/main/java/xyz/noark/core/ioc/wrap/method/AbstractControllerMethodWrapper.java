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
package xyz.noark.core.ioc.wrap.method;

import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.ioc.MethodDefinition;

/**
 * Controller类中的可执行方法.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public abstract class AbstractControllerMethodWrapper extends BaseMethodWrapper {
    /**
     * 串行执行的队列ID对应的Key名称
     */
    protected String queueIdKey;
    /**
     * 当前方法是否已废弃使用.
     */
    protected boolean deprecated = false;

    //----------------------------------------
    /**
     * 执行线程组
     */
    protected final ExecThreadGroup threadGroup;
    /**
     * 如果是模块串行，模块唯一标识
     */
    protected final String module;

    /**
     * 可执行方法的两个属性
     */
    private final String logCode;
    protected boolean printLog = false;

    public AbstractControllerMethodWrapper(Object single, ExecThreadGroup threadGroup, String module, String logCode, MethodDefinition md) {
        super(single, md.getMethodAccess(), md.getMethodIndex(), md.getOrder());
        this.module = module;
        this.logCode = logCode;
        this.threadGroup = threadGroup;
    }

    /**
     * 获取当前的串行执行的队列ID对应的Key名称
     *
     * @return 队列ID对应的Key名称
     */
    public String getQueueIdKey() {
        return queueIdKey;
    }

    /**
     * 判定当前封包处理方法是否被废弃使用.
     *
     * @return 如果被废弃返回true，否则返回false
     */
    public boolean isDeprecated() {
        return deprecated;
    }

    /**
     * 设置当前封包处理方法是否被废弃使用.
     *
     * @param deprecated 是否被废弃
     */
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    //----------------------------------------

    public boolean isPrintLog() {
        return printLog;
    }

    public void setPrintLog(boolean printLog) {
        this.printLog = printLog;
    }

    public ExecThreadGroup threadGroup() {
        return threadGroup;
    }

    public String getModule() {
        return module;
    }

    /**
     * 记录日志的Code信息
     *
     * @return Code信息
     */
    public String logCode() {
        return logCode;
    }


}