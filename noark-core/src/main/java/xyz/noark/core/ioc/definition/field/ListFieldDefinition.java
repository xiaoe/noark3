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
package xyz.noark.core.ioc.definition.field;

import xyz.noark.core.ioc.IocMaking;
import xyz.noark.core.ioc.definition.DefaultBeanDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * List类型的注入.
 * <p>
 * 所有实现此接口或继承此类的都算.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ListFieldDefinition extends DefaultFieldDefinition {

    public ListFieldDefinition(Field field, boolean required) {
        super(field, (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], required);
    }

    @Override
    protected Object extractInjectionObject(IocMaking making, Class<?> klass, Field field) {
        return making.findAllImpl(fieldClass).stream().sorted(Comparator.comparingInt(DefaultBeanDefinition::getOrder)).map(DefaultBeanDefinition::getSingle).collect(Collectors.toList());
    }
}