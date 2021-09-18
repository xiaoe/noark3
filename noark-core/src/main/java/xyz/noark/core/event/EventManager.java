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
package xyz.noark.core.event;

/**
 * 事件管理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public interface EventManager {
    /**
     * 发布一个事件.
     *
     * @param event 事件源
     */
    void publish(Event event);

    /**
     * 发布一个延迟事件.
     * <p>
     * 一定要重新HashCode和equals
     *
     * @param event 事件源
     */
    void publish(DelayEvent event);

    /**
     * 发布一个定时任务事件.
     *
     * @param event 事件源
     */
    void publish(FixedTimeEvent event);

    /**
     * 从事件管理器中移除最先执行的那个延迟事件（无论它是否过期）.
     * <p>
     * 注：<br>
     * 1.一定要重新HashCode和equals<br>
     * 2.只会删除一个事件，正常事件管理器里也不会有两个一样的事件，不是吗？<br>
     *
     * @param event 事件源
     * @return 如果移除成功则返回true, 否则返回false.
     */
    boolean remove(DelayEvent event);

    /**
     * 从事件管理器中移除指定的延迟事件（无论它是否过期）.
     * <p>
     * 注：<br>
     * 1.一定要重新HashCode和equals<br>
     * 2.移除所有指定的延迟事件，就是一个事件可能多次添加<br>
     * 3.用于重用事件，但没移除就又加进去了，建议有增有减，有序执行，理不清就是给将来维护留坑啊
     *
     * @param event 事件源
     * @return 如果移除成功则返回true, 否则返回false.
     */
    boolean removeAll(DelayEvent event);
}