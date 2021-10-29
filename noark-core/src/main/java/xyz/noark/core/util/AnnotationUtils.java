package xyz.noark.core.util;

import xyz.noark.core.annotation.Profile;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

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

    /**
     * 过滤掉非当前环境的配置
     *
     * @param profile    注解配置
     * @param profileStr 配置环境
     * @return 如果需要过滤掉返回true
     */
    public static boolean filterProfile(Profile profile, String profileStr) {
        // @Profile 指定环境，没有配置不过滤
        if (profile == null) {
            return false;
        }

        // 只要有一个是当前的配置的，那就不要过滤啦
        for (String test : profile.value()) {
            if (Objects.equals(test, profileStr)) {
                return false;
            }
        }

        // 一个也没有命令中，那就要过滤掉
        return true;
    }
}
