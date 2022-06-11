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
package xyz.noark.core.ioc.definition;

import xyz.noark.core.annotation.Primary;
import xyz.noark.core.annotation.configuration.Bean;
import xyz.noark.core.ioc.IocMaking;
import xyz.noark.core.ioc.definition.method.BeanMethodDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 启动配置Bean的定义.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ConfigurationBeanDefinition extends DefaultBeanDefinition {

    private final List<BeanMethodDefinition> beans;

    public ConfigurationBeanDefinition(String profileStr, Class<?> klass) {
        super(profileStr, klass);
        this.beans = new ArrayList<>();
    }

    @Override
    public void injection(IocMaking making) {
        super.injection(making);

        // 注入完属性，还要建构相关Bean.
        for (BeanMethodDefinition bean : beans) {
            boolean primary = bean.getMethod().isAnnotationPresent(Primary.class);
            Object obj = bean.getMethodAccess().invoke(single, bean.getMethodIndex());
            DefaultBeanDefinition beanDefinition = new DefaultBeanDefinition(profileStr, bean.getBeanName(), obj, primary).init();
            making.getLoader().getBeans().put(beanDefinition.getBeanClass(), beanDefinition);
        }
    }

    @Override
    protected void analysisMethodByAnnotation(Class<? extends Annotation> annotationType, Annotation annotation, Method method) {
        // 配置类中，只关心@Bean的注解方法，其他都忽略掉吧，没有什么意义...
        if (annotationType == Bean.class) {
            beans.add(new BeanMethodDefinition(methodAccess, method, (Bean) annotation));
        } else {
            super.analysisMethodByAnnotation(annotationType, annotation, method);
        }
    }
}