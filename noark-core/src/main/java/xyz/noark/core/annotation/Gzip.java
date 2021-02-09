package xyz.noark.core.annotation;

import java.lang.annotation.*;

/**
 * Gzip压缩功能.
 *
 * @author 小流氓[176543888@qq.com]
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gzip {

    /**
     * 只有当数据长度大于配置阀值时，才会开始压缩
     *
     * @return 开始压缩阀值，默认512
     */
    int threshold() default 512;
}
