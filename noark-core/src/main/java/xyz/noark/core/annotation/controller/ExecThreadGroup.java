/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.annotation.controller;

/**
 * 执行线程组枚举类.
 * <p>
 * 线程调度规则：N个线程 处理M个队列，哪个线程空闲了就去处理有任务的队列
 * </p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public enum ExecThreadGroup {
    /**
     * Netty本身的Work线程.
     * <p>
     * 心跳，或完全没有IO操作的逻辑，直接交给Netty的Work线程处理掉
     */
    NettyThreadGroup,

    /**
     * 以玩家ID划分的线程.
     * <p>
     * 可以理解为一个玩家一个线程<br>
     */
    PlayerThreadGroup,

    /**
     * 以模块划分的线程.
     * <p>
     * 可以理解为一个Controller一个线程<br>
     * 如：登录，世界聊天，公会，排行榜
     */
    ModuleThreadGroup,

    /**
     * 一种串行执行队列处理线程.
     */
    QueueThreadGroup;
}