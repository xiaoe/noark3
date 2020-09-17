package xyz.noark.core.annotation.controller;

import xyz.noark.core.util.StringUtils;

import java.lang.annotation.*;

/**
 * 注解RequestMapping的作用将HTTP请求映射到指定处理方法.
 * <p>
 * 这里区别Spring的只能在方法上定义，类上面没有实现，感觉没有必要
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

    /**
     * 允许的请求方式，如果没有指定默认全部允许.
     * <p>
     * GET、POST、PUT、DELETE等；
     * </p>
     *
     * @return 允许的请求方式
     */
    RequestMethod[] method() default {};

    /**
     * 指定串行执行的队列ID参数的名称.
     * <p>
     * 这里的队列ID值是指HTTP请求参数中名称，如果找到名称就执行串行化，没有找到则放弃串行执行
     * </p>
     *
     * @return 串行执行的队列ID参数的名称
     */
    String queueId() default StringUtils.EMPTY;
}