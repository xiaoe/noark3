package xyz.noark.network.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.manager.HttpMethodManager;
import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;
import xyz.noark.core.ioc.wrap.param.HttpParamWrapper;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.core.util.IpUtils;
import xyz.noark.network.http.exception.HandlerDeprecatedException;
import xyz.noark.network.http.exception.NoHandlerFoundException;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static xyz.noark.log.LogHelper.logger;

/**
 * HTTP协议处理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ThreadDispatcher threadDispatcher;
    private final ViewResolver viewResolver;

    public HttpServerHandler(ThreadDispatcher threadDispatcher, ViewResolver viewResolver) {
        this.threadDispatcher = threadDispatcher;
        this.viewResolver = viewResolver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.debug("发现HTTP客户端链接，channel={}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.debug("HTTP客户端断开链接. channel={}", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        viewResolver.resolveException(new NoarkHttpServletResponse(ctx), cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fhr) throws Exception {
        this.handleFullHttpRequest(ctx, fhr);
    }

    private void handleFullHttpRequest(ChannelHandlerContext ctx, FullHttpRequest fhr) throws Exception {
        // URL解析器，主要为了解析出URI和GET部分参数
        final QueryStringDecoder decoder = new QueryStringDecoder(fhr.uri());
        final String uri = decoder.path();
        final String ip = IpUtils.getIp(ctx.channel().remoteAddress());

        // 获取URI对应的处理器
        HttpMethodWrapper handler = HttpMethodManager.getHttpHandler(fhr.method().toString(), uri);

        // 404，没有找到对应的处理器
        if (handler == null) {
            logger.debug("request's API Unrealized. ip={}, uri={}", ip, uri);
            throw new NoHandlerFoundException(fhr.method().name(), uri);
        }

        // 已废弃
        if (handler.isDeprecated()) {
            logger.debug("request's API Deprecated. ip={}, uri={}", ip, uri);
            throw new HandlerDeprecatedException(fhr.method().name(), uri);
        }

        HttpServletRequest request = this.buildRequest(fhr, decoder, uri, ip);
        NoarkHttpServletResponse response = new NoarkHttpServletResponse(ctx);
        this.handleHttpRequest(handler, request, response);
    }


    private void handleHttpRequest(HttpMethodWrapper handler, HttpServletRequest request, NoarkHttpServletResponse response) {
        // 分析参数
        final Object[] args = this.analysisParam(handler, request);

        // 获取指定队列参数值
        String queueId = request.getParameter(handler.getQueueId());

        // 异步派发
        long createTime = System.nanoTime();
        threadDispatcher.dispatch(queueId, () -> this.exec(handler, args, request, response, createTime));
    }

    private Object[] analysisParam(HttpMethodWrapper handler, HttpServletRequest request) {
        // 如果没有参数，返回null.
        if (handler.getParameters().isEmpty()) {
            return new Object[0];
        }

        List<Object> args = new ArrayList<>(handler.getParameters().size());
        for (HttpParamWrapper param : handler.getParameters()) {
            // Request请求参数
            if (HttpServletRequest.class.isAssignableFrom(param.getParameter().getType())) {
                args.add(request);
            }
            // 其他转化器参数
            else {
                Converter<?> converter = this.getConverter(param.getParameter());
                String data = request.getParameter(param.getName());

                if (data == null) {
                    // 必选参数必需有值
                    if (param.isRequired()) {
                        throw new ConvertException("HTTP request param error. uri=" + request.getUri() + "," + param.getName() + " is required.");
                    }
                    // 不是必选参数，那就使用默认值来转化
                    else {
                        try {
                            args.add(converter.convert(param.getParameter(), param.getDefaultValue()));
                        } catch (Exception e) {
                            throw new ConvertException("HTTP request default param error. uri=" + request.getUri() + "," + param.getName() + "=" + param.getDefaultValue() + "-->" + converter.buildErrorMsg(), e);
                        }
                    }
                } else {
                    try {
                        args.add(converter.convert(param.getParameter(), data));
                    } catch (Exception e) {
                        throw new ConvertException("HTTP request param error. uri=" + request.getUri() + "," + param.getName() + "=" + data + "-->" + converter.buildErrorMsg(), e);
                    }
                }
            }
        }
        return args.toArray();
    }

    private Converter<?> getConverter(Parameter field) {
        Converter<?> result = ConvertManager.getInstance().getConverter(field.getType());
        if (result == null) {
            throw new UnrealizedException("未实现的注入(" + field.getType().getName() + ")" + field.getName());
        }
        return result;
    }

    private HttpServletRequest buildRequest(FullHttpRequest fhr, QueryStringDecoder decoder, String uri, String ip) throws IOException {
        Map<String, String> parameters = HttpParameterParser.parse(fhr, decoder);
        return new NoarkHttpServletRequest(uri, parameters, ip);
    }

    private void exec(HttpMethodWrapper handler, Object[] args, HttpServletRequest request, HttpServletResponse response, long createTime) {
        // 这里已非Netty线程了
        final long startExecuteTime = System.nanoTime();
        try {
            //过滤器

            Object result = handler.invoke(args);


            viewResolver.resolveView(request, response, handler, result);
        } catch (Throwable e) {
            viewResolver.resolveException(response, e);
        } finally {
            final long endExecuteTime = System.nanoTime();
            logger.info("handle {},delay={} ms,exe={} ms,ip={}", handler.logCode(), (startExecuteTime - createTime) / 100_0000F, (endExecuteTime - startExecuteTime) / 100_0000F, request.getRemoteAddr());
        }
    }
}
