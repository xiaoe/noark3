package xyz.noark.network.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Service;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.manager.HttpMethodManager;
import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;
import xyz.noark.core.ioc.wrap.param.HttpParamWrapper;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.core.util.IpUtils;

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
@Service
@ChannelHandler.Sharable
public class DispatcherServlet extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ViewResolver viewResolver = new DefaultViewResolver();
    @Autowired
    private ThreadDispatcher threadDispatcher;
    @Autowired
    private HandleInterceptChain handleInterceptChain;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("http client active. channel={}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("http client inactive. channel={}", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 参数解析异常时会
        viewResolver.resolveException(new NoarkHttpServletResponse(ctx, false), cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fhr) throws Exception {
        HttpServletRequest request = this.buildRequest(ctx, fhr);
        HttpServletResponse response = new NoarkHttpServletResponse(ctx, HttpUtil.isKeepAlive(fhr));
        this.doDispatch(request, response);
    }

    private void doDispatch(HttpServletRequest request, HttpServletResponse response) {
        // 获取URI对应的处理器
        HttpMethodWrapper handler = HttpMethodManager.getHttpHandler(request.getMethod(), request.getUri());

        // 获取指定队列参数值
        final String queueId = handler == null ? null : request.getParameter(handler.getQueueId());

        // 异步派发
        long createTime = System.nanoTime();
        threadDispatcher.dispatch(queueId, () -> this.exec(request, response, handler, createTime));
    }

    private void exec(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler, long createTime) {
        // 这里已非Netty线程了
        final long startExecTime = System.nanoTime();
        try {
            this.doAction(request, response, handler);
        } catch (Throwable e) {
            viewResolver.resolveException(response, e);
        } finally {
            response.flush();
            final long endExecuteTime = System.nanoTime();
            logger.info("handle http({}),delay={} ms,exe={} ms,ip={}", request.getUri(), (startExecTime - createTime) / 100_0000F, (endExecuteTime - startExecTime) / 100_0000F, request.getRemoteAddr());
        }
    }

    private void doAction(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler) throws Exception {
        // 没有找到对应的处理器
        if (handler == null) {
            noHandlerFound(request, response);
            return;
        }

        // 已废弃
        if (handler.isDeprecated()) {
            handleDeprecated(request, response);
            return;
        }

        // 1. 业务执行前触发postHandle
        if (handleInterceptChain.triggerPreHandle(request, response, handler)) {
            return;
        }

        try {
            // 解析参数并执行业务逻辑
            Object result = handler.invoke(this.analysisParam(handler, request));

            // 2. 业务执行后触发postHandle
            handleInterceptChain.triggerPostHandle(request, response, handler, result);

            // 视图渲染(没有实现View，结果就当Model吧)
            render(request, response, handler, result);
        } finally {
            // 3. 渲染结束后触发AfterCompletion
            handleInterceptChain.triggerAfterCompletion(request, response, handler);
        }
    }

    private void render(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler, Object result) {
        viewResolver.resolveView(request, response, handler, result);
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

    private void handleDeprecated(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("request's API Deprecated. ip={}, uri={}", request.getRemoteAddr(), request.getUri());
        response.setStatus(HttpResponseStatus.NOT_FOUND.code());
        response.writeObject(new HttpResult(HttpErrorCode.API_DEPRECATED, "request's API Deprecated."));
    }

    private void noHandlerFound(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("request's API Unrealized. ip={}, uri={}", request.getRemoteAddr(), request.getUri());
        response.setStatus(HttpResponseStatus.NOT_FOUND.code());
        response.writeObject(new HttpResult(HttpErrorCode.NO_API, "request's API Unrealized."));
    }

    private HttpServletRequest buildRequest(ChannelHandlerContext ctx, FullHttpRequest fhr) throws IOException {
        final QueryStringDecoder decoder = new QueryStringDecoder(fhr.uri());
        final String ip = IpUtils.getIp(ctx.channel().remoteAddress());
        Map<String, String> parameters = HttpParameterParser.parse(fhr, decoder);
        return new NoarkHttpServletRequest(decoder.path(), fhr.method(), parameters, ip);
    }
}
