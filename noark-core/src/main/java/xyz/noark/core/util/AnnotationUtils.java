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
