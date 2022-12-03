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

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.StaticComponent;

/**
 * 事件辅助类.
 * <p>事件小助手，这个静态组件就是方便那些使用IOC取事件管理器的逻辑</p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@StaticComponent
public class EventHelper {

    @Autowired(required = false)
    private static EventManager eventManager;

    /**
     * 私有化构造方法
     */
    private EventHelper() {
    }

    /**
     * 事件小助手，直接提供一个静态发布的方法.
     *
     * @param event 事件
     */
    public static void publish(Event event) {
        eventManager.publish(event);
    }

    /**
     * 事件小助手，直接提供一个静态发布的方法.
     *
     * @param event 延迟事件
     */
    public static void publish(DelayEvent event) {
        eventManager.publish(event);
    }

    /**
     * 事件小助手，移除一个延迟事件.
     *
     * @param event 延迟事件
     * @return 移除成功返回true
     */
    public static boolean remove(DelayEvent event) {
        return eventManager.remove(event);
    }

    /**
     * 事件小助手，移除一个延迟事件的所有副本.
     *
     * @param event 延迟事件
     * @return 移除成功返回true
     */
    public static boolean removeAll(DelayEvent event) {
        return eventManager.removeAll(event);
    }
}