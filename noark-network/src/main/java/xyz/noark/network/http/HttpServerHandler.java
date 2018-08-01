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

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static xyz.noark.log.LogHelper.logger;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.AsciiString;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.manager.HttpMethodManager;
import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;
import xyz.noark.core.ioc.wrap.param.HttpParamWrapper;
import xyz.noark.core.util.Md5Utils;
import xyz.noark.core.util.StringUtils;

/**
 * HTTP封包处理逻辑类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
	private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
	private static final AsciiString CONNECTION = new AsciiString("Connection");
	private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");
	private static final String SIGN = "sign";// 签名Key...
	private static final String TIME = "time";// 时间戳Key...

	private final String secretKey;
	private HttpRequest request;// 缓存一下请求...

	public HttpServerHandler(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;
			logger.info("http request. uri={}", request.uri());
			if (HttpUtil.is100ContinueExpected(request)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}
		} else if (msg instanceof HttpContent) {
			HttpResult result = this.exec(request, (HttpContent) msg);

			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(JSON.toJSONString(result).getBytes(DEFAULT_CHARSET)));
			response.headers().set(CONTENT_TYPE, "text/plain");
			response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

			if (HttpUtil.isKeepAlive(request)) {
				response.headers().set(CONNECTION, KEEP_ALIVE);
				ctx.write(response);
			} else {
				ctx.write(response).addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

	private HttpResult exec(HttpRequest req, HttpContent content) {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri(), DEFAULT_CHARSET);
		HttpMethodWrapper handler = HttpMethodManager.getInstance().getHttpHandler(queryStringDecoder.path());

		// API不存在...
		if (handler == null) {
			return new HttpResult(HttpErrorCode.NO_API, "client request's API Unrealized.");
		}

		// 已废弃
		if (handler.isDeprecated()) {
			return new HttpResult(HttpErrorCode.API_DEPRECATED, "client request's API Deprecated.");
		}

		// 签名失败...
		if (!checkSign(queryStringDecoder)) {
			return new HttpResult(HttpErrorCode.SIGN_FAILED, "client request's sign failed.");
		}

		// 参数解析...
		Object[] args = null;
		try {
			args = this.analysisParam(handler, req, content);
		} catch (Exception e) {
			return new HttpResult(HttpErrorCode.PARAMETERS_INVALID, "client request's parameters are invalid, " + e.getMessage());
		}

		// 逻辑执行...
		try {
			Object returnValue = null;
			if (args == null) {
				returnValue = handler.invoke();
			} else {
				returnValue = handler.invoke(args);
			}

			HttpResult result = new HttpResult(HttpErrorCode.OK);
			result.setData(returnValue);
			return result;
		} catch (Exception e) {
			return new HttpResult(HttpErrorCode.INTERNAL_ERROR, "server internal error, " + e.getMessage());
		}
	}

	public Object[] analysisParam(HttpMethodWrapper handler, HttpRequest request, HttpContent content) throws IOException {
		if (handler.getParameters().isEmpty()) {// 如果没有参数，返回null.
			return null;
		}

		List<Object> args = new ArrayList<>(handler.getParameters().size());
		HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
		decoder.offer(content);

		for (HttpParamWrapper param : handler.getParameters()) {
			Attribute data = (Attribute) decoder.getBodyHttpData(param.getName());
			Converter<?> converter = this.getConverter(param.getParameter());

			if (param.getRequestParam().required() || data != null) {
				try {
					args.add(converter.convert(data.getValue()));
				} catch (Exception e) {
					throw new ConvertException("HTTP request param error. uri=" + request.uri() + "," + param.getName() + "=" + data.getValue() + "-->" + converter.buildErrorMsg(), e);
				}
			} else {
				try {
					args.add(converter.convert(param.getRequestParam().defaultValue()));
				} catch (Exception e) {
					throw new ConvertException("HTTP request default param error. uri=" + request.uri() + "," + param.getName() + "=" + param.getRequestParam().defaultValue() + "-->" + converter.buildErrorMsg(), e);
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

	/**
	 * 检测签名.
	 */
	public boolean checkSign(QueryStringDecoder decoder) {
		// 如果未配置密钥，则不对签名认证...
		if (StringUtils.isEmpty(secretKey)) {
			return true;
		}

		Map<String, List<String>> parameters = decoder.parameters();
		List<String> times = parameters.getOrDefault(TIME, Collections.emptyList());
		if (times.isEmpty()) {
			return false;
		}
		List<String> signs = parameters.getOrDefault(SIGN, Collections.emptyList());
		if (signs.isEmpty()) {
			return false;
		}
		String time = times.get(0);
		String sign = signs.get(0);
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