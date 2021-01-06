package xyz.noark.core.event;

import java.io.Serializable;
import java.util.Date;

/**
 * 分布式延迟事件.
 *
 * @author 小流氓[176543888@qq.com]
 */
public interface DistributedDelayEvent extends Event, QueueEvent {
    /**
     * 事件的唯一ID
     *
     * @return 唯一ID
     */
    Serializable getId();

    /**
     * 事件的触发时间
     *
     * @return 触发时间
     */
    Date getEndTime();

    /**
     * 分布式延迟事件执行队列就使用这个ID
     * @return 唯一ID
     */
    @Override
    default Serializable getQueueId() {
        return getId();
    }
}