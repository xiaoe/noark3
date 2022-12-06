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
package xyz.noark.core.thread;

import xyz.noark.log.MDC;

import java.util.UUID;

/**
 * TraceId工厂类
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class TraceIdFactory {

    /**
     * 链路追踪ID = traceId
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 初始化一个固定的启动服务器TraceId标识
     */
    public static void initFixedTraceIdByStartServer() {
        MDC.put(TRACE_ID, "start-server");
    }

    /**
     * 初始化一个固定的停止服务器TraceId标识
     */
    public static void initFixedTraceIdByStopServer() {
        MDC.put(TRACE_ID, "stop-server");
    }

    /**
     * 初始化一个固定的调度线程TraceId标识
     */
    public static void initFixedTraceIdBySchedulingThread() {
        MDC.put(TRACE_ID, "scheduling-thread");
    }


    /**
     * 从MDC中获取链路追踪ID之traceId
     *
     * @return 链路追踪ID
     */
    public static String getMdcTraceId() {
        return String.valueOf(MDC.get(TRACE_ID));
    }

    /**
     * 随机生成一个链路追踪ID
     *
     * @return 链路追踪ID
     */
    public static String randomTraceId() {
        return UUID.randomUUID().toString();
    }
}