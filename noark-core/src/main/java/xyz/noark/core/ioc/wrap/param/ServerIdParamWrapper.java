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

import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.ioc.wrap.MethodParamContext;
import xyz.noark.core.ioc.wrap.ParamWrapper;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;
import xyz.noark.core.network.packet.ServerIdPacket;
import xyz.noark.core.util.StringUtils;

/**
 * 注入ServerId.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.6
 */
public class ServerIdParamWrapper implements ParamWrapper {

    @Override
    public Object read(MethodParamContext context) {
        // 如果有封包且带有元数据是玩家ID的话
        if (context.getReqPacket() != null && context.getReqPacket() instanceof ServerIdPacket) {
            return ((ServerIdPacket) context.getReqPacket()).getServerId();
        }
        // 拿本地区服编号配置
        return Integer.parseInt(EnvConfigHolder.getString("server.id"));
    }

    @Override
    public String toString(Session session, NetworkPacket packet) {
        return StringUtils.join("serverId=", read(new MethodParamContext(session, packet)).toString());
    }
}