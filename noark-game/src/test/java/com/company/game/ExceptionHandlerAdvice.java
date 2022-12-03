package com.company.game;

import xyz.noark.core.annotation.ControllerAdvice;
import xyz.noark.core.annotation.ExceptionHandler;

/**
 * 异常处理器
 *
 * @author 小流氓[176543888@qq.com]
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        System.out.println("handleException" + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException e) {
        System.out.println("handleRuntimeException" + e.getMessage());
    }

    @ExceptionHandler(ArithmeticException.class)
    public void handleArithmeticException(ArithmeticException e) {
        System.out.println("handleArithmeticException" + e.getMessage());
    }

}
