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
package xyz.noark.core.ioc;

import xyz.noark.core.annotation.*;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.ioc.definition.ConfigurationBeanDefinition;
import xyz.noark.core.ioc.definition.ControllerBeanDefinition;
import xyz.noark.core.ioc.definition.DefaultBeanDefinition;
import xyz.noark.core.ioc.definition.StaticComponentBeanDefinition;
import xyz.noark.core.ioc.scan.Resource;
import xyz.noark.core.ioc.scan.ResourceScanning;
import xyz.noark.core.util.AnnotationUtils;
import xyz.noark.core.util.ClassUtils;
import xyz.noark.core.util.MapUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 加载IOC容器接管的Bean资源.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class IocLoader {
    private static final String PACKAGE_INFO_CLASS = "package-info.class";
    private static final String CLASS_SUFFIX = ".class";

    private final HashMap<Class<?>, DefaultBeanDefinition> beans = MapUtils.newHashMap(1024);
    private final List<BeanDefinition> configurations = new ArrayList<>();
    private final List<StaticComponentBeanDefinition> staticComponents = new ArrayList<>();

    IocLoader(String... packages) {
        ResourceScanning.scanPackage(packages, this::analysisResource);
    }

    /**
     * 查找所有实现类.
     *
     * @param klass 接口
     * @return 实现类集合
     */
    protected List<DefaultBeanDefinition> findImpl(final Class<?> klass) {
        return beans.values().stream().filter(v -> klass.isInstance(v.getSingle())).collect(Collectors.toList());
    }

    private void analysisResource(Resource resource) {
        String resourceName = resource.getName();

        // 忽略 package-info.class
        if (PACKAGE_INFO_CLASS.equals(resourceName)) {
            return;
        }

        // 忽略非Class文件
        if (!resourceName.endsWith(CLASS_SUFFIX)) {
            return;
        }

        // Class快速载入
        analysisClass(ClassUtils.loadClass(resourceName.substring(0, resourceName.length() - 6).replaceAll("[/\\\\]", ".")));
    }

    /**
     * 分析Class
     *
     * @param klass Class
     */
    private void analysisClass(Class<?> klass) {
        // 接口、内部类、枚举、注解和匿名类 直接忽略
        if (klass.isInterface() || klass.isMemberClass() || klass.isEnum() || klass.isAnnotation() || klass.isAnonymousClass()) {
            return;
        }

        // 抽象类和非Public的也忽略
        int modify = klass.getModifiers();
        if (Modifier.isAbstract(modify) || (!Modifier.isPublic(modify))) {
            return;
        }

        // 查找带有@Component注解或其他注解上有@Component注解的类
        Annotation annotation = AnnotationUtils.getAnnotation(klass, Component.class);
        if (annotation == null) {
            return;
        }

        // 目标类上实际注解类型
        Class<? extends Annotation> annotationType = annotation.annotationType();
        // 配置类
        if (annotationType == Configuration.class) {
            configurations.add(new ConfigurationBeanDefinition(klass).init());
        }
        // 协议入口控制类
        else if (annotationType == Controller.class) {
            analytical(klass, (Controller) annotation);
        }
        // 协议入口控制类(模块化)
        else if (annotationType == ModuleController.class) {
            analytical(klass, (ModuleController) annotation);
        }
        // 静态组件
        else if (annotationType == StaticComponent.class) {
            staticComponents.add(new StaticComponentBeanDefinition(klass).init());
        }
        // 不是已定义的，那就扫描这个注解上有没有@Component
        else {
            DefaultBeanDefinition definition = new DefaultBeanDefinition(klass, annotation, annotationType).init();
            beans.put(klass, definition);
            // 模板转化器.
            if (annotationType == TemplateConverter.class) {
                ConvertManager.getInstance().register(klass, (TemplateConverter) annotation, definition.getSingle());
            }
        }
    }

    /**
     * 协议入口控制类(模块化)
     */
    private void analytical(Class<?> klass, ModuleController controller) {
        beans.put(klass, new ControllerBeanDefinition(klass, controller).init());
    }

    private void analytical(Class<?> klass, Controller controller) {
        beans.put(klass, new ControllerBeanDefinition(klass, controller).init());
    }

    public HashMap<Class<?>, DefaultBeanDefinition> getBeans() {
        return beans;
    }

    public List<BeanDefinition> getConfigurations() {
        return configurations;
    }

    public List<StaticComponentBeanDefinition> getStaticComponents() {
        return staticComponents;
    }
}