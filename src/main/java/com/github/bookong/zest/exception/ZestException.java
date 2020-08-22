package com.github.bookong.zest.exception;

/**
 * @author Jiang Xu
 */
public class ZestException extends RuntimeException {

    public ZestException(String message){
        super(message);
    }

    public ZestException(String message, Throwable cause){
        super(cause.getMessage() == null ? message : message.concat("\n").concat(cause.getMessage()), cause);
    }

    public ZestException(Throwable cause){
        super(cause.getMessage(), cause);
    }
}
