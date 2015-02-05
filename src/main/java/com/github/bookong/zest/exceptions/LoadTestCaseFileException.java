package com.github.bookong.zest.exceptions;

/**
 * @author jiangxu
 *
 */
public class LoadTestCaseFileException extends RuntimeException {
	private static final long serialVersionUID = 7794859278880687306L;

	public LoadTestCaseFileException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public LoadTestCaseFileException(String message) {
        super(message);
    }
}

