package com.company.game;

import xyz.noark.core.annotation.ControllerAdvice;
import xyz.noark.core.annotation.ExceptionHandler;
import xyz.noark.core.exception.ErrorMsgException;
import xyz.noark.core.network.NetworkProtocol;

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

    @ExceptionHandler(ErrorMsgException.class)
    public NetworkProtocol handleErrorMessagesException(ErrorMsgException e) {
        System.out.println("handleErrorMessagesException" + e.getMessage());

        return new NetworkProtocol(1, "");
    }
}
