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
package xyz.noark.core.exception;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.StaticComponent;
import xyz.noark.core.event.EventManager;
import xyz.noark.core.network.NetworkListener;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;

/**
 * 异常小助手.
 *
 * @author 小流氓[176543888@qq.com]
 */
@StaticComponent
public class ExceptionHelper {
    @Autowired(required = false)
    private static NetworkListener networkListener;
    @Autowired(required = false)
    private static EventManager eventManager;

    private ExceptionHelper() {
    }

    /**
     * 监控Socket业务中的未知异常.
     *
     * @param session Socket会话
     * @param packet  封包
     * @param e       异常堆栈
     */
    public static void monitor(Session session, NetworkPacket packet, Throwable e) {
        // 额外处理逻辑
        if (networkListener != null) {
            networkListener.handleException(session, packet, e);
        }
    }

    public static void monitor(Throwable e) {
        // 以事件形式发布这个异常，由具体项目来决定是打印还是上报...
        eventManager.publish(new ExceptionEvent(e));
    }
}
