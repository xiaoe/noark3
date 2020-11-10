package xyz.noark.core.annotation.controller;

import java.lang.annotation.*;

/**
 * IP白名单注解.
 * <p>主要用于配置HTTP访问白名单功能</p>
 * 注：需要激活IpIntercept，不然这个注解不启作用哈.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IpAllowList {

    /**
     * 配置文件中的IP白名单的配置Key。
     *
     * @return IP白名单的配置Key
     */
    String value();
}
