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
import xyz.noark.core.annotation.ServerId;
import xyz.noark.core.ioc.definition.method.ExceptionMethodDefinition;
import xyz.noark.core.ioc.wrap.MethodParamContext;
import xyz.noark.core.ioc.wrap.ParamWrapper;
import xyz.noark.core.ioc.wrap.param.*;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 异常处理方法的包装类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class ExceptionMethodWrapper extends BaseMethodWrapper {
    private final List<Class<? extends Throwable>> exceptionClassList;
    private final ArrayList<ParamWrapper> parameters;

    public ExceptionMethodWrapper(Object single, ExceptionMethodDefinition emd) {
        super(single, emd.getMethodAccess(), emd.getMethodIndex(), emd.getOrder());
        this.exceptionClassList = emd.getExceptionClassList();

        this.parameters = new ArrayList<>(emd.getParameters().length);
        Arrays.stream(emd.getParameters()).forEach(this::buildParamWrapper);
    }

    public List<Class<? extends Throwable>> getExceptionClassList() {
        return exceptionClassList;
    }

    /**
     * 构建参数
     */
    private void buildParamWrapper(Parameter parameter) {
        // Session
        if (Session.class.isAssignableFrom(parameter.getType())) {
            this.parameters.add(new SessionParamWrapper());
        }
        // 玩家ID
        else if (parameter.isAnnotationPresent(PlayerId.class)) {
            this.parameters.add(new PlayerIdParamWrapper());
        }
        // 区服ID
        else if (parameter.isAnnotationPresent(ServerId.class)) {
            this.parameters.add(new ServerIdParamWrapper());
        }
        // 封包(特别情况需要这个封包里的参数，留给有需要的人吧...)
        else if (NetworkPacket.class.isAssignableFrom(parameter.getType())) {
            this.parameters.add(new NetworkPacketParamWrapper());
        }
        // 无法识别的就当他是一个对象
        else {
            this.parameters.add(new ObjectParamWrapper());
        }
    }

    /**
     * 分析参数.
     *
     * @param session Session对象
     * @param packet  请求封包
     * @param e       异常对象
     * @return 参数列表
     */
    public Object[] analysisParam(Session session, NetworkPacket packet, Throwable e) {
        MethodParamContext context = new MethodParamContext(session, packet);
        context.setObject(e);
        return this.analysisParam(context);
    }

    /**
     * 分析参数.
     *
     * @param e 异常对象
     * @return 参数列表
     */
    public Object[] analysisParam(Throwable e) {
        return this.analysisParam(new MethodParamContext(null, e));
    }

    private Object[] analysisParam(MethodParamContext context) {
        if (parameters.isEmpty()) {
            return new Object[0];
        }

        List<Object> args = new ArrayList<>(parameters.size());
        for (ParamWrapper parameter : parameters) {
            args.add(parameter.read(context));
        }
        return args.toArray();
    }
}