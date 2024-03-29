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
package xyz.noark.core.ioc.wrap.param;

import xyz.noark.core.annotation.controller.RequestBody;
import xyz.noark.core.annotation.controller.RequestHeader;
import xyz.noark.core.annotation.controller.RequestParam;
import xyz.noark.core.annotation.orm.Json;
import xyz.noark.core.util.StringUtils;

import java.lang.reflect.Parameter;

/**
 * HTTP接口参数包装类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class HttpParamWrapper {
    private final Parameter parameter;

    private final String name;
    private final boolean required;
    private final String defaultValue;

    private boolean requestHeader = false;
    private boolean requestBody = false;

    /**
     * 使用JSON转化器
     */
    private final boolean useJsonConvert;

    public HttpParamWrapper(Parameter parameter) {
        this.parameter = parameter;
        this.useJsonConvert = parameter.isAnnotationPresent(Json.class);

        // 标识为@RequestParam
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            this.name = requestParam.name();
            this.required = requestParam.required();
            this.defaultValue = requestParam.defaultValue();
        }
        // 标识为@RequestHeader
        else if (parameter.isAnnotationPresent(RequestHeader.class)) {
            this.requestHeader = true;
            RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
            this.name = requestHeader.name();
            this.required = requestHeader.required();
            this.defaultValue = requestHeader.defaultValue();
        }
        // 标识为@RequestBody
        else if (parameter.isAnnotationPresent(RequestBody.class)) {
            this.requestBody = true;
            this.name = parameter.getName();
            this.defaultValue = StringUtils.EMPTY;
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            this.required = requestBody.required();
        }
        // 默认给个规则解析
        else {
            this.required = false;
            this.name = parameter.getName();
            this.defaultValue = StringUtils.EMPTY;
        }
    }

    public Parameter getParameter() {
        return parameter;
    }

    public boolean isRequired() {
        return required;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isRequestHeader() {
        return requestHeader;
    }

    public boolean isRequestBody() {
        return requestBody;
    }

    public boolean isUseJsonConvert() {
        return useJsonConvert;
    }
}
