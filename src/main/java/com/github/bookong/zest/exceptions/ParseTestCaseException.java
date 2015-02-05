package com.github.bookong.zest.exceptions;


/**
 * @author jiangxu
 *
 */
public class ParseTestCaseException extends RuntimeException {
	private static final long serialVersionUID = -7131640034852683461L;

	public ParseTestCaseException(String message) {
        super(message);
    }
	
	public ParseTestCaseException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
