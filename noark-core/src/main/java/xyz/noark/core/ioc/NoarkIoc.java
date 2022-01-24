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

import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.ioc.definition.DefaultBeanDefinition;
import xyz.noark.core.ioc.manager.EventMethodManager;
import xyz.noark.core.ioc.wrap.MethodWrapper;

import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static xyz.noark.log.LogHelper.logger;

/**
 * Noark-IOC的核心容器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class NoarkIoc implements Ioc {
    /**
     * 容器中所有托管的Bean对象.
     */
    private final ConcurrentHashMap<Class<?>, Object> singletons = new ConcurrentHashMap<>(512);

    private final ConcurrentHashMap<Class<? extends Annotation>, List<MethodWrapper>> customMethods = new ConcurrentHashMap<>();

    /**
     * 构建NoarkIOC容器
     *
     * @param packager 被扫描的业务目录
     */
    public NoarkIoc(String packager) {
        this(null, packager);
    }

    /**
     * 构建NoarkIOC容器
     *
     * @param packager 被扫描的业务目录
     * @param profile  环境配置
     */
    public NoarkIoc(String profile, String packager) {
        logger.debug("init ioc, packager={}", packager);
        IocHolder.setIoc(this);

        // 自动注入的实现也交给他去处理...
        IocLoader loader = new IocLoader(profile, packager);

        try (IocMaking making = new IocMaking(loader)) {
            // 优先构建Configuration里的显示申明的Bean.
            this.finishBeanInitialization(making, loader.getConfigurations());

            // 完成其他Bean的初始化和依赖注入的关系
            this.finishBeanInitialization(making, loader.getBeans().values());

            // 最后还有一些静态属性注入.
            this.finishBeanInitialization(making, loader.getStaticComponents());
        }

        // 完成分析Bean的功能用途
        this.finishBeanAnalysis(loader);
    }

    /**
     * 分析对象，处理并提取到对应的缓存区.
     *
     * @param loader IOC加载
     */
    private void finishBeanAnalysis(IocLoader loader) {
        loader.getBeans().forEach((k, v) -> v.doAnalysisFunction(this));

        // 事件管理类中的事件监听扩展处理并排序
        EventMethodManager.getInstance().listenerExtend().sort();
        // 对自定义的注解进行排序.
        customMethods.values().forEach(v -> v.sort(Comparator.comparingInt(MethodWrapper::getOrder)));

        this.singletons.putAll(loader.getBeans().values().stream().collect(Collectors.toMap(DefaultBeanDefinition::getBeanClass, DefaultBeanDefinition::getSingle)));
    }

    /**
     * 初始化和依赖注入的关系
     */
    private void finishBeanInitialization(IocMaking making, Collection<? extends BeanDefinition> beans) {
        // 转化器先初始化
        singletons.putAll(ConvertManager.getInstance().getBaseConverterMap());
        // Bean初始化
        beans.forEach(bean -> bean.injection(making));
    }

    @Override
    public <T> T get(Class<T> klass) {
        return klass.cast(singletons.get(klass));
    }

    /**
     * 调用自定义注解方法.
     *
     * @param klass 注解类
     */
    public void invokeCustomAnnotationMethod(Class<? extends Annotation> klass) {
        customMethods.getOrDefault(klass, Collections.emptyList()).forEach(MethodWrapper::invoke);
    }

    /**
     * 为有注解的方法添加方法执行入口.
     *
     * @param klass 注解类
     * @param mw    方法执行对象
     */
    public void addCustomMethod(Class<? extends Annotation> klass, MethodWrapper mw) {
        customMethods.computeIfAbsent(klass, key -> new ArrayList<>()).add(mw);
    }

    public void destroy() {
        invokeCustomAnnotationMethod(PreDestroy.class);
    }
}