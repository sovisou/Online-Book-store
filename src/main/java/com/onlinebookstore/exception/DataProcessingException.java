package com.onlinebookstore.exception;

public class DataProcessingException extends RuntimeException {
    public DataProcessingException(String message, Exception e) {
        super(message, e);
    }
}
