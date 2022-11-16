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
package xyz.noark.core.converter.list;

import xyz.noark.core.annotation.tpl.TplAttrDelimiter;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.util.FieldUtils;
import xyz.noark.core.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * 抽象的List转化器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.6
 */
public abstract class AbstractListConverter {

    /**
     * 创建一个List的实例对象
     *
     * @param length List的长度
     * @return 返回实例对象
     */
    protected abstract List<Object> createList(int length);

    /**
     * 字符串切割逻辑
     *
     * @param delimiter 分隔符注解
     * @param value     字符串
     * @return 返回列表中每一项的数组
     */
    private String[] splitValue(TplAttrDelimiter delimiter, String value) {
        // 默认List分隔符为英文的逗号
        if (delimiter == null) {
            return value.split(StringUtils.COMMA);
        }
        // 如果有注解指定则使用注解指定的分隔符
        return value.split(delimiter.value());
    }

    /**
     * 字符串转化为List列表.
     *
     * @param field 属性对象
     * @param value 字符串
     * @return 返回List列表
     * @throws Exception 转化字符串时可能出现不可知异常情况
     */
    protected List<Object> convert(Field field, String value) throws Exception {
        Converter<?> converter = this.getListGenericConverter(field);
        TplAttrDelimiter delimiter = field.getAnnotation(TplAttrDelimiter.class);
        String[] array = this.splitValue(delimiter, value);
        List<Object> result = this.createList(array.length);
        for (String s : array) {
            result.add(converter.convert(field, s));
        }
        return result;
    }

    /**
     * Map集合里的Value转化为List列表
     *
     * @param field 属性对象
     * @param data  Map集合
     * @return 返回List列表
     * @throws Exception 转化字符串时可能出现不可知异常情况
     */
    protected List<Object> convert(Field field, Map<String, String> data) throws Exception {
        if (data.isEmpty()) {
            return this.createList(0);
        }
        Converter<?> converter = this.getListGenericConverter(field);
        List<Object> result = this.createList(data.size());
        for (String value : data.values()) {
            result.add(converter.convert(field, value));
        }
        return result;
    }

    /**
     * 字符串转化为List列表.
     *
     * @param parameter 参数对象
     * @param value     字符串
     * @return 返回List列表
     * @throws Exception 转化字符串时可能出现不可知异常情况
     */
    protected List<Object> convert(Parameter parameter, String value) throws Exception {
        Converter<?> converter = this.getListGenericConverter(parameter);
        TplAttrDelimiter delimiter = parameter.getAnnotation(TplAttrDelimiter.class);
        String[] array = this.splitValue(delimiter, value);
        List<Object> result = this.createList(array.length);
        for (String s : array) {
            result.add(converter.convert(parameter, s));
        }
        return result;
    }

    /**
     * 获取List泛型转换器
     *
     * @param field List属性
     * @return List泛型转换器
     */
    private Converter<?> getListGenericConverter(Field field) {
        Class<?> genericClass = FieldUtils.getListGenericClass(field);
        Converter<?> converter = ConvertManager.getInstance().getConverter(genericClass);
        if (converter == null) {
            throw new UnrealizedException("转换器未实现的类型. field=(" + field.getType().getName() + ")" + field.getName());
        }
        return converter;
    }

    /**
     * 获取List泛型转换器
     *
     * @param parameter 参数对象
     * @return List泛型转换器
     */
    private Converter<?> getListGenericConverter(Parameter parameter) {
        Class<?> genericClass = FieldUtils.getGenericClass(parameter.getParameterizedType(), 0);
        Converter<?> converter = ConvertManager.getInstance().getConverter(genericClass);
        if (converter == null) {
            throw new UnrealizedException("转换器未实现的类型. field=(" + parameter.getType().getName() + ")" + parameter.getName());
        }
        return converter;
    }

    protected String buildErrorMsg() {
        return "List转化器是以英文逗号分隔噢. 例=struct,struct,struct";
    }
}
