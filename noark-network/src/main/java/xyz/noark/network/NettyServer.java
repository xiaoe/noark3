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
package xyz.noark.network;

import static xyz.noark.log.LogHelper.logger;

import java.net.BindException;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLoggerFactory;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Component;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.network.TcpServer;
import xyz.noark.network.log.NettyLoggerFactory;

/**
 * 基于Netty实现的一个网络服务.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Component(name = "NettyServer")
public class NettyServer implements TcpServer {
	private final ServerBootstrap bootstrap;
	/** Boss线程就用一个线程 */
	private final EventLoopGroup bossGroup;
	/** Work线程:CPU<=4的话CPU*2,CPU<=8的话CPU+4, 其他直接使用12 */
	private final EventLoopGroup workGroup;

	/** Netty监听端口 */
	@Value(NetworkConstant.PORT)
	protected int port = 9527;

	/** 心跳功能，默认值为0，则不生效 */
	@Value(NetworkConstant.HEARTBEAT)
	protected int heartbeat = 0;

	/** Netty的Work线程数 */
	@Value(NetworkConstant.WORK_THREADS)
	protected int workthreads = 0;

	/** 网络封包日志激活 */
	@Value(NetworkConstant.LOG_ACTIVE)
	protected boolean logActive = false;

	/** Netty低水位，默认值32K */
	@Value(NetworkConstant.LOW_WATER_MARK)
	private int DEFAULT_LOW_WATER_MARK = 32 * 1024;
	/** Netty高水位，默认值64K */
	@Value(NetworkConstant.HIGH_WATER_MARK)
	private int DEFAULT_HIGH_WATER_MARK = 64 * 1024;

	@Autowired
	protected InitializeHandlerManager initializeHandlerManager;
	@Autowired
	protected NettyServerHandler nettyServerHandler;

	public NettyServer() {
		this.bootstrap = new ServerBootstrap();

		final int nThreads = workthreads == 0 ? NetworkConstant.DEFAULT_EVENT_LOOP_THREADS : workthreads;
		if (Epoll.isAvailable()) {
			this.bossGroup = new EpollEventLoopGroup(1);
			this.workGroup = new EpollEventLoopGroup(nThreads);
			bootstrap.group(bossGroup, workGroup).channel(EpollServerSocketChannel.class);
		} else {
			this.bossGroup = new NioEventLoopGroup(1);
			this.workGroup = new NioEventLoopGroup(nThreads);
			bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);
		}

		// http://www.jianshu.com/p/0bff7c020af2

		// Socket参数，地址复用，默认值false
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		// Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。默认值，Windows为200，其他为128。
		bootstrap.option(ChannelOption.SO_BACKLOG, 65535);

		// TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。
		// 该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，
		// 如果需要发送一些较小的报文，则需要禁用该算法。Netty默认禁用该算法，从而最小化报文传输延时。
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		// Netty参数，写低水位标记，默认值32KB。当Netty的写缓冲区中的字节超过高水位之后若下降到低水位，则Channel的isWritable()返回True。
		// Netty参数，写高水位标记，默认值64KB。如果Netty的写缓冲区中的字节超过该值，Channel的isWritable()返回False。
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(DEFAULT_LOW_WATER_MARK, DEFAULT_HIGH_WATER_MARK));

		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) {
				buildChannelPipeline(ch.pipeline());
			}
		});
	}

	/**
	 * 构建ChannelPipeline通道.
	 * 
	 * @param pipeline ChannelPipeline通道
	 */
	protected void buildChannelPipeline(ChannelPipeline pipeline) {
		// 配置了心跳功能，则启用心跳机制.
		if (heartbeat > 0) {
			pipeline.addLast("idleStateHandler", new IdleStateHandler(heartbeat, 0, 0, TimeUnit.SECONDS));
		}

		// 输出具体的Netty接受与发送封包的日志
		if (logActive) {
			pipeline.addLast("logger", new LoggingHandler(LogLevel.DEBUG));
		}

		// 统一默认的事件处理...
		pipeline.addLast(nettyServerHandler);

		// 初始化封包处理器.
		pipeline.addLast(new InitializeDecoder(initializeHandlerManager));
	}

	@Override
	public void startup() {
		logger.info("game tcp server start on {}", port);

		// 如果封包日志打开的话，需要桥接进Noark日志实现
		if (logActive) {
			InternalLoggerFactory.setDefaultFactory(NettyLoggerFactory.INSTANCE);
		}

		try {
			bootstrap.bind(port).sync();
			logger.info("game tcp server start is success.");
		} catch (Exception e) {
			// 竟然不能直接捕获此异常，有点想不明白...
			if (e instanceof BindException) {
				throw new ServerBootstrapException("目标端口已被占用 port=" + port, e);
			}
			throw new ServerBootstrapException("未知异常", e);
		}
	}

	@Override
	public void shutdown() {
		Future<?> boosFuture = bossGroup.shutdownGracefully();
		Future<?> workFuture = workGroup.shutdownGracefully();
		try {
			if (boosFuture.await(NetworkConstant.SHUTDOWN_MAX_TIME, TimeUnit.MINUTES)) {
				logger.info("NettyBoss关闭成功.");
			}
			if (workFuture.await(NetworkConstant.SHUTDOWN_MAX_TIME, TimeUnit.MINUTES)) {
				logger.info("NettyWork关闭成功.");
			}
		} catch (InterruptedException ie) {
			logger.error("关闭网络时发生异常.", ie);
		}
	}
}