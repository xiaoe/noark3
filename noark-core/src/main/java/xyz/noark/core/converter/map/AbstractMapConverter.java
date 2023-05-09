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
package xyz.noark.core.converter.map;

import xyz.noark.core.annotation.tpl.TplAttrDelimiter;
import xyz.noark.core.converter.AbstractArrayConverter;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.util.FieldUtils;
import xyz.noark.core.util.MapUtils;
import xyz.noark.core.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;

/**
 * 抽象的Map转化器
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.8
 */
public abstract class AbstractMapConverter extends AbstractArrayConverter {
    protected String buildErrorMsg() {
        return "Map结构. 例=key1:value1,key2:value2,key3:value3";
    }

    public Map<Object, Object> convert(Field field, String value) throws Exception {
        Converter<?> keyGenericConverter = this.getMapKeyGenericConverter(field);
        Converter<?> valueGenericConverter = this.getMapValueGenericConverter(field);

        TplAttrDelimiter delimiter = field.getAnnotation(TplAttrDelimiter.class);
        String[] array = this.splitArray(delimiter, value);

        Map<Object, Object> result = this.createMap(array.length);
        for (String s : array) {
            // 一个英文冒号(有需要日后再增加一个注解修正)
            String[] split = s.split(StringUtils.COLON, 2);
            Object keyObject = keyGenericConverter.convert(field, split[0]);
            Object valueObject = valueGenericConverter.convert(field, split[1]);
            result.put(keyObject, valueObject);
        }
        return result;
    }

    public Map<Object, Object> convert(Parameter parameter, String value) throws Exception {
        Converter<?> keyGenericConverter = this.getMapKeyGenericConverter(parameter);
        Converter<?> valueGenericConverter = this.getMapValueGenericConverter(parameter);

        TplAttrDelimiter delimiter = parameter.getAnnotation(TplAttrDelimiter.class);
        String[] array = this.splitArray(delimiter, value);

        Map<Object, Object> result = this.createMap(array.length);
        for (String s : array) {
            // 一个英文冒号(有需要日后再增加一个注解修正)
            String[] split = s.split(StringUtils.COLON, 2);
            Object keyObject = keyGenericConverter.convert(parameter, split[0]);
            Object valueObject = valueGenericConverter.convert(parameter, split[1]);
            result.put(keyObject, valueObject);
        }
        return result;
    }

    public Map<Object, Object> convert(Field field, Map<String, String> data) throws Exception {
        if (MapUtils.isEmpty(data)) {
            return Collections.emptyMap();
        }

        Converter<?> keyGenericConverter = this.getMapKeyGenericConverter(field);
        Converter<?> valueGenericConverter = this.getMapValueGenericConverter(field);

        TplAttrDelimiter delimiter = field.getAnnotation(TplAttrDelimiter.class);

        Map<Object, Object> result = this.createMap(32);
        for (String value : data.values()) {
            String[] array = this.splitArray(delimiter, value);
            for (String s : array) {
                // 一个英文冒号(有需要日后再增加一个注解修正)
                String[] split = s.split(StringUtils.COLON, 2);
                Object keyObject = keyGenericConverter.convert(field, split[0]);
                Object valueObject = valueGenericConverter.convert(field, split[1]);
                result.put(keyObject, valueObject);
            }
        }
        return result;
    }

    private Converter<?> getMapValueGenericConverter(Parameter parameter) {
        Class<?> genericClass = FieldUtils.getGenericClass(parameter.getParameterizedType(), 1);
        Converter<?> converter = ConvertManager.getInstance().getConverter(genericClass);
        if (converter == null) {
            throw new UnrealizedException("Map转换器Value未实现的类型. field=(" + parameter.getType().getName() + ")" + parameter.getName());
        }
        return converter;
    }

    private Converter<?> getMapKeyGenericConverter(Parameter parameter) {
        Class<?> genericClass = FieldUtils.getGenericClass(parameter.getParameterizedType(), 0);
        Converter<?> converter = ConvertManager.getInstance().getConverter(genericClass);
        if (converter == null) {
            throw new UnrealizedException("Map转换器Key未实现的类型. field=(" + parameter.getType().getName() + ")" + parameter.getName());
        }
        return converter;
    }

    private Converter<?> getMapValueGenericConverter(Field field) {
        Class<?> keyGenericClass = FieldUtils.getMapValueGenericClass(field);
        Converter<?> converter = ConvertManager.getInstance().getConverter(keyGenericClass);
        if (converter == null) {
            throw new UnrealizedException("Map转换器Value未实现的类型. field=(" + field.getType().getName() + ")" + field.getName());
        }
        return converter;
    }

    private Converter<?> getMapKeyGenericConverter(Field field) {
        Class<?> keyGenericClass = FieldUtils.getMapKeyGenericClass(field);
        Converter<?> converter = ConvertManager.getInstance().getConverter(keyGenericClass);
        if (converter == null) {
            throw new UnrealizedException("Map转换器Key未实现的类型. field=(" + field.getType().getName() + ")" + field.getName());
        }
        return converter;
    }

    protected abstract Map<Object, Object> createMap(int length);
}
