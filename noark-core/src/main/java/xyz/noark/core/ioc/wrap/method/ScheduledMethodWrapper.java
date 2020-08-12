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

import com.esotericsoftware.reflectasm.MethodAccess;
import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.cron.DelayTrigger;
import xyz.noark.core.cron.DelayTriggerFactory;
import xyz.noark.core.ioc.definition.method.ScheduledMethodDefinition;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 延迟任务处理方法的包装类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2.6
 */
public class ScheduledMethodWrapper extends AbstractControllerMethodWrapper {
    /**
     * 自动增的ID编号，用于唯一调度
     */
    private static final AtomicLong AUTO_ID = new AtomicLong(0);
    private final Long id;
    private final DelayTrigger trigger;

    public ScheduledMethodWrapper(MethodAccess methodAccess, Object single, ScheduledMethodDefinition smd, ExecThreadGroup threadGroup, Class<?> controllerMasterClass) {
        super(methodAccess, single, smd.getMethodIndex(), threadGroup, controllerMasterClass.getName(), smd.getOrder(), "scheduled(" + smd.getMethodName() + ")");
        // 生成一个唯一ID编号.
        this.id = AUTO_ID.incrementAndGet();
        this.trigger = DelayTriggerFactory.create(smd.getScheduled());
    }

    public Long getId() {
        return id;
    }

    public Date nextExecutionTime() {
        return trigger.nextExecutionTime();
    }
}