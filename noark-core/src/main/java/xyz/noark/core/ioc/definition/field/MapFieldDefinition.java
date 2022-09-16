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

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.IocMaking;
import xyz.noark.core.ioc.definition.DefaultBeanDefinition;
import xyz.noark.core.util.FieldUtils;
import xyz.noark.core.util.MapUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Map类型的属性注入
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class MapFieldDefinition extends DefaultFieldDefinition {

    public MapFieldDefinition(Field field, boolean required) {
        super(field, (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1], required);
    }

    @Override
    protected Object extractInjectionObject(IocMaking making, Class<?> klass, Field field) {
        List<DefaultBeanDefinition> allImpl = making.findAllImpl(fieldClass);
        if (allImpl.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Serializable, Object> result = MapUtils.newHashMap(allImpl.size());
        final Map<Serializable, DefaultBeanDefinition> primaryMap = MapUtils.newHashMap(allImpl.size());

        Class<?> keyClass = FieldUtils.getMapKeyGenericClass(field);
        // Int类型的Key，使用ID
        if (Integer.class.equals(keyClass)) {
            allImpl.forEach(v -> Arrays.stream(v.getIds()).forEach(n -> result.put(n, selectPrimaryImpl(primaryMap, n, v).getSingle())));
        }
        // 其他类型使用Name
        else {
            allImpl.forEach(v -> Arrays.stream(v.getNames()).forEach(n -> result.put(n, selectPrimaryImpl(primaryMap, n, v).getSingle())));
        }
        return result;
    }

    protected DefaultBeanDefinition selectPrimaryImpl(Map<Serializable, DefaultBeanDefinition> primaryMap, Serializable key, DefaultBeanDefinition newImpl) {
        DefaultBeanDefinition oldImpl = primaryMap.get(key);
        if (oldImpl == null) {
            primaryMap.put(key, newImpl);
            return newImpl;
        }

        // 这个名称有两个实现，优先级一样，好无语
        if (oldImpl.isPrimary() == newImpl.isPrimary()) {
            throw new ServerBootstrapException("Class:" + field.getDeclaringClass().getName() + ">>Field:" + field.getName()
                    + " map key expected single matching bean but found 2. " +
                    "class1=" + oldImpl.getBeanClass().getName() + ", class2=" + newImpl.getBeanClass().getName());
        }

        // 新实现优先
        if (newImpl.isPrimary()) {
            primaryMap.put(key, newImpl);
            return newImpl;
        }

        // 老实现优先
        return oldImpl;
    }
}