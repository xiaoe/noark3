package xyz.noark.core.lang;

import java.util.Date;

/**
 * 一个时间段.
 * <p>包含一个开始时间，一个结束时间</p>
 *
 * @author 小流氓[176543888@qq.com]
 */
public class TimeSlot {
    private Date start;
    private Date end;

    public TimeSlot() {
    }

    public TimeSlot(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}