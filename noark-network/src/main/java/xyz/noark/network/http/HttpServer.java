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
package xyz.noark.network.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Service;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.network.TcpServer;
import xyz.noark.network.NetworkConstant;

import static xyz.noark.log.LogHelper.logger;

/**
 * HTTP服务器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
@Service
public class HttpServer implements TcpServer {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    @Value(NetworkConstant.HTTP_PORT)
    private int port = 0;
    @Value(NetworkConstant.HTTP_SECRET_KEY)
    private String secretKey = null;

    @Autowired
    private DispatcherServlet dispatcherServlet;

    /**
     * 向内部提供HTTP服务的最大内容长度（默认：1048576=1M）
     */
    @Value(NetworkConstant.HTTP_MAX_CONTENT_LENGTH)
    private int maxContentLength = 1048576;

    /**
     * 设置端口.
     *
     * @param port 端口
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 设置密钥.
     *
     * @param secretKey 密钥
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void startup() {
        if (port <= 0) {
            logger.debug("http server not started.");
            return;
        }

        logger.info("http server start on {}", port);
        ServerBootstrap bootstrap = new ServerBootstrap();
        // Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。默认值，Windows为200，其他为128。
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);

        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpServerCodec());
                p.addLast(new HttpObjectAggregator(maxContentLength));
                p.addLast(new ChunkedWriteHandler());
                p.addLast(dispatcherServlet);
            }
        });

        try {
            bootstrap.bind(port).sync();
            logger.info("http server start is success.");
        } catch (Exception e) {
            throw new ServerBootstrapException("目标端口已被占用 port=" + port, e);
        }
    }

    @Override
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}