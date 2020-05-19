package xyz.noark.core.annotation.controller;

import xyz.noark.core.util.StringUtils;

import java.lang.annotation.*;

/**
 * RequestMapping注解的作用将HTTP请求映射到指定处理方法.
 * <p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    /**
     * 映射路径.
     *
     * @return 映射路径
     */
    String path();

    RequestMethod[] method() default {};

    String queueId() default StringUtils.EMPTY;
}