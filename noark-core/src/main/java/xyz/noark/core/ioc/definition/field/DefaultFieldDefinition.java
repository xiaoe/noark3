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
import xyz.noark.core.ioc.FieldDefinition;
import xyz.noark.core.ioc.IocMaking;
import xyz.noark.core.ioc.definition.DefaultBeanDefinition;
import xyz.noark.core.util.FieldUtils;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 一个被IOC容器所管理的JavaBean定义描述类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class DefaultFieldDefinition implements FieldDefinition {
    /**
     * BeanOrder排序器
     */
    private static final Comparator<DefaultBeanDefinition> comparator = Comparator.comparingInt(DefaultBeanDefinition::getOrder);

    protected final Field field;
    protected final boolean required;
    protected final Class<?> fieldClass;

    public DefaultFieldDefinition(Field field, boolean required) {
        this(field, field.getType(), required);
    }

    protected DefaultFieldDefinition(Field field, Class<?> fieldClass, boolean required) {
        this.field = field;
        this.required = required;
        this.fieldClass = fieldClass;
        this.field.setAccessible(true);
    }

    @Override
    public void injection(Object single, IocMaking making) {
        FieldUtils.writeField(single, field, extractInjectionObject(making, field.getType(), field));
    }

    /**
     * 提取注入对象.
     *
     * @param making 装配对象
     * @param klass  宿主对象类
     * @param field  需要注入的属性
     * @return 需要注入的对象
     */
    protected Object extractInjectionObject(IocMaking making, Class<?> klass, Field field) {
        // 最佳注入的实现
        DefaultBeanDefinition target = null;

        // 所有实现
        List<DefaultBeanDefinition> implList = making.findAllImpl(klass);

        // 只有一个实现，那就是最佳
        if (implList.size() == 1) {
            target = implList.get(0);
        }
        // 有多个实现，找最优的，没有就安排序给一个
        else if (implList.size() > 1) {
            // 1. Primary+Sort
            target = findPrimaryAndSortImpl(implList);

            // 2. default + Sort
            if (target == null) {
                target = findDefaultAndSortImpl(implList);
            }

            // 3. ConditionalOnMissingBean + Sort
            if (target == null) {
                target = findMissingBeanAndSortImpl(implList);
            }
        }

        if (target == null) {
            // 如果是必选的注入，那要抛出一个异常
            if (required) {
                throw new ServerBootstrapException("Class:" + field.getDeclaringClass().getName() + ">>Field:" + field.getName() + " cannot autowired");
            }
            // 可以没有实现类
            else {
                return null;
            }
        }
        return target.getSingle();
    }


    /**
     * 查找带有@Primary和@Sort注解的实现
     *
     * @param implList 所有实现列表
     * @return 查找的最优的实现
     */
    private DefaultBeanDefinition findPrimaryAndSortImpl(List<DefaultBeanDefinition> implList) {
        return implList.stream().filter(DefaultBeanDefinition::isPrimary).min(comparator).orElse(null);
    }

    private DefaultBeanDefinition findDefaultAndSortImpl(List<DefaultBeanDefinition> implList) {
        return implList.stream().filter(v -> !v.isConditionalOnMissingBean()).min(comparator).orElse(null);
    }

    private DefaultBeanDefinition findMissingBeanAndSortImpl(List<DefaultBeanDefinition> implList) {
        return implList.stream().filter(DefaultBeanDefinition::isConditionalOnMissingBean).min(comparator).orElse(null);
    }

    /**
     * 查询指定实现中没有被标注@ConditionalOnMissingBean注解的实现
     *
     * @param implList 指定实现列表
     * @return 返回没有@ConditionalOnMissingBean注解的实现列表
     */
    protected Stream<DefaultBeanDefinition> findNotMissingBeanImpl(List<DefaultBeanDefinition> implList) {
        return implList.stream().filter(v -> !v.isConditionalOnMissingBean());
    }
}