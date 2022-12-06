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

import com.github.benmanes.caffeine.cache.CacheLoader;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.event.Event;
import xyz.noark.core.event.FixedTimeEvent;
import xyz.noark.core.event.PlayerEvent;
import xyz.noark.core.event.QueueEvent;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.manager.PacketMethodManager;
import xyz.noark.core.ioc.wrap.MethodParamContext;
import xyz.noark.core.ioc.wrap.method.EventMethodWrapper;
import xyz.noark.core.ioc.wrap.method.LocalPacketMethodWrapper;
import xyz.noark.core.ioc.wrap.method.ScheduledMethodWrapper;
import xyz.noark.core.lang.TimeoutHashMap;
import xyz.noark.core.network.*;
import xyz.noark.core.network.packet.QueueIdPacket;
import xyz.noark.core.thread.command.AsyncTaskCommand;
import xyz.noark.core.thread.command.ClientCommand;
import xyz.noark.core.thread.command.DefaultCommand;
import xyz.noark.core.thread.command.InnerCommand;
import xyz.noark.core.thread.task.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.*;

import static xyz.noark.log.LogHelper.logger;

/**
 * 线程调度器.
 * <p>
 * 根据opcode找到目标模块的负载均衡器，进行转发或传递给执行器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ThreadDispatcher {
    private static final int SHUTDOWN_MAX_TIME = 10;
    /**
     * 处理业务逻辑的线程池...
     */
    private ExecutorService businessThreadPool;
    /**
     * 处理业务逻辑的任务队列
     */
    private TimeoutHashMap<Serializable, TaskQueue> threadPoolTaskQueue;

    @Autowired(required = false)
    private MonitorThreadPool monitorThreadPool;

    public ThreadDispatcher() {
    }

    /**
     * 初始线程调度器的配置.
     *
     * @param poolSize         处理业务逻辑的线程数量
     * @param threadNamePrefix 线程名称前缀
     * @param timeout          队列超时销毁时间，单位：分钟
     * @param execTimeout      任务执行超时，单位：秒
     * @param outputStack      任务执行超时输出线程执行堆栈信息，默认开启
     */
    public void init(int poolSize, String threadNamePrefix, int timeout, int execTimeout, boolean outputStack) {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadFactory threadFactory = new NamedThreadFactory(threadNamePrefix);
        this.businessThreadPool = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, workQueue, threadFactory);
        this.threadPoolTaskQueue = new TimeoutHashMap<>(timeout, TimeUnit.MINUTES, buildLoader(execTimeout, outputStack));
    }

    private CacheLoader<Serializable, TaskQueue> buildLoader(int execTimeout, boolean outputStack) {
        // 启动监控的任务队列
        if (execTimeout > 0) {
            return (id) -> new MonitorTaskQueue(id, monitorThreadPool, businessThreadPool, execTimeout, outputStack);
        }
        // 常规的任务队列
        else {
            return (id) -> new TaskQueue(id, businessThreadPool);
        }
    }

    /**
     * 派发游戏封包.
     *
     * @param session Session对象
     * @param packet  网络封包
     * @param pmw     本地封包处理方法
     */
    public void dispatchClientPacket(Session session, NetworkPacket packet, LocalPacketMethodWrapper pmw) {
        // 客户端发来的封包，是不可以调用内部处理器的.
        if (pmw.isInner()) {
            logger.warn(" ^0^ inner protocol. opcode={}, playerId={}", packet.getOpcode(), session.getPlayerId());
            return;
        }

        // 鉴权
        if (!pmw.isAllState() && !pmw.getStateSet().contains(session.getState())) {
            logger.debug(" ^0^ session state error. opcode={}, playerId={}", packet.getOpcode(), session.getPlayerId());
            return;
        }

        // 增加协议计数.
        pmw.incrCallNum();

        // 具体分配哪个线程去执行.
        this.dispatchClientPacket(session, packet, pmw, pmw.analysisParam(new MethodParamContext(session, packet)));
    }

    private void dispatchClientPacket(Session session, NetworkPacket packet, LocalPacketMethodWrapper pmw, Object... args) {
        final ClientCommand command = new ClientCommand(session, packet, pmw, args);
        switch (pmw.threadGroup()) {
            // Netty线程组
            case NettyThreadGroup: {
                ResultHelper.trySendResult(session, packet, pmw.invoke(args));
                break;
            }

            // 玩家线程组，队列ID就是玩家ID
            case PlayerThreadGroup: {
                this.dispatchCommand(session.getPlayerId(), command);
                break;
            }

            // 模块线程组，队列ID就是模块的主入口类的类名
            case ModuleThreadGroup: {
                this.dispatchCommand(pmw.getControllerClassName(), command);
                break;
            }

            // 队列线程组，队列ID就要从Session上找到对应的绑定值
            case QueueThreadGroup: {
                Object id = this.analyticalQueueId(session, packet, pmw);
                if (Objects.nonNull(id) && id instanceof Serializable) {
                    this.dispatchCommand((Serializable) id, command);
                }
                break;
            }

            // 非法的类型
            default: {
                throw new UnrealizedException("非法线程执行组:" + pmw.threadGroup());
            }
        }
    }

    private Object analyticalQueueId(Session session, NetworkPacket packet, LocalPacketMethodWrapper pmw) {
        // 如果是个队列封包，直接从封包中取队列ID
        if (packet instanceof QueueIdPacket) {
            return ((QueueIdPacket) packet).getQueueId();
        }
        // 如果是一个客户端请求，那就要从Session的绑定值中找到对应的队列ID
        return session.attr(SessionAttrKey.valueOf(pmw.getQueueIdKey())).get();
    }

    /**
     * 派发内部指令.
     *
     * @param playerId 玩家ID
     * @param opcode   协议编号
     * @param protocol 协议内容
     */
    public void dispatchInnerPacket(Serializable playerId, Serializable opcode, Object protocol) {
        LocalPacketMethodWrapper pmw = (LocalPacketMethodWrapper) PacketMethodManager.getInstance().getPacketMethodWrapper(opcode);
        if (pmw == null) {
            logger.warn("undefined protocol, opcode={}", opcode);
            return;
        }

        // 是否已废弃使用.
        if (pmw.isDeprecated()) {
            logger.warn("deprecated protocol. opcode={}, playerId={}", opcode, playerId);
            return;
        }

        // 增加协议计数.
        pmw.incrCallNum();

        // 具体分配哪个线程去执行.
        this.dispatchInnerPacket(pmw, playerId, pmw.analysisParam(new MethodParamContext(playerId, protocol)));
    }

    private void dispatchInnerPacket(LocalPacketMethodWrapper pmw, Serializable playerId, Object... args) {
        final DefaultCommand command = new InnerCommand(pmw, args);
        switch (pmw.threadGroup()) {
            // 玩家线程组，队列ID就是玩家ID
            case PlayerThreadGroup: {
                this.dispatchCommand(playerId, command);
                break;
            }

            // 模块线程组，队列ID就是模块的主入口类的类名
            case ModuleThreadGroup: {
                this.dispatchCommand(pmw.getControllerClassName(), command);
                break;
            }

            // 非法的类型
            default: {
                throw new UnrealizedException("非法线程执行组:" + pmw.threadGroup());
            }
        }
    }

    /**
     * 派发执行指令.
     *
     * @param queueId 队列ID
     * @param command 执行指令
     */
    public void dispatchCommand(Serializable queueId, TaskCommand command) {
        TaskQueue taskQueue = threadPoolTaskQueue.get(queueId);
        taskQueue.submit(new AsyncQueueTask(taskQueue, command));
    }

    /**
     * 派发一个任务。
     *
     * @param queueId  执行队列ID
     * @param callback 一个任务
     * @param printLog 是否输出执行耗时日志
     */
    public void dispatchTask(Serializable queueId, TaskCallback callback, boolean printLog) {
        AsyncTaskCommand command = new AsyncTaskCommand(callback);
        // 如果没有指定队列ID
        if (queueId == null) {
            businessThreadPool.execute(new DefaultAsyncTask(command, printLog));
        }
        // 有指定队列
        else {
            TaskQueue taskQueue = threadPoolTaskQueue.get(queueId);
            taskQueue.submit(new AsyncQueueTask(taskQueue, command));
        }
    }

    /**
     * 派发事件任务给线程池.
     *
     * @param traceId 链路追踪ID
     * @param handler 事件处理方法
     * @param event   事件对象
     */
    public void dispatchEvent(String traceId, EventMethodWrapper handler, Event event) {
        switch (handler.threadGroup()) {
            // 玩家线程组，队列ID就是玩家ID
            case PlayerThreadGroup: {
                if (event instanceof PlayerEvent) {
                    PlayerEvent e = (PlayerEvent) event;
                    this.dispatchCommand(e.getPlayerId(), new DefaultCommand(traceId, handler, e));
                } else {
                    throw new UnrealizedException("玩家线程监听的事件，需要实现PlayerEvent接口. event=" + event.getClass().getSimpleName());
                }
                break;
            }

            // 模块线程组，队列ID就是模块的主入口类的类名
            case ModuleThreadGroup: {
                this.dispatchCommand(handler.getControllerClassName(), new DefaultCommand(traceId, handler, event));
                break;
            }

            // 队列线程组，队列ID就要从QueueEvent里取出来
            case QueueThreadGroup: {
                if (event instanceof QueueEvent) {
                    this.dispatchCommand(((QueueEvent) event).getQueueId(), new DefaultCommand(traceId, handler, event));
                } else {
                    throw new UnrealizedException("队列线程监听的事件，需要实现QueueEvent接口. event=" + event.getClass().getSimpleName());
                }
                break;
            }

            default:
                throw new UnrealizedException("事件监听发现了非法线程执行组:" + handler.threadGroup());
        }
    }

    /**
     * 派发定时事件任务给线程池.
     *
     * @param traceId 追踪ID
     * @param handler 事件处理方法
     * @param event   事件对象
     */
    public void dispatchFixedTimeEvent(String traceId, EventMethodWrapper handler, FixedTimeEvent event) {
        switch (handler.threadGroup()) {
            // 玩家线程组，队列ID就是玩家ID
            case PlayerThreadGroup: {
                // 当前所有在线的玩家才会收到，离开是不会收到此事件
                for (Serializable playerId : SessionManager.getOnlinePlayerIdList()) {
                    this.dispatchCommand(playerId, new DefaultCommand(traceId, handler, handler.analysisParam(playerId, event)));
                }
                break;
            }

            // 模块线程组，队列ID就是模块的主入口类的类名
            case ModuleThreadGroup: {
                this.dispatchCommand(handler.getControllerClassName(), new DefaultCommand(traceId, handler, event));
                break;
            }

            // 队列线程组，队列ID就要从QueueEvent里取出来
            case QueueThreadGroup: {
                if (event instanceof QueueEvent) {
                    this.dispatchCommand(((QueueEvent) event).getQueueId(), new DefaultCommand(traceId, handler, event));
                } else {
                    throw new UnrealizedException("队列线程监听的事件，需要实现QueueEvent接口. event=" + event.getClass().getSimpleName());
                }
                break;
            }
            default:
                throw new UnrealizedException("事件监听发现了非法线程执行组:" + handler.threadGroup());
        }
    }

    /**
     * 派发延迟任务.
     *
     * @param handler 延迟任务处理方法
     */
    public void dispatchScheduled(ScheduledMethodWrapper handler) {
        switch (handler.threadGroup()) {
            // 模块线程组，队列ID就是模块的主入口类的类名
            case ModuleThreadGroup: {
                this.dispatchCommand(handler.getControllerClassName(), new DefaultCommand(handler.getTraceId(), handler));
                break;
            }

            // 玩家线程组，那就是要所有在线的人都要发一条
            case PlayerThreadGroup: {
                for (Serializable playerId : SessionManager.getOnlinePlayerIdList()) {
                    this.dispatchCommand(playerId, new DefaultCommand(handler.getTraceId(), handler, playerId));
                }
                break;
            }
            default:
                throw new UnrealizedException("@Scheduled只能应用在系统模块或玩家模块：" + handler.threadGroup());
        }
    }

    /**
     * 停止接受新的任务，把老的都处理掉.
     */
    public void shutdown() {
        logger.info("开始通知停止处理业务逻辑的线程池停止服务.");
        businessThreadPool.shutdown();
        try {
            if (!businessThreadPool.awaitTermination(SHUTDOWN_MAX_TIME, TimeUnit.MINUTES)) {
                businessThreadPool.shutdownNow();
            }
            logger.info("处理业务逻辑的线程池已停止服务");
        } catch (InterruptedException ie) {
            logger.error("停止处理业务逻辑的线程池时发生异常.", ie);
            businessThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}