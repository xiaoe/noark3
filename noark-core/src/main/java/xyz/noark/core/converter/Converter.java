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
package xyz.noark.core.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 转化接口.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public interface Converter<T> {

    /**
     * 将一个字符串转化成目标对象
     *
     * @param field 类的属性
     * @param value 字符串
     * @return 目标对象
     * @throws Exception 转化字符串时可能出现不可知异常情况
     */
    public T convert(Field field, String value) throws Exception;

    /**
     * 将一个字符串转化成目标对象
     *
     * @param parameter 方法的参数
     * @param value     字符串
     * @return 目标对象
     * @throws Exception 转化字符串时可能出现不可知异常情况
     */
    public T convert(Parameter parameter, String value) throws Exception;

    /**
     * 将一组字符串转化为目标对象
     *
     * @param field 类的属性
     * @param data  一组字符串
     * @return 目标对象
     * @throws Exception 转化字符串时可能出现不可知异常情况
     */
    public T convert(Field field, Map<String, String> data) throws Exception;

    /**
     * 构建错误提示.
     *
     * @return 错误提示
     */
    public String buildErrorMsg();
}