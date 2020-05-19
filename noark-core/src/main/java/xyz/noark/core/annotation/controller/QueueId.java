package xyz.noark.core.annotation.controller;

import java.lang.annotation.*;

/**
 * 当前注解用于申明串行执行的队列ID
 * <p>
 * 如果没有申明，那将会在线程池里随机执行
 * </p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface QueueId {
    /**
     * 串行执行的队列ID
     *
     * @return 队列ID
     */
    String value();
}