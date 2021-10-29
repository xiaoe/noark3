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
package xyz.noark.core.ioc.wrap.method;

import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.annotation.controller.RequestMethod;
import xyz.noark.core.annotation.controller.RequestParam;
import xyz.noark.core.annotation.controller.ResponseBody;
import xyz.noark.core.ioc.definition.method.HttpMethodDefinition;
import xyz.noark.core.ioc.wrap.param.HttpParamWrapper;
import xyz.noark.core.network.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * 封包处理方法包装类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class HttpMethodWrapper extends AbstractControllerMethodWrapper implements HandlerMethod {
    private final String path;
    private final Method method;
    private final Set<RequestMethod> methodSet;
    private final ArrayList<HttpParamWrapper> parameters = new ArrayList<>();
    private final ResponseBody responseBody;

    public HttpMethodWrapper(Object single, HttpMethodDefinition method, ExecThreadGroup threadGroup, Class<?> controllerMasterClass) {
        super(single, threadGroup, controllerMasterClass.getName(), "handle(" + method.getPath() + ")", method);
        this.path = method.getPath();
        // 这里的方法缓存着，拦截器里可能会有获取注解的需求
        this.method = method.getMethod();
        this.printLog = method.isPrintLog();
        this.queueIdKey = method.getQueueId();
        this.methodSet = method.getMethodSet();
        this.deprecated = method.isDeprecated();
        this.responseBody = method.getResponseBody();

        Arrays.stream(method.getParameters()).forEach(this::buildParamWrapper);
    }

    private void buildParamWrapper(Parameter parameter) {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        this.parameters.add(new HttpParamWrapper(requestParam, parameter));
    }

    public String getPath() {
        return path;
    }

    public ArrayList<HttpParamWrapper> getParameters() {
        return parameters;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public Set<RequestMethod> getMethodSet() {
        return methodSet;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }
}