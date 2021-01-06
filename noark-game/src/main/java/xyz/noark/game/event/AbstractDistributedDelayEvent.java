package xyz.noark.game.event;

import xyz.noark.core.event.DistributedDelayEvent;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 抽象的分布式延迟事件.
 *
 * @author 小流氓[176543888@qq.com]
 */
public abstract class AbstractDistributedDelayEvent implements DistributedDelayEvent {

    private Serializable id;

    private Date endTime;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractDistributedDelayEvent)) {
            return false;
        }
        AbstractDistributedDelayEvent that = (AbstractDistributedDelayEvent) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DistributedDelayEvent [id=" + id + ", endTime=" + endTime + "]";
    }
}