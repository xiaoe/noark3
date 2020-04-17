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
package xyz.noark.robot;

import static xyz.noark.log.LogHelper.logger;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Service;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.event.EventManager;
import xyz.noark.core.network.PacketCodecHolder;
import xyz.noark.core.network.Session.State;
import xyz.noark.core.network.SessionManager;
import xyz.noark.core.util.DateUtils;
import xyz.noark.core.util.ThreadUtils;
import xyz.noark.network.codec.AbstractPacketCodec;
import xyz.noark.network.init.SocketInitializeHandler;

/**
 * 机器人管理类.
 *
 * @since 3.4
 * @author 小流氓[176543888@qq.com]
 */
@Service
public class RobotManager {
	/** 机器人启动数量 */
	@Value(RobotConstant.ROBOT_NUM)
	private int robotNum = 1;
	/** 启动机器人的间隔（单位：秒） */
	@Value(RobotConstant.ROBOT_CREATE_INTERVAL)
	private int createInterval = 1;
	/** 机器人的AI间隔（单位：秒） */
	@Value(RobotConstant.ROBOT_AI_INTERVAL)
	private int aiInterval = 1;
	/** 机器人的账号前缀（默认："robot:"） */
	@Value(RobotConstant.ROBOT_ACCOUNT_PREFIX)
	private String accountPrefix = "robot:";

	@Autowired
	private EventManager eventManager;
	@Autowired
	private RobotClientHandler robotClientHandler;

	private static final Bootstrap BOOTSTRAP = new Bootstrap();
	private final ConcurrentMap<String, Robot> robots = new ConcurrentHashMap<>(2048);

	@PostConstruct
	public void initBootstrap() {
		final AbstractPacketCodec packetCodec = (AbstractPacketCodec) PacketCodecHolder.getPacketCodec();
		BOOTSTRAP.group(new NioEventLoopGroup());
		BOOTSTRAP.channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("decoder", packetCodec.lengthDecoder());
				pipeline.addLast("encoder", packetCodec.lengthEncoder());
				pipeline.addLast("hanlder", robotClientHandler);
			}
		});
	}

	public void init(AbstractRobotBootstrap bootstrap) {
		logger.info("环境准备OK，开始创建机器人...");
		for (int id = 1; id <= robotNum; id++) {
			Robot robot = this.createRobot(id, bootstrap);
			robots.put(robot.getPlayerId(), robot);
			eventManager.publish(new RobotAiEvent(robot.getPlayerId(), DateUtils.addSeconds(new Date(), aiInterval)));
			ThreadUtils.sleep(1L * createInterval * DateUtils.MILLISECOND_PER_SECOND);
		}
	}

	private Robot createRobot(int id, AbstractRobotBootstrap bootstrap) {
		logger.info("创建机器人 id={}", id);
		final String playerId = accountPrefix + id;
		return new Robot(playerId, bootstrap.rebuildAi(playerId));
	}

	public Robot getRobot(String playerId) {
		return robots.get(playerId);
	}

	public <T> T getData(String playerId, Class<? extends T> klass) {
		return robots.get(playerId).getData(klass);
	}

	public void connect(String playerId, String ip, int port) throws InterruptedException {
		logger.debug("TCP链接");
		Channel channel = BOOTSTRAP.connect(ip, port).sync().channel();
		logger.debug("链接成功，发送暗号，请求密钥...");
		RobotSession session = (RobotSession) SessionManager.createSession(channel.id(), key -> new RobotSession(channel));
		session.setPlayerId(playerId);
		session.setState(State.INGAME);
		SessionManager.bindPlayerIdAndSession(session.getPlayerId(), session);
		logger.debug("创建Session={}", session.getId());
		channel.writeAndFlush(SocketInitializeHandler.SOCKET_NAME);
	}
}