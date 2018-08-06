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
package xyz.noark.network.initialize;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.network.NetworkListener;
import xyz.noark.core.network.Session;
import xyz.noark.core.network.SessionManager;
import xyz.noark.network.InitializeHandler;
import xyz.noark.network.NettySession;

/**
 * 抽象实现的初始化协议处理器.
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public abstract class AbstractInitializeHandler implements InitializeHandler {

	@Autowired(required = false)
	private NetworkListener networkListener;

	@Override
	public void handle(ChannelHandlerContext ctx) {
		this.build(ctx.pipeline());

		// 只要第一个协议对了就要创建Session...
		Session session = SessionManager.createSession(ctx.channel().id(), key -> new NettySession(ctx.channel()));

		if (networkListener != null) {
			networkListener.channelActive(session);
		}
	}

	/**
	 * 判定具体协议后再重装ChannelPipeline对象.
	 * 
	 * @param pipeline ChannelPipeline对象
	 */
	protected abstract void build(ChannelPipeline pipeline);
}