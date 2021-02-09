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
