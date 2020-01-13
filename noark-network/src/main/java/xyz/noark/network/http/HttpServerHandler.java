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
package xyz.noark.network.http;

import static xyz.noark.log.LogHelper.logger;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.ReferenceCountUtil;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.manager.HttpMethodManager;
import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;
import xyz.noark.core.ioc.wrap.param.HttpParamWrapper;
import xyz.noark.core.util.CharsetUtils;
import xyz.noark.core.util.IpUtils;
import xyz.noark.core.util.Md5Utils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.network.http.HttpServletRequest.NoarkHttpServletRequest;

/**
 * HTTP封包处理逻辑类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
	/** 签名Key... */
	private static final String SIGN = "sign";
	/** 时间戳Key... */
	private static final String TIME = "time";

	private final String secretKey;
	private final String parameterFormat;

	public HttpServerHandler(String secretKey, String parameterFormat) {
		this.secretKey = secretKey;
		this.parameterFormat = parameterFormat;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try {
			if (msg instanceof FullHttpRequest) {
				this.handleFullHttpRequest(ctx, (FullHttpRequest) msg);
			}
		} finally {
			// HttpRequestDecoder过来的msg是需要手工释放的...
			// http://netty.io/wiki/reference-counted-objects.html
			ReferenceCountUtil.release(msg);
		}
	}

	private void handleFullHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
		final Object result = this.exec(ctx, request);
		ByteBuf buf = Unpooled.wrappedBuffer((result instanceof byte[]) ? (byte[]) result : JSON.toJSONString(result).getBytes(CharsetUtils.CHARSET_UTF_8));
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
		ctx.write(response).addListener(ChannelFutureListener.CLOSE);
	}

	private Object exec(ChannelHandlerContext ctx, FullHttpRequest fhr) {
		QueryStringDecoder decoder = new QueryStringDecoder(fhr.uri());
		final String uri = decoder.path();

		// 局域网判定
		final String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		if (!IpUtils.isInnerIp(ip)) {
			logger.warn("client request's not authorized. ip={}, uri={}", ip, uri);
			return new HttpResult(HttpErrorCode.NOT_AUTHORIZED, "client request's not authorized.");
		}

		final long createTime = System.nanoTime();
		HttpMethodWrapper handler = HttpMethodManager.getInstance().getHttpHandler(uri);

		// API不存在...
		if (handler == null) {
			logger.warn("client request's API Unrealized. ip={}, uri={}", ip, uri);
			return new HttpResult(HttpErrorCode.NO_API, "client request's API Unrealized.");
		}

		// 已废弃
		if (handler.isDeprecated()) {
			logger.warn("client request's API Deprecated. ip={}, uri={}", ip, uri);
			return new HttpResult(HttpErrorCode.API_DEPRECATED, "client request's API Deprecated.");
		}

		// 解析Http参数...
		Map<String, String> parameters = Collections.emptyMap();
		try {
			parameters = HttpParameterParser.parse(fhr, decoder, parameterFormat);
		} catch (Exception e) {
			logger.warn("client request's parameters not json. ip={}, uri={}, e={}", ip, uri, e);
			return new HttpResult(HttpErrorCode.PARAMETERS_INVALID, "client request's parameters not json.");
		}

		// 验证签名，如果未配置密钥，将忽略对签名的验证...
		if (secretKey != null && !checkSign(parameters.getOrDefault(TIME, StringUtils.EMPTY), parameters.get(SIGN))) {
			logger.warn("client request's sign failed. ip={}, uri={}", ip, uri);
			return new HttpResult(HttpErrorCode.SIGN_FAILED, "client request's sign failed.");
		}

		// 参数解析...
		Object[] args = null;
		try {
			args = this.analysisParam(handler, uri, parameters);
		} catch (Exception e) {
			logger.warn("client request's parameters are invalid, ip={}, uri={}, e={}", ip, uri, e);
			return new HttpResult(HttpErrorCode.PARAMETERS_INVALID, "client request's parameters are invalid, " + e.getMessage());
		}

		// 逻辑执行...
		final long startExecuteTime = System.nanoTime();
		try {
			Object returnValue = null;
			if (args == null) {
				returnValue = handler.invoke();
			} else {
				returnValue = handler.invoke(args);
			}

			// 如果返回值就是这个接口那就直接返回吧...
			if (returnValue instanceof HttpResult) {
				return (HttpResult) returnValue;
			}
			// 如果业务已处理成字节流了，那就直接返回
			else if (returnValue instanceof byte[]) {
				return returnValue;
			}

			HttpResult result = new HttpResult(HttpErrorCode.OK);
			result.setData(returnValue);
			return result;
		} catch (Exception e) {
			logger.warn("server internal error, ip={}, uri={}, e={}", ip, fhr.uri(), e);
			return new HttpResult(HttpErrorCode.INTERNAL_ERROR, "server internal error, " + e.getMessage());
		} finally {
			final long endExecuteTime = System.nanoTime();
			logger.info("handle {},delay={} ms,exe={} ms,ip={}", handler.logCode(), (startExecuteTime - createTime) / 100_0000F, (endExecuteTime - startExecuteTime) / 100_0000F, ip);
		}
	}

	public Object[] analysisParam(HttpMethodWrapper handler, String uri, Map<String, String> parameters) throws IOException {
		// 如果没有参数，返回null.
		if (handler.getParameters().isEmpty()) {
			return null;
		}

		HttpServletRequest request = null;

		List<Object> args = new ArrayList<>(handler.getParameters().size());
		for (HttpParamWrapper param : handler.getParameters()) {
			// Request请求参数
			if (HttpServletRequest.class.isAssignableFrom(param.getParameter().getType())) {
				args.add(request = this.build(request, uri, parameters));
			}
			// 其他转化器参数
			else {
				Converter<?> converter = this.getConverter(param.getParameter());
				String data = parameters.get(param.getName());

				if (data == null) {
					// 必选参数必需有值
					if (param.isRequired()) {
						throw new ConvertException("HTTP request param error. uri=" + uri + "," + param.getName() + " is required.");
					}
					// 不是必选参数，那就使用默认值来转化
					else {
						try {
							args.add(converter.convert(param.getParameter(), param.getDefaultValue()));
						} catch (Exception e) {
							throw new ConvertException("HTTP request default param error. uri=" + uri + "," + param.getName() + "=" + param.getDefaultValue() + "-->" + converter.buildErrorMsg(), e);
						}
					}
				} else {
					try {
						args.add(converter.convert(param.getParameter(), data));
					} catch (Exception e) {
						throw new ConvertException("HTTP request param error. uri=" + uri + "," + param.getName() + "=" + data + "-->" + converter.buildErrorMsg(), e);
					}
				}
			}
		}
		return args.toArray();
	}

	/**
	 * 构建Request对象.
	 * 
	 * @param request 请求对象
	 * @param uri URI
	 * @param parameters 所有请求参数
	 * @return Request对象
	 */
	private HttpServletRequest build(HttpServletRequest request, String uri, Map<String, String> parameters) {
		if (request != null) {
			return request;
		}
		return new NoarkHttpServletRequest(uri, parameters);
	}

	private Converter<?> getConverter(Parameter field) {
		Converter<?> result = ConvertManager.getInstance().getConverter(field.getType());
		if (result == null) {
			throw new UnrealizedException("未实现的注入(" + field.getType().getName() + ")" + field.getName());
		}
		return result;
	}

	/**
	 * 检测签名.
	 */
	private boolean checkSign(String time, String sign) {
		return Md5Utils.encrypt(new StringBuilder(time.length() + secretKey.length() + 1).append(secretKey).append("+").append(time).toString()).equalsIgnoreCase(sign);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.debug("HTTP异常. channel={}", ctx.channel(), cause);
		ctx.close();
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
}