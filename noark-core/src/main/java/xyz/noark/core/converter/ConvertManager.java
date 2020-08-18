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
package xyz.noark.core.converter;

import xyz.noark.core.annotation.TemplateConverter;
import xyz.noark.core.converter.impl.*;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板转换实现管理类.
 * <p>
 * 内置一些系统级别的转化功能.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ConvertManager {
    private static final ConvertManager INSTANCE = new ConvertManager();
    private static final Map<Class<?>, Converter<?>> CONVERTERS = new HashMap<>(128);

    static {
        INSTANCE.register(BooleanConverter.class);
        INSTANCE.register(IntegerConverter.class);
        INSTANCE.register(LongConverter.class);
        INSTANCE.register(StringConverter.class);
        INSTANCE.register(FloatConverter.class);
        INSTANCE.register(IntListConverter.class);
        INSTANCE.register(FloatListConverter.class);
        // 使用了IOC功能，有自动扫描功能了，就不再需要手工添加...
    }

    private ConvertManager() {
    }

    public static ConvertManager getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> Converter<T> getConverter(Class<T> type) {
        return (Converter<T>) CONVERTERS.get(type);
    }

    /**
     * 注册一个模板转化实现类.
     *
     * @param klass             类型
     * @param templateConverter 转化器
     */
    public void register(Class<?> klass, TemplateConverter templateConverter) {
        Object object = ClassUtils.newInstance(klass);
        if (!(object instanceof Converter<?>)) {
            throw new ServerBootstrapException("非法的转化器." + klass.getName());
        }
        this.putConvert((Converter<?>) object, templateConverter);
    }

    /**
     * 系统内部使用,不判定注解和接口
     *
     * @param klass 转化类
     */
    public void register(Class<? extends Converter<?>> klass) {
        this.putConvert(ClassUtils.newInstance(klass), klass.getAnnotation(TemplateConverter.class));
    }

    private void putConvert(Converter<?> converter, TemplateConverter annotation) {
        for (Class<?> targetClass : annotation.value()) {
            CONVERTERS.put(targetClass, converter);
        }
    }
}