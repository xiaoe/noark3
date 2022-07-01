/*
 * Copyright © 2018 huiyunetwork.com All Rights Reserved.
 *
 * 感谢您加入辉娱网络，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 *
 * 		http://www.huiyunetwork.com/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归辉娱网络所有，我们将保留所有权利;
 * 4、凡侵犯辉娱网络相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.converter.list;

import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.util.FieldUtils;

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
     * @param value 字符串
     * @return 返回列表中每一项的数组
     */
    private String[] splitValue(String value) {
        return value.split(",");
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
        String[] array = this.splitValue(value);
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
        String[] array = this.splitValue(value);
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
