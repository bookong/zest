package com.github.bookong.zest.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Jiang Xu
 */
public class ZestException extends RuntimeException {

    public ZestException(String message){
        super(message);
    }

    public ZestException(String message, Throwable cause){
        super(StringUtils.isBlank(cause.getMessage()) ? message : message.concat("\n").concat(cause.getMessage()),
              cause);
    }

    public ZestException(Throwable cause){
        super(cause.getMessage(), cause);
    }

}
