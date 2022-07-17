package com.restapi.advice.exception;

public class EmailSignupFailedException extends RuntimeException {
    public EmailSignupFailedException() {
        super();
    }

    public EmailSignupFailedException(String message) {
        super(message);
    }

    public EmailSignupFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
