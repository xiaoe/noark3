package xyz.noark.core.exception;

import xyz.noark.core.event.Event;

/**
 * 异常事件.
 * <p>用于框架收集异常，然后走事件系统由项目具体考虑是否要额外处理</p>
 *
 * @author 小流氓[176543888@qq.com]
 */
public class ExceptionEvent implements Event {
    private final Throwable exception;

    public ExceptionEvent(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }
}
