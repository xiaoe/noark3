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
        monitor(e);

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
