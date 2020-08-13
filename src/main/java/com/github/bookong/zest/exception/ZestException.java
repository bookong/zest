package com.github.bookong.zest.exception;

/**
 * @author Jiang Xu
 */
public class ZestException extends RuntimeException {

    public ZestException(){
        super();
    }

    public ZestException(String message){
        super(message);
    }

    public ZestException(String message, Throwable cause){
        super(message.concat("\n").concat(cause.getMessage()), cause);
    }
}
