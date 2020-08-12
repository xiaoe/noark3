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
import xyz.noark.core.annotation.PlayerId;
import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.ioc.definition.method.PacketMethodDefinition;
import xyz.noark.core.ioc.wrap.ParamWrapper;
import xyz.noark.core.ioc.wrap.param.*;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;

import java.io.Serializable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;

/**
 * 封包处理方法包装类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class PacketMethodWrapper extends AbstractControllerMethodWrapper {
    private final Serializable opcode;
    private final boolean inner;
    private final Set<Session.State> stateSet;
    private final boolean allState;
    /**
     * 串行执行队列ID
     */
    private final String queueId;
    private final ArrayList<ParamWrapper> parameters;
    /**
     * 调用总次数
     */
    private final LongAdder callNum = new LongAdder();

    public PacketMethodWrapper(MethodAccess methodAccess, Object single, PacketMethodDefinition md, ExecThreadGroup threadGroup, Class<?> controllerMasterClass, String queueId) {
        super(methodAccess, single, md.getMethodIndex(), threadGroup, controllerMasterClass.getName(), md.getOrder(), "protocol(opcode=" + md.getOpcode() + ")");
        this.opcode = md.getOpcode();
        this.inner = md.isInnerPacket();
        this.printLog = md.isPrintLog();
        this.stateSet = md.getStateSet();
        this.allState = stateSet.contains(Session.State.ALL);
        this.deprecated = md.isDeprecated();
        this.queueId = queueId;

        this.parameters = new ArrayList<>(md.getParameters().length);
        Arrays.stream(md.getParameters()).forEach(this::buildParamWrapper);
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
        // byte[]
        else if (parameter.getType().equals(byte[].class)) {
            this.parameters.add(new ByteArrayParamWrapper());
        }
        // 封包(特别情况需要这个封包里的参数，留给有需要的人吧...)
        else if (NetworkPacket.class.isAssignableFrom(parameter.getType())) {
            this.parameters.add(new NetworkPacketParamWrapper());
        }
        // 无法识别的只能依靠Session内置解码器来转化了.
        else {
            this.parameters.add(new PacketParamWrapper(parameter.getType()));
        }
    }

    /**
     * 分析参数.
     *
     * @param session Session对象
     * @param packet  封包
     * @return 参数列表
     */
    public Object[] analysisParam(Session session, NetworkPacket packet) {
        List<Object> args = new ArrayList<>(parameters.size());
        for (ParamWrapper parameter : parameters) {
            args.add(parameter.read(session, packet));
        }
        return args.toArray();
    }

    /**
     * 分析参数.
     *
     * @param playerId 玩家ID
     * @param protocol 协议对象
     * @return 参数列表
     */
    public Object[] analysisParam(Serializable playerId, Object protocol) {
        List<Object> args = new ArrayList<>(parameters.size());
        for (ParamWrapper parameter : parameters) {
            args.add(parameter.read(playerId, protocol));
        }
        return args.toArray();
    }

    /**
     * 获取封包编号.
     *
     * @return 封包编号
     */
    public Serializable getOpcode() {
        return opcode;
    }

    /**
     * 是否为内部指令.
     * <p>
     * 如果是内部指令，客户端过来的封包是不可以调用此方法的.
     *
     * @return 是否为内部指令
     */
    public boolean isInner() {
        return inner;
    }

    /**
     * 当前入口的状态是All
     *
     * @return 状态是All
     */
    public boolean isAllState() {
        return allState;
    }

    /**
     * 获取当前方法在什么Session状态才可以被执行.
     *
     * @return 可执行的Session状态集合
     */
    public Set<Session.State> getStateSet() {
        return stateSet;
    }

    /**
     * 调用次数自增
     */
    public void incrCallNum() {
        callNum.increment();
    }

    /**
     * 获取当前被调用的次数.
     *
     * @return 调用的次数
     */
    public long getCallNum() {
        return callNum.longValue();
    }

    /**
     * 获取串型队列ID
     *
     * @return 串型队列ID
     */
    @Override
    public String getQueueId() {
        return queueId;
    }
}