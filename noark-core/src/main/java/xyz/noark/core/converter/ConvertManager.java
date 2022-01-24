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

import java.util.Collection;
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
        INSTANCE.register(ByteArrayConverter.class);
        INSTANCE.register(DateConverter.class);
        INSTANCE.register(DoubleConverter.class);
        INSTANCE.register(DoubleListConverter.class);
        INSTANCE.register(FileSizeConverter.class);
        INSTANCE.register(FloatConverter.class);
        INSTANCE.register(FloatListConverter.class);
        INSTANCE.register(IntegerConverter.class);
        INSTANCE.register(IntListConverter.class);
        INSTANCE.register(IntPairConverter.class);
        INSTANCE.register(IntRangeConverter.class);
        INSTANCE.register(IntSectionConverter.class);
        INSTANCE.register(LocalDateConverter.class);
        INSTANCE.register(LocalDateTimeConverter.class);
        INSTANCE.register(LocalTimeArrayConverter.class);
        INSTANCE.register(LocalTimeConverter.class);
        INSTANCE.register(LocalTimeSectionConverter.class);
        INSTANCE.register(LongConverter.class);
        INSTANCE.register(LongListConverter.class);
        INSTANCE.register(PointConverter.class);
        INSTANCE.register(PointListConverter.class);
        INSTANCE.register(StringConverter.class);
        INSTANCE.register(StringListConverter.class);
        INSTANCE.register(TimeRangeConverter.class);
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
     * @param single            转化器的实例
     */
    public void register(Class<?> klass, TemplateConverter templateConverter, Object single) {
        if (!(single instanceof Converter<?>)) {
            throw new ServerBootstrapException("非法的转化器." + klass.getName());
        }
        this.putConvert((Converter<?>) single, templateConverter);
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

    /**
     * 获取基础转化器实例.
     * <p>用于初始化时先把这个给初始化了，这是基础，没有这个IOC容器都没法初始化</p>
     *
     * @return 基础转化器实例
     */
    public Collection<Converter<?>> getAllBaseConverter() {
        return CONVERTERS.values();
    }
}