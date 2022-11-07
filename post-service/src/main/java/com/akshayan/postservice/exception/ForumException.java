package com.akshayan.postservice.exception;

public class ForumException extends RuntimeException {
    public ForumException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public ForumException(String exMessage) {
        super(exMessage);
    }
}
