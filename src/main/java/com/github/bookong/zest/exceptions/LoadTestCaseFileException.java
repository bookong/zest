package com.github.bookong.zest.exceptions;

/**
 * @author jiangxu
 */
public class LoadTestCaseFileException extends Exception {

    public LoadTestCaseFileException(String message, Throwable cause){
        super(message, cause);
    }

    public LoadTestCaseFileException(String message){
        super(message);
    }
}
