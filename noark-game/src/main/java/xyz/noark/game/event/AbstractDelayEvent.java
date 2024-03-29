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
package xyz.noark.game.event;

import xyz.noark.core.annotation.orm.Column;
import xyz.noark.core.annotation.orm.Id;
import xyz.noark.core.event.DelayEvent;
import xyz.noark.core.thread.TraceIdFactory;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的延迟事件.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class AbstractDelayEvent implements DelayEvent {

    @Id
    @Column(name = "id", nullable = false, comment = "事件ID")
    private long id;

    @Column(name = "trace_id", nullable = false, comment = "链路追踪ID", defaultValue = "0")
    private String traceId;

    @Column(name = "end_time", nullable = false, comment = "结束时间", defaultValue = "2018-01-01 00:00:00")
    private Date endTime;

    public AbstractDelayEvent() {
        this.setTraceId(TraceIdFactory.getMdcTraceId());
    }

    @Override
    public int compareTo(Delayed o) {
        return endTime.compareTo(((DelayEvent) o).getEndTime());
    }

    @Override
    public long getDelay(TimeUnit unit) {
        // 需要延迟的毫秒数
        long delay = endTime.getTime() - System.currentTimeMillis();
        // 转化为目标指定时间单位
        return unit.convert(DefaultEventManager.calculateMaxDelay(delay), TimeUnit.MILLISECONDS);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {

        // 优化判定前置条件，如果endTime为空则肯定不会在队列树上.
        if (this.endTime != null) {
            DefaultEventManager.assertDebugAndNotInQueue(this);
        }

        this.endTime = endTime;
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    /**
     * 此Set方法主要用于存储读写，不理解需求的情况下不建议主动给值
     *
     * @param traceId 链路追踪ID
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractDelayEvent other = (AbstractDelayEvent) obj;
        return id == other.id;
    }
}