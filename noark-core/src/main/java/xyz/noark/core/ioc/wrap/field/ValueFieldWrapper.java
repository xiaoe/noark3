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
package xyz.noark.core.ioc.wrap.field;

import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.util.FieldUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.log.Logger;
import xyz.noark.log.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * &#064;Value字段的包装类
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.8
 */
public class ValueFieldWrapper {
    private static final Logger logger = LoggerFactory.getLogger(ValueFieldWrapper.class);
    private final Class<?> beanClass;
    private final Field field;
    private final String key;
    private boolean autoRefreshed;
    private final Object owner;

    private final Converter<?> converter;
    /**
     * 初始化的那个默认值
     */
    private final Object defaultValue;

    public ValueFieldWrapper(Class<?> beanClass, Field field, String key, boolean autoRefreshed, Object owner) {
        this.beanClass = beanClass;
        this.field = field;
        this.key = key;
        this.autoRefreshed = autoRefreshed;
        this.owner = owner;

        this.converter = ConvertManager.getInstance().getConverter(field.getType());
        if (converter == null) {
            throw new UnrealizedException("类：" + beanClass.getName() + "中的属性：" + field.getName() + "类型未实现此转化器");
        }

        // 默认值需要克隆出来
        this.defaultValue = FieldUtils.readField(owner, field);
    }

    public boolean isAutoRefreshed() {
        return autoRefreshed;
    }

    public void setAutoRefreshed(final boolean autoRefreshed) {
        logger.info("reset  @Value field auto refresh. beanClass={}, key={}, {} >> {}", beanClass.getName(), key, this.autoRefreshed, autoRefreshed);
        this.autoRefreshed = autoRefreshed;
    }

    public void injection() {
        String value = EnvConfigHolder.getString(key);
        if (StringUtils.isNotEmpty(value)) {
            FieldUtils.writeField(owner, field, this.convertValue(value));
        }
    }

    private Object convertValue(String value) {
        try {
            return converter.convert(field, value);
        } catch (Exception e) {
            throw new ConvertException(beanClass.getName() + " >> " + field.getName() + " >> " + value + "-->" + converter.buildErrorMsg(), e);
        }
    }

    public void refresh() {
        Object targetValue = defaultValue;

        // 有配置则不使用默认值
        String value = EnvConfigHolder.getString(key);
        if (StringUtils.isNotEmpty(value)) {
            targetValue = this.convertValue(value);
        }

        // 对比后赋值...
        this.compareAndSetValue(targetValue);
    }

    private void compareAndSetValue(Object targetValue) {
        Object nowValue = FieldUtils.readField(owner, field);
        if (!Objects.equals(nowValue, targetValue)) {
            FieldUtils.writeField(owner, field, targetValue);
            logger.info("refresh @Value field value. beanClass={}, key={}, {} >> {}", beanClass.getName(), key, nowValue, targetValue);
        }
    }
}

