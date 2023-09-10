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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.ExceptionHelper;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.manager.HttpMethodManager;
import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;
import xyz.noark.core.ioc.wrap.param.HttpParamWrapper;
import xyz.noark.core.thread.ThreadDispatcher;
import xyz.noark.core.thread.TraceIdFactory;
import xyz.noark.core.util.DateUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.log.MDC;
import xyz.noark.network.NetworkConstant;
import xyz.noark.network.http.exception.HandlerDeprecatedException;
import xyz.noark.network.http.exception.NoHandlerFoundException;
import xyz.noark.network.http.exception.UnrealizedQueueIdException;
import xyz.noark.network.util.NettyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static xyz.noark.log.LogHelper.logger;

/**
 * HTTP协议处理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@ChannelHandler.Sharable
public class DispatcherServlet extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ViewResolver viewResolver = new DefaultViewResolver();
    @Autowired
    private HandleInterceptChain handleInterceptChain;
    /**
     * HTTP报文是否输出，默认不输出
     */
    @Value(value = NetworkConstant.HTTP_LOG_ENABLED, autoRefreshed = true)
    private boolean logEnabled = false;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        MDC.put(TraceIdFactory.TRACE_ID, ctx.channel().id().asShortText());
        logger.debug("http client active. channel={}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        MDC.put(TraceIdFactory.TRACE_ID, ctx.channel().id().asShortText());
        logger.debug("http client inactive. channel={}", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 忽略那些java.io.IOException: 远程主机强迫关闭了一个现有的连接
        if (cause instanceof IOException) {
            return;
        }
        logger.error("未处理的异常={}", cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fhr) {
        TraceIdFactory.initRandomTraceId();
        final String ip = NettyUtils.analyzeIp(fhr, ctx);

        // 再申明一次，是保证输出同步
        final boolean logEnabled = this.logEnabled;
        if (logEnabled) {
            HttpOutputManager.logRequest(fhr, ip);
        }

        final QueryStringDecoder decoder = new QueryStringDecoder(fhr.uri());
        // HTTP请求
        NoarkHttpServletRequest request = new NoarkHttpServletRequest(decoder.path(), fhr.method(), ip);
        // HTTP响应
        HttpServletResponse response = new NoarkHttpServletResponse(ctx, HttpUtil.isKeepAlive(fhr), logEnabled);
        // 获取URI对应的处理器
        HttpMethodWrapper handler = HttpMethodManager.getHttpHandler(request.getMethod(), request.getUri());

        boolean dispatchException = false;
        try {
            // 解析请求参数
            request.parse(fhr, decoder);
            if (logEnabled) {
                HttpOutputManager.logParameter(request);
            }

            // 线程调度
            this.doDispatch(request, response, handler);
        }
        // 如果在解析参数和派发任务时抛出异常
        catch (Throwable e) {
            dispatchException = true;
            this.processHandlerException(request, response, e);
        } finally {
            if (dispatchException) {
                response.flush();
            }
        }
    }

    private void doDispatch(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler) {
        // 没有找到对应的处理器
        if (handler == null) {
            throw new NoHandlerFoundException(request.getMethod(), request.getUri());
        }

        // 已废弃
        if (handler.isDeprecated()) {
            throw new HandlerDeprecatedException(request.getMethod(), request.getUri());
        }

        // 获取指定队列参数值
        final Serializable queueId = this.getQueueId(handler, request);
        // 异步派发
        final long createTime = System.nanoTime();
        ThreadDispatcher.getInstance().dispatchTask(queueId, () -> this.exec(request, response, handler, createTime), false);
    }

    /**
     * 获取这个请求的串行队列ID
     *
     * @param handler 处理器
     * @param request 请求对象
     * @return 串行队列ID可能会为null
     */
    private Serializable getQueueId(HttpMethodWrapper handler, HttpServletRequest request) {
        // 没有设定队列ID，那就返回null，走非队列任务
        if (StringUtils.isEmpty(handler.getQueueIdKey())) {
            return null;
        }

        // 如果配置了队列ID，请求里没有，那就给个转化异常，让提示走传入参数问题.
        String value = request.getParameter(handler.getQueueIdKey());
        if (StringUtils.isEmpty(value)) {
            throw new ConvertException("HTTP request param error. uri=" + request.getUri() + "," + handler.getQueueIdKey() + " is required.");
        }

        // 如果有值，需要使用Handler里的参数修正类型，以确保拿到参数最终状态，不然这个调度队列就是错的，比如玩家IDLong类型与参数String
        for (HttpParamWrapper param : handler.getParameters()) {
            if (handler.getQueueIdKey().equals(param.getName())) {
                Converter<?> converter = this.getConverter(param);
                try {
                    Object result = converter.convert(param.getParameter(), value);
                    if (result instanceof Serializable) {
                        return (Serializable) result;
                    }
                    // 有队列ID，但类型异常，这个要算服务器内部错误了，定义有问题
                    throw new UnrealizedQueueIdException(request.getMethod(), request.getUri(), handler.getQueueIdKey());
                } catch (Exception e) {
                    // 出了异常，那就是参数转化出了问题，提交请求调用者.
                    throw new ConvertException("HTTP request param error. uri=" + request.getUri() + " >> " + handler.getQueueIdKey() + " >> " + value + "-->" + converter.buildErrorMsg(), e);
                }
            }
        }

        // 这里就是处理方法上没有配置队列ID参数
        throw new UnrealizedQueueIdException(request.getMethod(), request.getUri(), handler.getQueueIdKey());
    }

    private void exec(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler, long createTime) {
        // 这里已非Netty线程了
        final long startExecuteTime = System.nanoTime();
        try {
            this.doAction(request, response, handler);
        } catch (Throwable e) {
            this.processHandlerException(request, response, e);
        } finally {
            response.flush();

            // 延迟时间与执行时间
            if (handler.isPrintLog()) {
                this.handleExecAfter(request, startExecuteTime, createTime);
            }
        }
    }

    private void handleExecAfter(HttpServletRequest request, long startExecuteTime, long createTime) {
        String ip = request.getRemoteAddr();
        float delay = DateUtils.formatNanoTime(startExecuteTime - createTime);
        float exec = DateUtils.formatNanoTime(System.nanoTime() - startExecuteTime);
        logger.info("handle http({}),delay={} ms,exe={} ms,ip={}", request.getUri(), delay, exec, ip);
    }

    private void processHandlerException(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        // 404 Handler没找到...
        if (e instanceof NoHandlerFoundException) {
            this.noHandlerFound(request, response);
        }
        // API已过期...
        else if (e instanceof HandlerDeprecatedException) {
            this.handleDeprecated(request, response);
        }
        // 参数解析异常
        else if (e instanceof ConvertException) {
            this.handleConvertException(request, response, e);
        }
        // 都没有命中，那就提示服务器内部错误500
        else {
            this.handleServerException(request, response, e);
        }

        // 异常监控...
        ExceptionHelper.monitor(e);
    }

    private void doAction(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler) throws Exception {
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

    private Object[] analysisParam(HttpMethodWrapper handler, HttpServletRequest request) throws Exception {
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
                String data = this.readParameter(request, param);
                final Converter<?> converter = this.getConverter(param);
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

    private String readParameter(HttpServletRequest request, HttpParamWrapper param) throws Exception {
        // 标识为@RequestBody
        if (param.isRequestBody()) {
            try (InputStream inputStream = request.getInputStream()) {
                return StringUtils.readString(inputStream);
            }
        }
        // 请求头部参数
        else if (param.isRequestHeader()) {
            return request.getHeader(param.getName());
        }
        // 默认就从请求体中找参数
        else {
            return request.getParameter(param.getName());
        }
    }

    private Converter<?> getConverter(HttpParamWrapper param) {

        // 使用JSON转化器
        if (param.isUseJsonConvert()) {
            return new HttpParamJsonConverter();
        }

        // 默认方案
        final Parameter field = param.getParameter();
        Converter<?> result = ConvertManager.getInstance().getConverter(field.getType());
        if (result == null) {
            throw new UnrealizedException("未实现的注入(" + field.getType().getName() + ")" + field.getName());
        }
        return result;
    }

    /**
     * 500服务器内部错误.
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param e        异常信息
     */
    private void handleServerException(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        logger.warn("internal error for HTTP request with URI [{}] in DispatcherServlet. ip={}{}", request.getUri(), request.getRemoteAddr(), e);
        response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        response.writeObject(new HttpResult(HttpErrorCode.INTERNAL_ERROR, "request's API internal error."));
    }

    /**
     * API方法的参数解析异常情况.
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param e        异常信息
     */
    private void handleConvertException(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        logger.warn("parameters invalid for HTTP request with URI [{}] in DispatcherServlet. ip={}{}", request.getUri(), request.getRemoteAddr(), e);
        response.setStatus(HttpResponseStatus.BAD_REQUEST.code());
        response.writeObject(new HttpResult(HttpErrorCode.PARAMETERS_INVALID, "request's API parameters invalid."));
    }

    /**
     * API方法已过期.
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    private void handleDeprecated(HttpServletRequest request, HttpServletResponse response) {
        logger.warn("deprecated for HTTP request with URI [{}] in DispatcherServlet. ip={}", request.getUri(), request.getRemoteAddr());
        response.setStatus(HttpResponseStatus.LOCKED.code());
        response.writeObject(new HttpResult(HttpErrorCode.API_DEPRECATED, "request's API Deprecated."));
    }

    /**
     * 404的默认处理方案.
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    private void noHandlerFound(HttpServletRequest request, HttpServletResponse response) {
        logger.warn("No mapping found for HTTP request with URI [{}] in DispatcherServlet. ip={}", request.getUri(), request.getRemoteAddr());
        response.setStatus(HttpResponseStatus.NOT_FOUND.code());
        response.writeObject(new HttpResult(HttpErrorCode.NO_API, "request's API Unrealized."));
    }
}
