package org.hacker.exception;

public class ApiException extends RuntimeException {
	private static final long serialVersionUID = -6656994484983715966L;

	public ApiException() {}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(Throwable cause) {
		super(cause);
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}