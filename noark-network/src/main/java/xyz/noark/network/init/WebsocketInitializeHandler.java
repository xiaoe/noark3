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
package xyz.noark.network.init;

import static xyz.noark.log.LogHelper.logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Component;
import xyz.noark.core.annotation.Value;
import xyz.noark.network.NetworkConstant;
import xyz.noark.network.handler.WebsocketServerHandler;

/**
 * WebSocket协议请求.
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
@Component(name = WebsocketInitializeHandler.WEBSOCKET_NAME)
public class WebsocketInitializeHandler extends AbstractInitializeHandler {
	public static final String WEBSOCKET_NAME = "_______websocket_______";
	/** 是否为WebSocket */
	@Value(NetworkConstant.WEBSOCKET_PATH)
	protected String websocketPath = "/game";
	@Autowired
	private WebsocketServerHandler websocketServerHandler;

	@Override
	public void handle(ChannelHandlerContext ctx) {
		logger.debug("WebSocket链接...");
		ChannelPipeline pipeline = ctx.pipeline();
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new HttpObjectAggregator(65535));
		pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath));
		pipeline.addLast(websocketServerHandler);
	}
}