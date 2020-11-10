package xyz.noark.core.network;

import java.lang.annotation.Annotation;

/**
 * 处理器方法.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public interface HandlerMethod {

    /**
     * 获取这个处理器方法上的指定注解
     *
     * @param annotationClass 注解类
     * @param <T>             注解类型
     * @return 如果有此类型的注解则返回，如果没有就返回null
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);
}
