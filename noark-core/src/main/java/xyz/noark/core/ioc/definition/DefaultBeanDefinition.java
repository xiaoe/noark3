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

import xyz.noark.core.annotation.*;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.*;
import xyz.noark.core.ioc.definition.field.DefaultFieldDefinition;
import xyz.noark.core.ioc.definition.field.ListFieldDefinition;
import xyz.noark.core.ioc.definition.field.MapFieldDefinition;
import xyz.noark.core.ioc.definition.field.ValueFieldDefinition;
import xyz.noark.core.ioc.definition.method.BeanMethodDefinition;
import xyz.noark.core.ioc.definition.method.SimpleMethodDefinition;
import xyz.noark.core.ioc.wrap.method.BaseMethodWrapper;
import xyz.noark.core.util.*;
import xyz.noark.reflectasm.MethodAccess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 默认的Bean定义描述类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class DefaultBeanDefinition implements BeanDefinition {
    private static final Set<Class<?>> IGNORE_ANNOTATION_BY_METHODS = new HashSet<>();

    static {
        IGNORE_ANNOTATION_BY_METHODS.add(Deprecated.class);
        IGNORE_ANNOTATION_BY_METHODS.add(Primary.class);
    }

    /**
     * 缓存那个单例对象
     */
    protected final Object single;
    protected final MethodAccess methodAccess;
    protected final HashMap<Class<? extends Annotation>, List<MethodDefinition>> customMethods = new HashMap<>();
    protected final String profileStr;

    private final Class<?> beanClass;

    private String beanName = StringUtils.EMPTY;

    private Annotation annotation;
    private Class<? extends Annotation> annotationType;

    /**
     * 是否标有@Primary注解
     */
    private boolean primary;
    /**
     * 注入排序值
     */
    private final int order;
    /**
     * 是否标有@ConditionalOnMissingBean注解
     */
    private boolean conditionalOnMissingBean;

    /**
     * 所有需要注入的属性
     */
    private final ArrayList<FieldDefinition> autowiredFields = new ArrayList<>();

    public DefaultBeanDefinition(String profileStr, Class<?> klass) {
        this(profileStr, klass, ClassUtils.newInstance(klass));
    }

    public DefaultBeanDefinition(String profileStr, BeanMethodDefinition bmd, Object object) {
        this(profileStr, object.getClass(), object);
        this.beanName = bmd.getBeanName();
        // 或一下，有一个优先，他就是优先的方案
        this.primary |= bmd.getMethod().isAnnotationPresent(Primary.class);
        this.conditionalOnMissingBean |= bmd.getMethod().isAnnotationPresent(ConditionalOnMissingBean.class);
    }

    public DefaultBeanDefinition(String profileStr, Class<?> klass, Object object) {
        this.profileStr = profileStr;
        this.single = object;
        this.beanClass = klass;
        this.methodAccess = MethodAccess.get(beanClass);

        Order order = beanClass.getAnnotation(Order.class);
        this.order = order == null ? Integer.MAX_VALUE : order.value();
        this.primary = beanClass.isAnnotationPresent(Primary.class);
        this.conditionalOnMissingBean = beanClass.isAnnotationPresent(ConditionalOnMissingBean.class);
    }

    public DefaultBeanDefinition(String profileStr, Class<?> klass, Annotation annotation, Class<? extends Annotation> annotationType) {
        this(profileStr, klass, ClassUtils.newInstance(klass));
        this.annotation = annotation;
        this.annotationType = annotationType;
    }

    public DefaultBeanDefinition init() {
        this.analysisField();
        this.analysisMethod();
        return this;
    }


    /**
     * 获取Bean的唯一ID.
     * <p>只有@Component才会有这个配置</p>
     *
     * @return Bean的唯一ID
     */
    public int[] getIds() {
        if (annotationType == Component.class) {
            return ((Component) annotation).id();
        }
        throw new UnrealizedException("亲，只有@Component才会有这个配置，用于Map的注入");
    }

    @Override
    public String[] getNames() {
        if (annotationType == Component.class) {
            return ((Component) annotation).name();
        }

        // 有指定名称使用指定名称
        if (StringUtils.isNotEmpty(beanName)) {
            return new String[]{beanName};
        }

        return new String[]{beanClass.getName()};
    }

    /**
     * 获取这个Bean的单例缓存对象.
     *
     * @return 实例对象.
     */
    public Object getSingle() {
        return single;
    }

    /**
     * 获取当前Bean的Class
     *
     * @return 当前Bean的Class
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * 注入排序值.
     *
     * @return 排序值
     */
    public int getOrder() {
        return order;
    }

    private void analysisMethod() {
        List<Method> methods = MethodUtils.getAllMethod(beanClass);
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            // 没有注解的忽略掉
            if (ArrayUtils.isNotEmpty(annotations)) {

                // @Profile 指定环境
                if (AnnotationUtils.filterProfile(method.getAnnotation(Profile.class), profileStr)) {
                    continue;
                }

                for (Annotation annotation : annotations) {
                    final Class<? extends Annotation> annotationType = annotation.annotationType();
                    // 忽略一些系统警告类的注解
                    if (IGNORE_ANNOTATION_BY_METHODS.contains(annotationType)) {
                        continue;
                    }
                    this.analysisMethodByAnnotation(annotationType, annotation, method);
                }
            }
        }
    }

    /**
     * 分析方法上的注解.
     *
     * @param annotationType 注解类型
     * @param annotation     注解对象
     * @param method         方法体
     */
    protected void analysisMethodByAnnotation(Class<? extends Annotation> annotationType, Annotation annotation, Method method) {
        customMethods.computeIfAbsent(annotationType, key -> new ArrayList<>(64)).add(new SimpleMethodDefinition(methodAccess, method));
    }

    private void analysisField() {
        FieldUtils.getAllField(beanClass).stream().filter(v -> v.isAnnotationPresent(Autowired.class) || v.isAnnotationPresent(Value.class)).forEach(this::analysisAutowiredOrValue);
    }

    /**
     * 分析注入的类型
     */
    private void analysisAutowiredOrValue(Field field) {
        final Class<?> fieldClass = field.getType();

        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            Autowired autowired = field.getAnnotation(Autowired.class);

            // List类型的注入需求
            if (fieldClass == List.class) {
                autowiredFields.add(new ListFieldDefinition(field, autowired.required()));
            }

            // Map类型的注入需求
            else if (fieldClass == Map.class) {
                autowiredFields.add(new MapFieldDefinition(field, autowired.required()));
            }

            // 其他就当普通Bean处理...
            else {
                autowiredFields.add(new DefaultFieldDefinition(field, autowired.required()));
            }
        }
        // @Value注入配置属性.
        else {
            autowiredFields.add(new ValueFieldDefinition(beanClass, field, value.value(), value.autoRefreshed()));
        }
    }

    @Override
    public void injection(IocMaking making) {
        this.autowiredFields.forEach((v) -> v.injection(single, making));
    }

    /**
     * 分析此类的功能用途.
     *
     * @param ioc 容器
     */
    public void doAnalysisFunction(NoarkIoc ioc) {
        // 有自定义的注解需要送回来IOC容器中.
        customMethods.forEach((k, list) -> list.forEach(v -> ioc.addCustomMethod(k, new BaseMethodWrapper(single, v.getMethodAccess(), v.getMethodIndex(), v.getOrder()))));
    }

    public boolean isPrimary() {
        return primary;
    }

    public boolean isConditionalOnMissingBean() {
        return conditionalOnMissingBean;
    }
}