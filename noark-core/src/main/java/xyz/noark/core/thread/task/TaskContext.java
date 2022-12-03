package xyz.noark.core.thread.task;

import java.io.Serializable;

/**
 * 任务上下文
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class TaskContext {
    private final Serializable queueId;

    TaskContext(Serializable queueId) {
        this.queueId = queueId;
    }

    public Serializable getQueueId() {
        return queueId;
    }
}
