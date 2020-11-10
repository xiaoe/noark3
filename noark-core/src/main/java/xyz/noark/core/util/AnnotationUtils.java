package xyz.noark.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * 注解工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class AnnotationUtils {
    private AnnotationUtils() {
    }

    /**
     * 获取指定类型的注解或注解上有指定的注解.
     *
     * @param element        注解元素
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 返回标识有指定注解的注解
     */
    public static <A extends Annotation> Annotation getAnnotation(AnnotatedElement element, Class<A> annotationType) {
        A annotation = element.getAnnotation(annotationType);
        if (annotation == null) {
            for (Annotation metaAnn : element.getAnnotations()) {
                annotation = metaAnn.annotationType().getAnnotation(annotationType);
                if (annotation != null) {
                    return metaAnn;
                }
            }
        }
        return annotation;
    }
}
