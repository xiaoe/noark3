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
package xyz.noark.core.converter.impl;

import xyz.noark.core.annotation.DateTimeFormat;
import xyz.noark.core.annotation.TemplateConverter;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.ConvertException;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * LocalDateTime类型转化器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@TemplateConverter(LocalDateTime.class)
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public String buildErrorMsg() {
        return "不是一个LocalDateTime类型的字符串";
    }

    @Override
    public LocalDateTime convert(Field field, String value) throws Exception {
        return this.convert(field.getAnnotation(DateTimeFormat.class), value);
    }

    @Override
    public LocalDateTime convert(Parameter parameter, String value) throws Exception {
        if (value == null) {
            return null;
        }
        return this.convert(parameter.getAnnotation(DateTimeFormat.class), value);
    }

    private LocalDateTime convert(DateTimeFormat format, String value) throws ParseException {
        if (format == null) {
            return LocalDateTime.parse(value);
        }
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format.pattern()));
    }

    @Override
    public LocalDateTime convert(Field field, Map<String, String> data) throws Exception {
        throw new ConvertException("LocalDateTime无法转化Map类型的配置...");
    }
}