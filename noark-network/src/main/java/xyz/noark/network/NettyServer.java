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

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Component;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.bootstrap.ServerBootstrapException;
import xyz.noark.core.network.TcpServer;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.network.codec.InitializeDecoder;
import xyz.noark.network.codec.InitializeManager;

/**
 * 基于Netty实现的一个网络服务.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Component(name = "NettyServer")
public class NettyServer implements TcpServer {
	// Boss线程就用一个线程
	private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	// Work线程:CPU<=4的话CPU*2,CPU<=8的话CPU+4, 其他直接使用12
	private final EventLoopGroup workGroup;

	private final ServerBootstrap bootstrap;

	private static final int DEFAULT_EVENT_LOOP_THREADS;
	static {
		int count = Runtime.getRuntime().availableProcessors();
		if (count <= 4) {
			DEFAULT_EVENT_LOOP_THREADS = count * 2;
		} else if (count <= 8) {
			DEFAULT_EVENT_LOOP_THREADS = count + 4;
		} else {
			DEFAULT_EVENT_LOOP_THREADS = 12;
		}
	}

	// Netty监听端口
	@Value("network.port")
	private int port = 8888;

	// 心跳功能，默认值为0，则不生效
	@Value("network.heartbeat")
	private int heartbeat = 0;

	// Netty的Work线程数
	@Value("network.work.threads")
	private int workthreads = 0;

	// 是否为WebSocket
	@Value("network.websocket.path")
	private String websocketPath;

	@Autowired
	private InitializeManager initializeManager;
	@Autowired
	private ThreadDispatcher threadDispatcher;
	//@Autowired
	private NettyServerHandler nettyServerHandler;

	public NettyServer() {
		// TODO 有时间来实现一个NoarkLog的工厂...
		// InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);

		bootstrap = new ServerBootstrap();
		this.workGroup = new NioEventLoopGroup(workthreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : workthreads);
		bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);

		// http://www.jianshu.com/p/0bff7c020af2
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);

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
	 * @param context ChannelContext上下文
	 */
	protected void buildChannelPipeline(ChannelPipeline pipeline) {
		// 配置了心跳功能，则启用心跳机制.
		if (heartbeat > 0) {
			pipeline.addLast("idleStateHandler", new IdleStateHandler(heartbeat, 0, 0, TimeUnit.SECONDS));
		}

		pipeline.addLast(new InitializeDecoder(initializeManager));

		pipeline.addLast("handle", new NettyServerHandler(threadDispatcher));
	}

	@Override
	public void startup() {
		logger.info("game tcp server start on {}", port);
		try {
			bootstrap.bind(port).sync();
			logger.info("game tcp server start is success.");
		} catch (Exception e) {
			throw new ServerBootstrapException("目标端口已被占用 port=" + port, e);
		}
	}

	@Override
	public void shutdown() {
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
}