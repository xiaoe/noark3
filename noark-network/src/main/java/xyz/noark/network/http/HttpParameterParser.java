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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.util.CharsetUtils;
import xyz.noark.core.util.CollectionUtils;

/**
 * HTTP参数解析器.
 *
 * @since 3.4
 * @author 小流氓[176543888@qq.com]
 */
class HttpParameterParser {
	public static final String DEFAULT_FORMAT = "default";
	public static final String JSON_FORMAT = "json";

	private HttpParameterParser() {}

	public static Map<String, String> parse(FullHttpRequest fhr, QueryStringDecoder decoder, String parameterFormat) throws IOException {
		final HttpMethod method = fhr.method();
		// GET请求
		if (HttpMethod.GET == method) {
			return parseGetRequestParm(decoder);
		}
		// POST请求
		else if (HttpMethod.POST == method) {
			return parsePostRequestParm(fhr, decoder, parameterFormat);
		}
		// 不支持其它方法，有需求再实现
		else {
			throw new UnrealizedException("未实现的HTTP请求=" + method.name());
		}
	}

	private static Map<String, String> parsePostRequestParm(FullHttpRequest fhr, QueryStringDecoder decoder, String parameterFormat) throws IOException {
		// 是POST请求
		switch (parameterFormat) {
		// JSON类型的参数格式
		case JSON_FORMAT:
			return parseJsonParm(fhr);
		// 默认走标准HTTP的POST参数
		case DEFAULT_FORMAT:
		default:
			return parsePostParm(fhr);
		}
	}

	private static Map<String, String> parsePostParm(FullHttpRequest fhr) throws IOException {
		// 解析Post默认参数
		HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fhr);
		decoder.offer(fhr);
		List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
		Map<String, String> parmMap = new HashMap<>(parmList.size());
		for (InterfaceHttpData parm : parmList) {
			Attribute data = (Attribute) parm;
			parmMap.put(data.getName(), data.getValue());
		}
		return parmMap;
	}

	private static Map<String, String> parseJsonParm(FullHttpRequest fhr) {
		final ByteBuf buf = fhr.content();
		byte[] bs = new byte[buf.readableBytes()];
		buf.readBytes(bs);
		return JSON.parseObject(new String(bs, CharsetUtils.CHARSET_UTF_8), new TypeReference<Map<String, String>>() {});
	}

	private static Map<String, String> parseGetRequestParm(QueryStringDecoder decoder) {
		// 是GET请求，只取第一个参数，有需求再优化
		Map<String, String> parmMap = new HashMap<>(decoder.parameters().size());
		for (Map.Entry<String, List<String>> e : decoder.parameters().entrySet()) {
			if (CollectionUtils.isNotEmpty(e.getValue())) {
				parmMap.put(e.getKey(), e.getValue().get(0));
			}
		}
		return parmMap;
	}
}