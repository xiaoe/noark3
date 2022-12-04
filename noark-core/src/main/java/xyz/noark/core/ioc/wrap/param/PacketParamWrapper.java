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
package xyz.noark.core.ioc.wrap.param;

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.wrap.MethodParamContext;
import xyz.noark.core.ioc.wrap.ParamWrapper;
import xyz.noark.core.ioc.wrap.method.LocalPacketMethodWrapper;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.PacketCodecHolder;
import xyz.noark.core.network.Session;
import xyz.noark.core.util.StringUtils;

/**
 * 协议编解码包装类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class PacketParamWrapper implements ParamWrapper {
    private final Class<?> klass;

    public PacketParamWrapper(Class<?> klass, LocalPacketMethodWrapper packetMethod) {
        this.klass = klass;
        // 正常情况下，封包不可能是基本类型，只有注入的角色ID可能是下面这些类型，但没有@PlayerId所以被识别为封包了...
        if (klass == Long.class || klass == long.class || klass == String.class || klass == int.class || klass == Integer.class) {
            throw new ServerBootstrapException("亲，你是不是少了个@PlayerId了... " + packetMethod.getTipsInfo());
        }
    }

    @Override
    public Object read(MethodParamContext context) {
        // 有请求封包直接取值
        if (context.getReqPacket() != null) {
            return PacketCodecHolder.getPacketCodec().decodeProtocol(context.getReqPacket().getByteArray(), klass);
        }
        // 有协议对象基本就是这个直接传进来的
        if (context.getObject() != null) {
            return context.getObject();
        }
        throw new IllegalArgumentException("未知的参数注入方式，请联系小流氓[176543888@qq.com]");
    }

    @Override
    public String toString(Session session, NetworkPacket packet) {
        Object object = read(new MethodParamContext(session, packet));
        if (object == null) {
            return "protocol=null";
        }

        String protocol = object.toString();
        if (StringUtils.isNotEmpty(protocol)) {
            // 把回车换成逗号，最后一个逗号干掉
            protocol = protocol.replace('\n', ',');
            protocol = protocol.substring(0, protocol.length() - 1);
        }
        return StringUtils.join("protocol={", protocol, "}");
    }
}