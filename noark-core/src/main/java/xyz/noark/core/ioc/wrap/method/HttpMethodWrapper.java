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
package xyz.noark.core.ioc.wrap.method;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.controller.RequestParam;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.definition.method.HttpMethodDefinition;
import xyz.noark.core.ioc.wrap.param.HttpParamWrapper;
import xyz.noark.reflectasm.MethodAccess;

/**
 * 封包处理方法包装类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class HttpMethodWrapper extends AbstractControllerMethodWrapper {
	private final String uri;
	private final ArrayList<HttpParamWrapper> parameters = new ArrayList<>();
	/** 当前方法是否已废弃使用. */
	private boolean deprecated = false;

	public HttpMethodWrapper(MethodAccess methodAccess, Object single, HttpMethodDefinition method, Controller controller) {
		super(methodAccess, single, method.getMethodIndex(), controller, method.getOrder());
		this.uri = method.uri();
		this.deprecated = method.isDeprecated();

		Arrays.stream(method.getParameters()).forEach(v -> buildParamWrapper(v));
	}

	private void buildParamWrapper(Parameter parameter) {
		RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
		if (requestParam == null) {
			throw new UnrealizedException("HTTP接口参数没有未标识@RequestParam. uri=" + uri);
		}
		this.parameters.add(new HttpParamWrapper(requestParam, parameter));
	}

	public String getUri() {
		return uri;
	}

	public ArrayList<HttpParamWrapper> getParameters() {
		return parameters;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public String logCode() {
		// TODO 所有Logcode可以提前搞定
		return "http(" + uri + ")";
	}
}