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
package xyz.noark.core.ioc.definition.method;

import xyz.noark.core.annotation.controller.*;
import xyz.noark.reflectasm.MethodAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * HTTP请求方法的定义.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class HttpMethodDefinition extends SimpleMethodDefinition {
    private final String path;
    private final Set<RequestMethod> methodSet;
    private final String queueId;

    private final Parameter[] parameters;
    private final ResponseBody responseBody;
    private final boolean publicApi;
    private final boolean privateApi;

    public HttpMethodDefinition(MethodAccess methodAccess, Method method, RequestMapping requestMapping) {
        this(methodAccess, method, requestMapping.path(), requestMapping.method(), requestMapping.queueId());
    }

    public HttpMethodDefinition(MethodAccess methodAccess, Method method, GetMapping mapping) {
        this(methodAccess, method, mapping.path(), new RequestMethod[]{RequestMethod.GET}, mapping.queueId());
    }

    public HttpMethodDefinition(MethodAccess methodAccess, Method method, PostMapping mapping) {
        this(methodAccess, method, mapping.path(), new RequestMethod[]{RequestMethod.POST}, mapping.queueId());
    }

    private HttpMethodDefinition(MethodAccess methodAccess, Method method, String path, RequestMethod[] methods, String queueId) {
        super(methodAccess, method);

        this.path = path;
        this.methodSet = new HashSet<>(Arrays.asList(methods));
        this.queueId = queueId;

        this.parameters = method.getParameters();
        this.responseBody = method.getAnnotation(ResponseBody.class);
        this.publicApi = method.isAnnotationPresent(PublicApi.class);
        this.privateApi = method.isAnnotationPresent(PrivateApi.class);
    }

    @Override
    public Parameter[] getParameters() {
        return parameters;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public boolean isPublicApi() {
        return publicApi;
    }

    public boolean isPrivateApi() {
        return privateApi;
    }

    public String getPath() {
        return path;
    }

    public Set<RequestMethod> getMethodSet() {
        return methodSet;
    }

    public String getQueueId() {
        return queueId;
    }
}