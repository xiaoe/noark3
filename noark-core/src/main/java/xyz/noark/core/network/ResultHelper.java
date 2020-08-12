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
package xyz.noark.core.network;

/**
 * 执行结果辅助类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2.3
 */
public class ResultHelper {

    /**
     * 尝试发送入口的返回值.
     *
     * @param session Session对象
     * @param packet  请求封包对象
     * @param result  协议入口返回值
     */
    public static void trySendResult(Session session, NetworkPacket packet, Object result) {
        // 玩家已下线也可以忽略发送...
        if (session == null) {
            return;
        }

        // 没有返回值，那就结束了...
        if (result == null || result instanceof Void) {
            return;
        }

        // 如果是网络协议，那就直接转发，不是就包裹他,由封包编码器2次处理.
        if (!(result instanceof NetworkProtocol)) {
            // 接受的编号与发送的编号一致才能使用这个功能
            result = new NetworkProtocol(packet.getOpcode(), result);
        }
        NetworkProtocol protocol = (NetworkProtocol) result;
        protocol.setPacket(packet);
        session.send(protocol);
    }
}