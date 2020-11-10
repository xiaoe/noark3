package xyz.noark.core.thread;

import java.io.Serializable;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
class TaskContext {
    private final Serializable queueId;
    private final Serializable playerId;

    TaskContext(Serializable queueId, Serializable playerId) {
        this.queueId = queueId;
        this.playerId = playerId;
    }

    public Serializable getQueueId() {
        return queueId;
    }

    public Serializable getPlayerId() {
        return playerId;
    }
}
