package com.openalpr.jni.exceptions;

public class ProcessingException extends RuntimeException {

    public ProcessingException() {
    }

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
