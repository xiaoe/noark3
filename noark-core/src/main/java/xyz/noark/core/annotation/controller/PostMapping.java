package xyz.noark.core.annotation.controller;

import xyz.noark.core.util.StringUtils;

import java.lang.annotation.*;

/**
 * 注解PostMapping用于将Post请求映射到指定处理方法.
 * <p></p>
 * 这可以理解为一个组合注解，是@RequestMapping(method = RequestMethod.POST)的缩写
 *
 * @author 小流氓[176543888@qq.com]
 * @see RequestMapping
 * @since 3.4
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostMapping {

    /**
     * 映射路径.
     *
     * @return 映射路径
     */
    String path();

    /**
     * 指定串型执行的队列ID参数的名称.
     * <p>
     * 这里的队列ID值是指HTTP请求参数中名称，如果找到名称就执行串型化，没有找到则放弃串型执行
     * </p>
     *
     * @return 串型执行的队列ID参数的名称
     */
    String queueId() default StringUtils.EMPTY;
}