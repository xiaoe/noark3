package xyz.noark.core.event;

import xyz.noark.core.annotation.Autowired;

/**
 * 事件辅助类.
 * <p>事件小助手，这个静态组件就是方便那些使用IOC取事件管理器的逻辑</p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class EventHelper {

    @Autowired(required = false)
    private static EventManager eventManager;

    /**
     * 私有化构造方法
     */
    private EventHelper() {
    }

    /**
     * 事件小助手，直接提供一个静态发布的方法.
     *
     * @param event 事件
     */
    public static void publish(Event event) {
        eventManager.publish(event);
    }

    /**
     * 事件小助手，直接提供一个静态发布的方法.
     *
     * @param event 延迟事件
     */
    public static void publish(DelayEvent event) {
        eventManager.publish(event);
    }

    /**
     * 事件小助手，移除一个延迟事件.
     *
     * @param event 延迟事件
     * @return 移除成功返回true
     */
    public static boolean remove(DelayEvent event) {
        return eventManager.remove(event);
    }

    /**
     * 事件小助手，移除一个延迟事件的所有副本.
     *
     * @param event 延迟事件
     * @return 移除成功返回true
     */
    public static boolean removeAll(DelayEvent event) {
        return eventManager.removeAll(event);
    }
}