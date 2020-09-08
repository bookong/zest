/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * Custom exception
 * 
 * @author Jiang Xu
 */
public class ZestException extends RuntimeException {

    /**
     * Constructs a new instance with the specified detail message.
     * 
     * @param message
     *          The detail message.
     */
    public ZestException(String message){
        super(message);
    }

    /**
     * Constructs a new instance with the specified detail message and cause.<br>
     * The {@code message} and {@code cause.getMessage()} will be connected together
     *
     * @param message
     *          The detail message.
     * @param cause
     *          The cause exception.
     */
    public ZestException(String message, Throwable cause){
        super(StringUtils.isBlank(cause.getMessage()) ? message : message.concat("\n").concat(cause.getMessage()),
              cause);
    }

    /**
     * Constructs a new instance with the specified cause.
     *
     * @param cause
     *          The cause exception.
     */
    public ZestException(Throwable cause){
        super(cause.getMessage(), cause);
    }

}
