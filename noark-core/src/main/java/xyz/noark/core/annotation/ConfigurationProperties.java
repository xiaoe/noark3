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
package xyz.noark.core.annotation;

import xyz.noark.core.util.StringUtils;

import java.lang.annotation.*;

/**
 * 配置属性
 *
 * @author 小流氓[176543888@qq.com]
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationProperties {
    /**
     * 可绑定到此对象的有效属性的名称前缀。
     * <p>等同于{@link #prefix()}.</p>
     *
     * @return 要绑定的属性的名称前缀
     */
    //@AliasFor("prefix")
    String value() default StringUtils.EMPTY;

    /**
     * 可绑定到此对象的有效属性的名称前缀。
     * <p>等同于{@link #value()}.</p>
     *
     * @return 要绑定的属性的名称前缀
     */
    //@AliasFor("value")
    String prefix() default StringUtils.EMPTY;

    /**
     * 是否忽略那些绑定失败属性的状态
     *
     * @return 忽略状态（默认为false）
     */
    boolean ignoreInvalidFields() default false;

    /**
     * 是否忽略未知绑定属性配置的状态
     *
     * @return 忽略状态（默认为true）
     */
    boolean ignoreUnknownFields() default true;
}
