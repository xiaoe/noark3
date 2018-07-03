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
package xyz.noark.core.thread;

import static xyz.noark.log.LogHelper.logger;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import xyz.noark.core.annotation.Component;
import xyz.noark.core.event.Event;
import xyz.noark.core.event.PlayerEvent;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.manager.PacketMethodManager;
import xyz.noark.core.ioc.wrap.method.EventMethodWrapper;
import xyz.noark.core.ioc.wrap.method.PacketMethodWrapper;
import xyz.noark.core.lang.TimeoutHashMap;
import xyz.noark.core.network.Session;
import xyz.noark.core.thread.command.PlayerThreadCommand;
import xyz.noark.core.thread.command.SystemThreadCommand;

/**
 * 线程调度器.
 * <p>
 * 根据opcode找到目标模块的负载均衡器，进行转发或传递给执行器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Component(name = "ThreadDispatcher")
public class ThreadDispatcher {
	/** 非场景类的线程池... */
	private final ExecutorService logicPool;
	/** 非场景线程处理的任务队列 */
	private final TimeoutHashMap<Serializable, TaskQueue> logicPoolTaskQueue;

	public ThreadDispatcher() {
		this.logicPool = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("logic"));
		this.logicPoolTaskQueue = new TimeoutHashMap<>(1, TimeUnit.MINUTES, () -> new TaskQueue(logicPool));
	}

	/**
	 * 派发游戏封包.
	 * 
	 * @param session Session对象
	 * @param pmw 协议处理方法
	 * @param args 处理方法参数
	 */
	public void dispatchPacket(Session session, Integer opcode, byte[] bytes) {
		PacketMethodWrapper pmw = PacketMethodManager.getInstance().getPacketMethodWrapper(opcode);
		if (pmw == null) {
			logger.warn("undefined protocol, opcode={}", opcode);
			return;
		}

		// 是否已废弃使用.
		if (pmw.isDeprecated()) {
			logger.warn("deprecated protocol. opcode={}, playerId={}", opcode, session.getPlayerId());
			return;
		}

		// 客户端发来的封包，是不可以调用内部处理器的.
		if (pmw.isInner()) {
			logger.warn(" ^0^ inner protocol. opcode={}, playerId={}", opcode, session.getPlayerId());
			return;
		}

		// 增加协议计数.
		pmw.incrCount();

		// 具体分配哪个线程去执行.
		this.dispatchPacket(session.getPlayerId(), pmw, pmw.analysisParam(session, bytes));
	}

	/**
	 * 派发内部指令.
	 * 
	 * @param playerId 玩家ID
	 * @param pmw 协议处理方法
	 * @param args 处理方法参数
	 */
	public void dispatchInnerPacket(Serializable playerId, Integer opcode, Object protocal) {
		PacketMethodWrapper pmw = PacketMethodManager.getInstance().getPacketMethodWrapper(opcode);
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
		pmw.incrCount();

		// 具体分配哪个线程去执行.
		this.dispatchPacket(playerId, pmw, pmw.analysisParam(playerId, protocal));
	}

	private void dispatchPacket(Serializable playerId, PacketMethodWrapper pmw, Object... args) {
		switch (pmw.threadGroup()) {
		case NettyThreadGroup:
			this.dispatchNettyThreadHandle(pmw, args);
			break;
		case PlayerThreadGroup:
			this.dispatchPlayerThreadHandle(new PlayerThreadCommand(playerId, pmw, args));
			break;
		case ModuleThreadGroup:
			this.dispatchSystemThreadHandle(new SystemThreadCommand(pmw.getModule(), pmw, args));
			break;
		default:
			throw new UnrealizedException("非法线程执行组:" + pmw.threadGroup());
		}
	}

	/** 派发给Netty线程处理的逻辑. */
	private void dispatchNettyThreadHandle(PacketMethodWrapper protocal, Object... args) {
		protocal.invoke(args);
	}

	/** 派发给系统线程处理的逻辑. */
	void dispatchSystemThreadHandle(SystemThreadCommand command) {
		TaskQueue taskQueue = logicPoolTaskQueue.get(command.getModule());
		taskQueue.submit(new AsyncTask(taskQueue, command));
	}

	/** 派发给玩家线程处理的逻辑. */
	void dispatchPlayerThreadHandle(PlayerThreadCommand command) {
		TaskQueue taskQueue = logicPoolTaskQueue.get(command.getPlayerId());
		taskQueue.submit(new AsyncTask(taskQueue, command));
	}

	/** 派发事件任务给线程池. */
	public void dispatchEvent(EventMethodWrapper handler, Event event) {
		switch (handler.threadGroup()) {
		case PlayerThreadGroup: {
			if (event instanceof PlayerEvent) {
				PlayerEvent e = (PlayerEvent) event;
				this.dispatchPlayerThreadHandle(new PlayerThreadCommand(e.getPlayerId(), handler, e));
			} else {
				throw new UnrealizedException("玩家线程监听的事件，需要实现PlayerEvent接口. event=" + event.getClass().getSimpleName());
			}
			break;
		}
		case ModuleThreadGroup:
			this.dispatchSystemThreadHandle(new SystemThreadCommand(handler.getModule(), handler, event));
			break;
		default:
			throw new UnrealizedException("事件监听发现了非法线程执行组:" + handler.threadGroup());
		}
	}
}