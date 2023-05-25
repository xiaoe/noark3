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

import com.alibaba.fastjson.JSON;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.util.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * HTTP参数JSON转化器.
 *
 * @author 小流氓[176543888@qq.com]
 */
public class HttpParamJsonConverter implements Converter<Object> {
    @Override
    public Object convert(Field field, String value) throws Exception {
        return this.convert(field.getType(), value);
    }

    @Override
    public Object convert(Parameter parameter, String value) throws Exception {
        return this.convert(parameter.getType(), value);
    }

    @Override
    public Object convert(Field field, Map<String, String> data) throws Exception {
        throw new UnrealizedException("JsonConverter Unrealized");
    }

    @Override
    public String buildErrorMsg() {
        return "不是一个JSON格式";
    }

    private Object convert(Class<?> klass, String value) {
        if (klass.isAssignableFrom(List.class)) {
            return JSON.parseArray(value, FieldUtils.getGenericClass(klass, 0));
        }

        return JSON.parseObject(value, klass);
    }
}
