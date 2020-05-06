/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.ioc.definition.method;

import xyz.noark.core.annotation.controller.CommandMapping;
import xyz.noark.core.annotation.controller.PacketMapping;
import xyz.noark.core.network.Session;
import xyz.noark.reflectasm.MethodAccess;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 封包处理入口的定义.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class PacketMethodDefinition extends SimpleMethodDefinition {
    private final Parameter[] parameters;
    private final Serializable opcode;
    private final boolean printLog;
    private final boolean inner;
    private final Set<Session.State> stateSet;

    public PacketMethodDefinition(MethodAccess methodAccess, Method method, PacketMapping packetMapping) {
        super(methodAccess, method);
        this.parameters = method.getParameters();
        this.opcode = packetMapping.opcode();
        this.printLog = packetMapping.printLog();
        this.inner = packetMapping.inner();
        this.stateSet = new HashSet<>(Arrays.asList(packetMapping.state()));
    }

    public PacketMethodDefinition(MethodAccess methodAccess, Method method, CommandMapping commandMapping) {
        super(methodAccess, method);
        this.parameters = method.getParameters();
        this.opcode = commandMapping.opcode();
        this.printLog = commandMapping.printLog();
        this.inner = commandMapping.inner();
        this.stateSet = new HashSet<>(Arrays.asList(commandMapping.state()));
    }

    public Serializable getOpcode() {
        return opcode;
    }

    public boolean isPrintLog() {
        return printLog;
    }

    public boolean isInnerPacket() {
        return inner;
    }

    public Set<Session.State> getStateSet() {
        return stateSet;
    }

    @Override
    public Parameter[] getParameters() {
        return parameters;
    }
}