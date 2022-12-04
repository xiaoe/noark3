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

import xyz.noark.core.annotation.PlayerId;
import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.event.Event;
import xyz.noark.core.ioc.definition.method.EventMethodDefinition;
import xyz.noark.core.ioc.wrap.MethodParamContext;
import xyz.noark.core.ioc.wrap.ParamWrapper;
import xyz.noark.core.ioc.wrap.param.ObjectParamWrapper;
import xyz.noark.core.ioc.wrap.param.PlayerIdParamWrapper;

import java.io.Serializable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 事件处理方法的包装类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class EventMethodWrapper extends AbstractControllerMethodWrapper implements Comparable<EventMethodWrapper> {
    private final Class<? extends Event> eventClass;
    private final ArrayList<ParamWrapper> parameters;
    private final boolean async;

    public EventMethodWrapper(Object single, EventMethodDefinition emd, ExecThreadGroup threadGroup, Class<?> controllerMasterClass) {
        super(single, threadGroup, controllerMasterClass.getName(), buildLogCode(single, emd), emd);
        this.eventClass = emd.getEventClass();
        this.printLog = emd.isPrintLog();
        this.async = emd.isAsync();

        this.parameters = new ArrayList<>(emd.getParameters().length);
        Arrays.stream(emd.getParameters()).forEach(this::buildParamWrapper);
    }

    /**
     * 构建这个事件日志的编码
     *
     * @param single 处理类入口
     * @param emd    事件处理方法的定义
     * @return 返回日志编码
     */
    private static String buildLogCode(Object single, EventMethodDefinition emd) {
        StringBuilder sb = new StringBuilder(128);
        // 事件
        sb.append("event[");
        // 类名
        sb.append(single.getClass().getSimpleName()).append('#');
        // 方法名称
        sb.append(emd.getMethod().getName()).append('(');
        // 参数名称
        sb.append(emd.getEventClass().getSimpleName()).append(')');
        // 最终结果
        return sb.append(']').toString();
    }

    /**
     * 构建参数
     */
    private void buildParamWrapper(Parameter parameter) {
        // 玩家ID
        if (parameter.isAnnotationPresent(PlayerId.class)) {
            this.parameters.add(new PlayerIdParamWrapper());
        }
        // 无法识别的就当他是一个对象
        else {
            this.parameters.add(new ObjectParamWrapper());
        }
    }

    public Object[] analysisParam(Serializable playerId, Event event) {
        if (parameters.isEmpty()) {
            return new Object[0];
        }

        MethodParamContext context = new MethodParamContext(playerId, event);
        List<Object> args = new ArrayList<>(parameters.size());
        for (ParamWrapper parameter : parameters) {
            args.add(parameter.read(context));
        }
        return args.toArray();
    }

    public Class<? extends Event> getEventClass() {
        return eventClass;
    }

    public boolean isAsync() {
        return async;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(EventMethodWrapper o) {
        // 相同方式，采用Order排序
        if (async == o.isAsync()) {
            return Integer.compare(this.getOrder(), o.getOrder());
        }

        // 异步情况向后排
        return async ? 1 : -1;
    }

    @Override
    public String toString() {
        return "EventMethodWrapper [async=" + async + ", method=" + methodAccess.getMethodNames()[methodIndex] + "]";
    }
}