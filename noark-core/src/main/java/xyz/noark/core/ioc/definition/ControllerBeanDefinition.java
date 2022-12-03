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
package xyz.noark.core.ioc.definition;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.ExceptionHandler;
import xyz.noark.core.annotation.ModuleController;
import xyz.noark.core.annotation.controller.*;
import xyz.noark.core.ioc.NoarkIoc;
import xyz.noark.core.ioc.definition.method.*;
import xyz.noark.core.ioc.manager.EventMethodManager;
import xyz.noark.core.ioc.manager.HttpMethodManager;
import xyz.noark.core.ioc.manager.PacketMethodManager;
import xyz.noark.core.ioc.manager.ScheduledMethodManager;
import xyz.noark.core.ioc.wrap.exception.ExceptionHandlerSelector;
import xyz.noark.core.ioc.wrap.method.*;
import xyz.noark.core.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 控制器的Bean定义描述类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ControllerBeanDefinition extends DefaultBeanDefinition {
    /**
     * 执行线程组
     */
    private final ExecThreadGroup threadGroup;
    /**
     * 控制器隶属哪个主控制器
     */
    private final Class<?> controllerMasterClass;
    /**
     * 串行执行队列ID
     */
    private final String queueId;

    private final ArrayList<PacketMethodDefinition> pmds = new ArrayList<>();
    private final ArrayList<EventMethodDefinition> emds = new ArrayList<>();
    private final ArrayList<HttpMethodDefinition> hmds = new ArrayList<>();
    private final ArrayList<ScheduledMethodDefinition> smds = new ArrayList<>();
    private final ArrayList<ExceptionMethodDefinition> exceptionMethodList = new ArrayList<>();

    public ControllerBeanDefinition(String profileStr, Class<?> klass, Controller controller) {
        this(profileStr, klass, controller.threadGroup(), controller.value(), klass);
    }

    public ControllerBeanDefinition(String profileStr, Class<?> klass, ModuleController controller) {
        this(profileStr, klass, ExecThreadGroup.ModuleThreadGroup, StringUtils.EMPTY, controller.master());
    }

    private ControllerBeanDefinition(String profileStr, Class<?> klass, ExecThreadGroup threadGroup, String queueId, Class<?> controllerMasterClass) {
        super(profileStr, klass);
        this.queueId = queueId;
        this.threadGroup = threadGroup;
        this.controllerMasterClass = controllerMasterClass;
    }

    @Override
    protected void analysisMethodByAnnotation(Class<? extends Annotation> annotationType, Annotation annotation, Method method) {
        // 客户端过来的协议入口.
        if (annotationType == PacketMapping.class) {
            pmds.add(new PacketMethodDefinition(methodAccess, method, (PacketMapping) annotation));
        }
        // 客户端过来的协议入口.
        else if (annotationType == CommandMapping.class) {
            pmds.add(new PacketMethodDefinition(methodAccess, method, (CommandMapping) annotation));
        }
        // 事件监听
        else if (annotationType == EventListener.class) {
            emds.add(new EventMethodDefinition(methodAccess, method, (EventListener) annotation, this));
        }
        // HTTP服务
        else if (annotationType == RequestMapping.class) {
            hmds.add(new HttpMethodDefinition(methodAccess, method, (RequestMapping) annotation));
        } else if (annotationType == GetMapping.class) {
            hmds.add(new HttpMethodDefinition(methodAccess, method, (GetMapping) annotation));
        } else if (annotationType == PostMapping.class) {
            hmds.add(new HttpMethodDefinition(methodAccess, method, (PostMapping) annotation));
        }
        // 延迟任务
        else if (annotationType == Scheduled.class) {
            smds.add(new ScheduledMethodDefinition(methodAccess, method, (Scheduled) annotation));
        }
        // 异常处理器
        else if (annotationType == ExceptionHandler.class) {
            exceptionMethodList.add(new ExceptionMethodDefinition(methodAccess, method, (ExceptionHandler) annotation));
        }
        // 其他的交给父类去处理
        else {
            super.analysisMethodByAnnotation(annotationType, annotation, method);
        }
    }

    @Override
    public void doAnalysisFunction(NoarkIoc noarkIoc) {
        super.doAnalysisFunction(noarkIoc);

        this.doAnalysisPacketHandler();

        this.doAnalysisEventHandler();

        this.doAnalysisHttpHandler();

        this.doAnalysisScheduledHandler();

        this.doAnalysisExceptionHandler();
    }

    /**
     * 分析异常处理入口.
     */
    private void doAnalysisExceptionHandler() {
        final Class<?> controllerClass = single.getClass();
        for (ExceptionMethodDefinition emd : exceptionMethodList) {
            ExceptionHandlerSelector.registerExceptionHandler(controllerClass, new ExceptionMethodWrapper(single, emd));
        }
    }

    /**
     * 分析延迟任务处理入口.
     */
    private void doAnalysisScheduledHandler() {
        final ScheduledMethodManager manager = ScheduledMethodManager.getInstance();
        smds.forEach(smd -> manager.resetScheduledHandler(new ScheduledMethodWrapper(single, smd, threadGroup, controllerMasterClass)));
    }

    /**
     * 分析HTTP处理入口.
     */
    private void doAnalysisHttpHandler() {
        hmds.forEach(hmd -> HttpMethodManager.registerHandler(new HttpMethodWrapper(single, hmd, threadGroup, controllerMasterClass)));
    }

    /**
     * 分析事件处理入口.
     */
    private void doAnalysisEventHandler() {
        final EventMethodManager manager = EventMethodManager.getInstance();
        emds.forEach(emd -> manager.resetEventHandler(new EventMethodWrapper(single, emd, threadGroup, controllerMasterClass)));
    }

    /**
     * 分析一下封包处理方法.
     */
    private void doAnalysisPacketHandler() {
        final PacketMethodManager manager = PacketMethodManager.getInstance();
        pmds.forEach(pmd -> manager.resetPacketHandler(new LocalPacketMethodWrapper(single, pmd, threadGroup, controllerMasterClass, queueId)));
    }
}