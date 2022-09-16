/*
 * Copyright © 2018 huiyunetwork.com All Rights Reserved.
 *
 * 感谢您加入辉娱网络，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 *
 * 		http://www.huiyunetwork.com/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归辉娱网络所有，我们将保留所有权利;
 * 4、凡侵犯辉娱网络相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.annotation;

import xyz.noark.core.util.StringUtils;

import java.lang.annotation.*;

/**
 * 配置条件，取决于所配置的类是否生效
 *
 * @author 小流氓[176543888@qq.com]
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ConditionalOnProperty {

    /**
     * 指定配置项的前缀.
     * <p>
     * 比如 noark.config.enabled=true 中的 noark.config <br>
     * 可以为空，如果指定了，只会读取前缀为 noark.config 的配置项
     *
     * @return 配置项的前缀
     */
    String prefix() default StringUtils.EMPTY;

    /**
     * 指定配置项的属性名称.
     * <p>
     * 比如 noark.config.enabled=true 中的 enabled<br>
     * 必选项，enabled 前面的叫前缀，enabled就是名称
     *
     * @return 配置项的属性名称
     */
    String name();

    /**
     * 指定配置项的属性名称对应的值.
     * <p>
     * 比如 noark.config.enabled=true 中的 true<br>
     * 默认为空 {@link StringUtils#EMPTY}
     *
     * @return 属性名称对应的值
     */
    String havingValue() default StringUtils.EMPTY;

    /**
     * 如果缺少配置项时是否生效，默认不生效
     * <p>
     * 此属性前提是缺少配置项时
     *
     * @return 缺少配置时是否生效
     */
    boolean matchIfMissing() default false;
}