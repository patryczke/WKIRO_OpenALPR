package com.openalpr.jni.exceptions;

public class TaskInterruptedException extends RuntimeException {

    public TaskInterruptedException() {}

    public TaskInterruptedException(String message) {
        super(message);
    }

    public TaskInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

}
