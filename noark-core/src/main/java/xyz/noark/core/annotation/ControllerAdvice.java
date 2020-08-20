package xyz.noark.core.annotation;

import java.lang.annotation.*;

/**
 * ControllerAdvice注解可用于定义@ExceptionHandler。
 *
 * @author 小流氓[176543888@qq.com]
 */
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerAdvice {
}