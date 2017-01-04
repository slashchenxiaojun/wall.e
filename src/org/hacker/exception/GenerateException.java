package org.hacker.exception;

public class GenerateException extends RuntimeException {
	private static final long serialVersionUID = -6656994484983715966L;

	public GenerateException() {}

	public GenerateException(String message) {
		super(message);
	}

	public GenerateException(Throwable cause) {
		super(cause);
	}

	public GenerateException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerateException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}