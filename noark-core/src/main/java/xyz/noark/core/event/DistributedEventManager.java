package xyz.noark.core.event;

/**
 * 分布式事件管理器.
 *
 * @author 小流氓[176543888@qq.com]
 */
public interface DistributedEventManager {

    /**
     * 发布一个事件.
     *
     * @param event 事件
     * @return 发布成功返回true, 特殊情况下会有对结果的要求
     */
    boolean publish(DistributedDelayEvent event);

    /**
     * 移除一个事件.
     *
     * @param event 事件
     * @return 移除成功返回true
     */
    boolean remove(DistributedDelayEvent event);

    /**
     * 停服时，清理相关数据
     */
    void shutdown();
}
