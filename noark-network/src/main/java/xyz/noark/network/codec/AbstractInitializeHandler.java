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
package xyz.noark.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.network.ProtocalCodec;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.network.NettyServerHandler;

/**
 * 抽象实现的初始化协议处理器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public abstract class AbstractInitializeHandler implements InitializeHandler {

	@Autowired
	private ThreadDispatcher threadDispatcher;
	
	@Override
	public void handle(ChannelHandlerContext ctx) {
		final ChannelPipeline pipeline = ctx.pipeline();
		// TODO 确认没有并发可以优化一下单例方法
		pipeline.addLast("decoder", createPacketDecoder());
		pipeline.addLast("handle", new NettyServerHandler(threadDispatcher));

		// 为Session绑定编解码.
		// NettySession session
	}

	/**
	 * 创建一个封包解码器.
	 * 
	 * @return 封包解码器
	 */
	protected abstract ByteToMessageDecoder createPacketDecoder();

	/**
	 * 创建协议编解码.
	 * 
	 * @param session Session对象.
	 */
	protected abstract ProtocalCodec createProtocalCodec();
}