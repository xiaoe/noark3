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
}
